package org.hogel.bookscan;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public interface Connector {
    Connection connect(String url);

    Document execute(Connection connection) throws IOException;

    Map<String, String> getCookies();

    void putCookies(Map<String, String> cookies);

    void clearCookies();
}
