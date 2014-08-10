package org.hogel.bookscan.exception;

import org.hogel.bookscan.BookscanException;

import java.io.IOException;

public class BookscanNetworkException extends BookscanException {
    public BookscanNetworkException(IOException e) {
        super(e);
    }
}
