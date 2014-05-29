package org.hogel.bookscan;

import org.jsoup.nodes.Document;

public class BookscanException extends Exception {
    private final Document document;

    public BookscanException(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
}
