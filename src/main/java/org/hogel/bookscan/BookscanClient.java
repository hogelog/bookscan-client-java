package org.hogel.bookscan;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hogel.bookscan.listener.*;
import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;
import org.hogel.bookscan.model.OptimizingBook;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookscanClient {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final Connector connector;

    public BookscanClient() {
        this(new BasicConnector());
    }

    public BookscanClient(Connector connector) {
        this.connector = connector;
    }

    public Map<String, String> getCookies() {
        return connector.getCookies();
    }

    public boolean isLogin() {
        Map<String, String> cookies = connector.getCookies();
        return cookies.containsKey("email") && cookies.containsKey("password");
    }

    public void login(String email, String password, LoginListener listener) {
        Connection connection = connector.connect(Constants.URL_LOGIN)
            .method(Connection.Method.POST)
            .data("email", email)
            .data("password", password);
        try {
            Document document = connector.execute(connection);
            if (Constants.URL_LOGIN.equals(document.location())) {
                listener.onError(new BookscanException(document));
            } else {
                listener.onSuccess();
            }
        } catch (IOException e) {
            listener.onError(e);
        }
    }

    public void fetchBooks(FetchBooksListener listener) {
        Connection connection = connector.connect(Constants.URL_MYPAGE).method(Connection.Method.GET);
        try {
            Document document = connector.execute(connection);

            Elements bookLinks = document.select("#sortable_box .showbook");
            List<Book> books = new ArrayList<>();
            for (Element bookLink : bookLinks) {
                String url = Constants.URL_ROOT + bookLink.attr("href");
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), UTF8.name());
                String filename = null, hash = null, digest = null;
                for (NameValuePair param : params) {
                    switch (param.getName()) {
                        case "f":
                            filename = param.getValue();
                            break;
                        case "h":
                            hash = param.getValue();
                            break;
                        case "d":
                            digest = param.getValue();
                            break;
                    }
                }
                if (filename == null || hash == null || digest == null) {
                    continue;
                }
                Elements imgElement = bookLink.select("img");
                Book book;
                if (imgElement.size() == 0) {
                    book = new Book(filename, hash, digest, null);
                } else {
                    book = new Book(filename, hash, digest, imgElement.first().attr("src"));
                }
                books.add(book);
            }
            listener.onSuccess(books);
        } catch (IOException | URISyntaxException e) {
            listener.onError(e);
        }
    }

    public void fetchOptimizedBooks(FetchOptimizedBooksListener listener) {
        Connection connection = connector.connect(Constants.URL_OPTIMIZED_BOOKS).method(Connection.Method.GET);
        try {
            Document document = connector.execute(connection);

            Elements bookLinks = document.select("a.download");
            List<OptimizedBook> books = new ArrayList<>();
            for (Element bookLink : bookLinks) {
                String url = Constants.URL_ROOT + bookLink.attr("href");
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), UTF8.name());
                String filename = null, digest = null;
                for (NameValuePair param : params) {
                    switch (param.getName()) {
                        case "f":
                            filename = param.getValue();
                            break;
                        case "d":
                            digest = param.getValue();
                            break;
                    }
                }
                if (filename == null || digest == null) {
                    continue;
                }
                books.add(new OptimizedBook(filename, digest));
            }
            listener.onSuccess(books);
        } catch (IOException | URISyntaxException e) {
            listener.onError(e);
        }
    }

    public void requestBookOptimize(Book book, OptimizeOption option, RequestBookOptimizeListener listener) {
        String optimizeUrl = book.createOptimizeUrl();

        final List<OptimizeOption.Type> types = option.getTypes();
        final List<OptimizeOption.Flag> flags = option.getFlags();
        if (types.size() == 0) {
            listener.onError(new Exception("Optimize option is not specified"));
            return;
        }

        try {
            List<Connection.KeyVal> hiddenOptions = fetchHiddenOptimizeOptions(optimizeUrl);

            Connection connection = connector.connect(optimizeUrl).method(Connection.Method.POST);

            for (Connection.KeyVal hiddenOption : hiddenOptions) {
                connection = connection.data(hiddenOption.key(), hiddenOption.value());
            }

            for (OptimizeOption.Type type : types) {
                connection = connection.data(OptimizeOption.OPTIMIZE_TYPE_NAME, type.getValue());
            }

            for (OptimizeOption.Flag flag : flags) {
                connection = connection.data(flag.getInputName(), flag.getValue());
            }

            Document document = connector.execute(connection);
            if (book.createOptimizedUrl().equals(document.location())) {
                listener.onSuccess();
            } else {
                listener.onError(new BookscanException(document));
            }
        } catch (IOException e) {
            listener.onError(e);
        }
    }

    private List<Connection.KeyVal> fetchHiddenOptimizeOptions(String optimizeUrl) throws IOException {
        Connection connection = connector.connect(optimizeUrl).method(Connection.Method.GET);
        Document document = connector.execute(connection);
        Elements inputs = document.select("input");

        final List<Connection.KeyVal> keyVals = new ArrayList<>();

        for (Element input : inputs) {
            if (input.attr("type").equals("hidden")) {
                final String name = input.attr("name");
                final String value = input.attr("value");
                keyVals.add(HttpConnection.KeyVal.create(name, value));
            }
        }
        
        return keyVals;
    }

    private static final Pattern OPTIMIZING_BOOK_PATTERN =
        Pattern.compile("(.+\\.pdf)\\s*チューニングタイプ:(.+)\\s*チューニング依頼日時:(\\d+年\\d+月\\d+日 \\d+:\\d+)\\s*(.+)\\s+優先度:");
    public void fetchOptimizingBooks(FetchOptimizingBooksListener listener) {
        Connection connection = connector.connect(Constants.URL_OPTIMIZING_BOOKS).method(Connection.Method.GET);
        try {
            Document document = connector.execute(connection);

            Elements typeLabels = document.getElementsContainingOwnText("チューニングタイプ:");
            List<OptimizingBook> books = new ArrayList<>();
            for (Element typeLabel : typeLabels) {
                Element container = typeLabel.parent();
                String text = container.text();

                Matcher matcher = OPTIMIZING_BOOK_PATTERN.matcher(text);
                if (!matcher.find()) {
                    continue;
                }

                String file = matcher.group(1).trim();
                String type = matcher.group(2).trim();
                String requestedAt = matcher.group(3).trim();
                String status = matcher.group(4).trim();
                OptimizingBook book = new OptimizingBook(file, type, requestedAt, status);
                books.add(book);
            }
            listener.onSuccess(books);
        } catch (IOException e) {
            listener.onError(e);
        }
    }
}
