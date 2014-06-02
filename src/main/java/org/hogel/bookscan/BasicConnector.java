package org.hogel.bookscan;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class BasicConnector implements Connector {
    private final Map<String, String> cookies = new TreeMap<>();

    @Override
    public Connection connect(String url) {
        return Jsoup.connect(url).cookies(cookies);
    }

    @Override
    public Document execute(Connection connection) throws IOException {
        Connection.Response response = connection.execute();
        cookies.putAll(response.cookies());
        return response.parse();
    }

    @Override
    public Map<String, String> getCookies() {
        return cookies;
    }

    @Override
    public void putCookies(Map<String, String> cookies) {
        cookies.putAll(cookies);
    }
}
