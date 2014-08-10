package org.hogel.bookscan.exception;

import java.io.IOException;

public class BookscanNetworkException extends BookscanException {
    public BookscanNetworkException(IOException e) {
        super(e);
    }
}
