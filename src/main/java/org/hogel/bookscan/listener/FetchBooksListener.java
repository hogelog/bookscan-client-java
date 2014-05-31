package org.hogel.bookscan.listener;

import org.hogel.bookscan.model.Book;

import java.util.List;

public interface FetchBooksListener {
    void onSuccess(List<Book> books);

    void onError(Exception e);
}
