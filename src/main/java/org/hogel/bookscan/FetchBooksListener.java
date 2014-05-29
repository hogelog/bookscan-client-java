package org.hogel.bookscan;

import org.hogel.bookscan.models.Book;

import java.util.List;

public interface FetchBooksListener {
    void onSuccess(List<Book> books);

    void onError(Exception e);
}
