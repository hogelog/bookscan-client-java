package org.hogel.bookscan.listener;

import org.hogel.bookscan.model.OptimizingBook;

import java.util.List;

public interface FetchOptimizingBooksListener {
    void onSuccess(List<OptimizingBook> books);

    void onError(Exception e);
}
