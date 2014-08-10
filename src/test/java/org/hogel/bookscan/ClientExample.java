package org.hogel.bookscan;

import org.hogel.bookscan.model.Book;
import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public class ClientExample {
    public static void main(String[] args) throws Exception {
        String email = args[0];
        String pass = args[1];

        final BookscanClient client = new BookscanClient();
        client.login(email, pass);

        Thread.sleep(1000);

        List<Book> books = client.fetchBooks().get();
        System.out.println("fetch books: " + books.size());
        for (Book book : books) {
            System.out.println(book.getFilename());
        }

        Thread.sleep(1000);

        List<OptimizedBook> optimizedBooks = client.fetchOptimizedBooks().get();
        System.out.println("fetch optimized books: " + optimizedBooks.size());
        for (OptimizedBook optimizedBook : optimizedBooks) {
            System.out.println(optimizedBook.getFilename());
        }
    }
}
