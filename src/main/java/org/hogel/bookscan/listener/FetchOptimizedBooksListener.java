package org.hogel.bookscan.listener;

import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public interface FetchOptimizedBooksListener {
    void onSuccess(List<OptimizedBook> optimizedBooks);

    void onError(Exception e);
}
