package org.hogel.bookscan.listener;

public interface RequestBookOptimizeListener {
    void onSuccess();

    void onError(Exception e);
}
