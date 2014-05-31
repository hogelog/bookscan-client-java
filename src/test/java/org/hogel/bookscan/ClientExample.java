package org.hogel.bookscan;

import org.hogel.bookscan.listener.FetchBooksAdapter;
import org.hogel.bookscan.listener.FetchOptimizedBooksAdapter;
import org.hogel.bookscan.listener.LoginAdapter;
import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public class ClientExample {
    public static void main(String[] args) {
        String email = args[0];
        String pass = args[1];

        final BookscanClient client = new BookscanClient();
        client.login(email, pass, new LoginAdapter() {
            @Override
            public void onSuccess() {
                try {
                    Thread.sleep(1000);

                    client.fetchBooks(new FetchBooksAdapter() {
                        @Override
                        public void onSuccess(List<Book> books) {
                            System.out.println("fetch books: " + books.size());
                            for (Book book : books) {
                                System.out.println(book.getFilename());
                            }
                        }
                    });

                    Thread.sleep(1000);

                    client.fetchOptimizedBooks(new FetchOptimizedBooksAdapter() {
                        @Override
                        public void onSuccess(List<OptimizedBook> books) {
                            System.out.println("fetch optimized books: " + books.size());
                            for (OptimizedBook book : books) {
                                System.out.println(book.getFilename());
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
