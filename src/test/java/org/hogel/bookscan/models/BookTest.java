package org.hogel.bookscan.models;

import org.hogel.bookscan.Constants;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BookTest {
    @Test
    public void createDownloadUrl() {
        Book book = new Book("あ.pdf", "hash", "digest", null);
        assertThat(book.createDownloadUrl(), is(Constants.URL_DOWNLOAD + "?d=digest&f=%E3%81%82.pdf"));
    }
}