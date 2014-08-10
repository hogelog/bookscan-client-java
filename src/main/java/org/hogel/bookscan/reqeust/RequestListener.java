package org.hogel.bookscan.reqeust;

public interface RequestListener<T> {
    void success(T result);
}
