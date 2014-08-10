package org.hogel.bookscan.reqeust;

import org.hogel.bookscan.exception.BookscanException;
import org.hogel.bookscan.Connector;
import org.hogel.bookscan.exception.BookscanNetworkException;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public abstract class Request<T> {
    private final ExecutorService executorService;
    private final Connector connector;
    private final Connection connection;
    private RequestListener<T> listener;
    private RequestErrorListener requestErrorListener;

    public Request(ExecutorService executorService, Connector connector, Connection connection) {
        this.executorService = executorService;
        this.connector = connector;
        this.connection = connection;
    }

    public abstract T get() throws BookscanException;

    public Document getDocument() throws BookscanNetworkException {
        try {
            return connector.execute(connection);
        } catch (IOException e) {
            throw new BookscanNetworkException(e);
        }
    }

    public Request<T> listener(RequestListener<T> listener) {
        this.listener = listener;
        return this;
    }

    public Request<T> error(RequestErrorListener requestErrorListener) {
        this.requestErrorListener = requestErrorListener;
        return this;
    }

    public Request<T> timeout(int timeout) {
        connection.timeout(timeout);
        return this;
    }

    public void execute() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    T result = get();
                    if (listener != null) {
                        listener.success(result);
                    }
                } catch (BookscanException e) {
                    if (requestErrorListener != null) {
                        requestErrorListener.error(e);
                    }
                }
            }
        });
    }
}
