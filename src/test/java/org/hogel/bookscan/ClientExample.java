package org.hogel.bookscan;

import org.hogel.bookscan.listener.FetchBooksAdapter;
import org.hogel.bookscan.listener.LoginAdapter;
import org.hogel.bookscan.models.Book;

import java.util.List;

public class ClientExample {
    public static void main(String[] args) {
        String email = args[0];
        String pass = args[1];

        final BookscanClient client = new BookscanClient();
        client.login(email, pass, new LoginAdapter() {
            @Override
            public void onSuccess() {
                client.fetchBooks(new FetchBooksAdapter() {
                    @Override
                    public void onSuccess(List<Book> books) {
                        System.out.println("fetch books: " + books.size());
                        for (Book book : books) {
                            System.out.println(book.getFilename());
                        }
                    }
                });
            }
        });
    }
}
