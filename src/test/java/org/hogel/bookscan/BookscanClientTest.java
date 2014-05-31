package org.hogel.bookscan;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.hogel.bookscan.listener.FetchBooksListener;
import org.hogel.bookscan.listener.LoginListener;
import org.hogel.bookscan.models.Book;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class BookscanClientTest {
    private Connector connector;
    private Connection connection;
    private Connection.Response response;
    private Map<String, String> cookies = new TreeMap<>();

    private BookscanClient client;

    private String getResourceString(String resourceName) throws IOException {
        return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8);
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
    public void loginSuccess() throws IOException {
        Document loginSuccessDocument = new Document(Constants.URL_MYPAGE);
        doReturn(loginSuccessDocument).when(response).parse();

        cookies.put("login", "success");

        LoginListener listener = mock(LoginListener.class);
        client.login("user", "password", listener);

        verify(listener).onSuccess();

        assertThat(connector.getCookies().get("login"), is("success"));
    }

    @Test
    public void loginError() throws IOException {
        Document loginErrorDocument = new Document(Constants.URL_LOGIN);
        doReturn(loginErrorDocument).when(response).parse();

        LoginListener listener = mock(LoginListener.class);
        client.login("user", "password", listener);

        verify(listener).onError(any(Exception.class));
    }

    @Test
    public void fetchBookListSuccess() throws IOException {
        final List<Book> fetchBooks = Lists.newArrayList();
        FetchBooksListener listener = new FetchBooksListener() {
            @Override
            public void onSuccess(List<Book> books) {
                fetchBooks.addAll(books);
            }

            @Override
            public void onError(Exception e) {
            }
        };

        Document booksDocument = Jsoup.parse(getResourceString("/data/books.html"));
        doReturn(booksDocument).when(connector).execute(any(Connection.class));

        client.fetchBooks(listener);

        assertThat(fetchBooks.get(0).getHash(), is("hash1"));
        assertThat(fetchBooks.get(0).getDigest(), is("digest1"));
        assertThat(fetchBooks.get(0).getFilename(), is("filename1"));
        assertThat(fetchBooks.get(0).getImageUrl(), is(nullValue()));

        assertThat(fetchBooks.get(1).getHash(), is("hash2"));
        assertThat(fetchBooks.get(1).getDigest(), is("digest2"));
        assertThat(fetchBooks.get(1).getFilename(), is("filename2"));
        assertThat(fetchBooks.get(1).getImageUrl(), is("http://example.com/hoge.jpg"));
    }
}