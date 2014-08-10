package org.hogel.bookscan.exception;

public class BookscanException extends Exception {
    public BookscanException() {
    }

    public BookscanException(String message) {
        super(message);
    }

    public BookscanException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookscanException(Throwable cause) {
        super(cause);
    }

    public BookscanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
