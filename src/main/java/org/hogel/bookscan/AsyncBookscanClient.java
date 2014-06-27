package org.hogel.bookscan;

import org.hogel.bookscan.listener.*;
import org.hogel.bookscan.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncBookscanClient {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncBookscanClient.class);

    private final BookscanClient bookscanClient;
    private final ExecutorService executorService;

    public AsyncBookscanClient() {
        this(new BasicConnector());
    }

    public AsyncBookscanClient(Connector connector) {
        this(Executors.newSingleThreadExecutor(), connector);
    }

    public AsyncBookscanClient(ExecutorService executorService, Connector connector) {
        bookscanClient = new BookscanClient(connector);
        this.executorService = executorService;
    }

    public boolean isLogin() {
        return bookscanClient.isLogin();
    }

    public Map<String, String> getCookies() {
        return bookscanClient.getCookies();
    }

    public void putCookies(Map<String, String> cookies) {
        bookscanClient.putCookies(cookies);
    }

    public void clearCookies() {
        bookscanClient.clearCookies();
    }

    public Future<?> login(final String email, final String password, final LoginListener listener) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    bookscanClient.login(email, password);
                    listener.onSuccess();
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        });
    }

    public Future<?> fetchBooks(final FetchBooksListener listener) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onSuccess(bookscanClient.fetchBooks());
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        });
    }

    public Future<?> fetchOptimizedBooks(final FetchOptimizedBooksListener listener) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onSuccess(bookscanClient.fetchOptimizedBooks());
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        });
    }

    public Future<?> requestBookOptimize(final Book book, final OptimizeOption option, final RequestBookOptimizeListener listener) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    bookscanClient.requestBookOptimize(book, option);
                    listener.onSuccess();
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        });
    }

    public Future<?> fetchOptimizingBooks(final FetchOptimizingBooksListener listener) {
        return executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.onSuccess(bookscanClient.fetchOptimizingBooks());
                } catch (Exception e) {
                    listener.onError(e);
                }
            }
        });
    }
}
