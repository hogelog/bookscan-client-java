package org.hogel.bookscan;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;
import org.hogel.bookscan.model.OptimizingBook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BookscanClientTest {
    private Connector connector;
    private Connection connection;
    private Connection.Response response;
    private Map<String, String> cookies = new TreeMap<>();

    private BookscanClient client;

    private String getResourceString(String resourceName) throws IOException {
        URL resourceUrl = getClass().getResource(resourceName);
        return Resources.toString(resourceUrl, Charsets.UTF_8);
    }

    @Before
    public void setupClient() throws IOException {
        connection = mock(Connection.class);
        doReturn(connection).when(connection).method(any(Connection.Method.class));
        doReturn(connection).when(connection).data(anyString(), anyString());

        response = mock(Connection.Response.class);
        doReturn(response).when(connection).execute();
        doReturn(cookies).when(response).cookies();

        connector = spy(new BasicConnector());
        doReturn(connection).when(connector).connect(anyString());

        client = new BookscanClient(connector);
    }

    @Test
    public void isLoginSuccess() throws IOException {
        cookies.put("email", "test");
        cookies.put("password", "test");

        doReturn(cookies).when(connector).getCookies();

        assertThat(client.isLogin(), is(true));
    }

    @Test
    public void isLoginFailue() throws IOException {
        cookies.clear();

        doReturn(cookies).when(connector).getCookies();

        assertThat(client.isLogin(), is(false));
    }

    @Test
    public void loginSuccess() throws IOException, BookscanException {
        Document loginSuccessDocument = new Document(Constants.URL_MYPAGE);
        doReturn(loginSuccessDocument).when(response).parse();

        cookies.put("login", "success");

        client.login("user", "password").get();

        assertThat(connector.getCookies().get("login"), is("success"));
    }

    @Test
    public void loginError() throws IOException {
        Document loginErrorDocument = new Document(Constants.URL_LOGIN);
        doReturn(loginErrorDocument).when(response).parse();

        try {
            client.login("user", "password").get();
            assertTrue(false);
        } catch (BookscanException e) {
        }
    }

    @Test
    public void fetchBookListSuccess() throws IOException, BookscanException {
        Document booksDocument = Jsoup.parse(getResourceString("/data/books.html"));
        doReturn(booksDocument).when(connector).execute(any(Connection.class));

        List<Book> fetchBooks = client.fetchBooks().get();

        assertThat(fetchBooks.get(0).getHash(), is("hash1"));
        assertThat(fetchBooks.get(0).getDigest(), is("digest1"));
        assertThat(fetchBooks.get(0).getFilename(), is("filename1"));
        assertThat(fetchBooks.get(0).getImageUrl(), is(nullValue()));

        assertThat(fetchBooks.get(1).getHash(), is("hash2"));
        assertThat(fetchBooks.get(1).getDigest(), is("digest2"));
        assertThat(fetchBooks.get(1).getFilename(), is("filename2"));
        assertThat(fetchBooks.get(1).getImageUrl(), is("http://example.com/hoge.jpg"));
    }

    @Test
    public void fetchOptimizedBookListSuccess() throws IOException, BookscanException {
        Document booksDocument = Jsoup.parse(getResourceString("/data/optimized_books.html"));
        doReturn(booksDocument).when(connector).execute(any(Connection.class));

        List<OptimizedBook> fetchBooks = client.fetchOptimizedBooks().get();

        assertThat(fetchBooks.get(0).getDigest(), is("digest1"));
        assertThat(fetchBooks.get(0).getFilename(), is("filename1"));

        assertThat(fetchBooks.get(1).getDigest(), is("digest2"));
        assertThat(fetchBooks.get(1).getFilename(), is("filename2"));
    }

    @Test
    public void requestBookOptimizeSuccess() throws IOException, BookscanException {
        Book book = new Book("hoge.pdf", "hash", "digest", null);

        Document document = Jsoup.parse(getResourceString("/data/book_optimize.html"), book.createOptimizedUrl());
        doReturn(document).when(connector).execute(any(Connection.class));

        final OptimizeOption option = new OptimizeOption();

        option.addType(OptimizeOption.Type.KINDLEP);
        option.addType(OptimizeOption.Type.ANDROID);

        option.addFlag(OptimizeOption.Flag.COVER);
        option.addFlag(OptimizeOption.Flag.BOLD);

        client.requestBookOptimize(book, option).get();

        verify(connection).data("abc", "12345");
        verify(connection).data("def", "67890");

        verify(connection).data(OptimizeOption.OPTIMIZE_TYPE_NAME, OptimizeOption.Type.KINDLEP.getValue());
        verify(connection).data(OptimizeOption.OPTIMIZE_TYPE_NAME, OptimizeOption.Type.ANDROID.getValue());

        verify(connection).data(OptimizeOption.Flag.COVER.getInputName(), OptimizeOption.Flag.COVER.getValue());
        verify(connection).data(OptimizeOption.Flag.BOLD.getInputName(), OptimizeOption.Flag.BOLD.getValue());
    }

    @Test
    public void fetchHiddenOptimizeOptionsSuccess() throws BookscanException, IOException {
        Book book = new Book("hoge.pdf", "hash", "digest", null);

        String optimizeUrl = book.createOptimizeUrl();
        Document document = Jsoup.parse(getResourceString("/data/book_optimize.html"), optimizeUrl);
        doReturn(document).when(connector).execute(any(Connection.class));

        List<Connection.KeyVal> hiddenOptions = client.fetchHiddenOptimizeOptions(optimizeUrl);

        assertThat(findValue("abc", hiddenOptions), is("12345"));
        assertThat(findValue("def", hiddenOptions), is("67890"));
    }

    private String findValue(String key, List<Connection.KeyVal> options) {
        for (Connection.KeyVal option: options) {
            if (option.key().equals(key)) {
                return option.value();
            }
        }
        return null;
    }

    @Test
    public void fetchOptimizingBookListSuccess() throws IOException, BookscanException {
        Document booksDocument = Jsoup.parse(getResourceString("/data/optimizing_books.html"));
        doReturn(booksDocument).when(connector).execute(any(Connection.class));

        final List<OptimizingBook> fetchBooks = client.fetchOptimizingBooks().get();

        assertThat(fetchBooks.get(0).getFilename(), is("hoge.pdf"));
        assertThat(fetchBooks.get(0).getType(), is("Kindle PaperWhiteチューニング"));
        assertThat(fetchBooks.get(0).getRequestedAt(), is("2000年01月01日 00:00"));
        assertThat(fetchBooks.get(0).getStatus(), is("チューニング開始前"));

        assertThat(fetchBooks.get(1).getFilename(), is("fuga.pdf"));
        assertThat(fetchBooks.get(1).getType(), is("Androidチューニング"));
        assertThat(fetchBooks.get(1).getRequestedAt(), is("2010年01月01日 00:00"));
        assertThat(fetchBooks.get(1).getStatus(), is("チューニング中"));
    }
}
