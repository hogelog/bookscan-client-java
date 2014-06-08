package org.hogel.bookscan;

import com.google.common.collect.Maps;
import org.hogel.bookscan.listener.LoginListener;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AsyncBookscanClientTest {
    private Connector connector;
    private Connection connection;
    private Connection.Response response;
    private Map<String, String> cookies = Maps.newTreeMap();

    private AsyncBookscanClient client;

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

        client = new AsyncBookscanClient(connector);
    }

    @Test
    public void loginSuccess() throws IOException, ExecutionException, InterruptedException {
        Document loginSuccessDocument = new Document(Constants.URL_MYPAGE);
        doReturn(loginSuccessDocument).when(response).parse();

        cookies.put("login", "success");

        LoginListener listener = mock(LoginListener.class);
        client.login("user", "password", listener).get();

        verify(listener).onSuccess();

        assertThat(connector.getCookies().get("login"), is("success"));
    }

    @Test
    public void loginError() throws IOException, ExecutionException, InterruptedException {
        Document loginErrorDocument = new Document(Constants.URL_LOGIN);
        doReturn(loginErrorDocument).when(response).parse();

        LoginListener listener = mock(LoginListener.class);
        client.login("user", "password", listener).get();

        verify(listener).onError(any(Exception.class));
    }
}
