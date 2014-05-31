package org.hogel.bookscan.listener;

import org.hogel.bookscan.model.Book;

import java.util.List;

public class FetchBooksAdapter implements FetchBooksListener {
    @Override
    public void onSuccess(List<Book> books) {
    }

    @Override
    public void onError(Exception e) {
    }
}
