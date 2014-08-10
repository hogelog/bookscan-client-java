package org.hogel.bookscan.exception;

import org.jsoup.nodes.Document;

public class BookscanResponseException extends BookscanException {
    private final Document document;

    public BookscanResponseException(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
}
