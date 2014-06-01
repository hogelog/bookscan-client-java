package org.hogel.bookscan.listener;

import org.hogel.bookscan.model.OptimizingBook;

import java.util.List;

public class FetchOptimizingBooksAdapter implements FetchOptimizingBooksListener {
    @Override
    public void onSuccess(List<OptimizingBook> books) {
    }

    @Override
    public void onError(Exception e) {
    }
}
