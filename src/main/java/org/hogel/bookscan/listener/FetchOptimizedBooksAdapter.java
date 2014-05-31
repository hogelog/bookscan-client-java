package org.hogel.bookscan.listener;

import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public class FetchOptimizedBooksAdapter implements FetchOptimizedBooksListener {
    @Override
    public void onSuccess(List<OptimizedBook> optimizedBooks) {
    }

    @Override
    public void onError(Exception e) {
    }
}
