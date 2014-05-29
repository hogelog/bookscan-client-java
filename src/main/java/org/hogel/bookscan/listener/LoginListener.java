package org.hogel.bookscan.listener;

public interface LoginListener {
    void onSuccess();

    void onError(Exception e);
}
