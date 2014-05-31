package org.hogel.bookscan;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hogel.bookscan.listener.FetchBooksListener;
import org.hogel.bookscan.listener.FetchOptimizedBooksListener;
import org.hogel.bookscan.listener.LoginListener;
import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;
import org.jsoup.Connection;
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
}
