package org.hogel.bookscan.model;

import org.hogel.bookscan.Constants;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OptimizedBookTest {
    @Test
    public void createDownloadUrl() {
        OptimizedBook book = new OptimizedBook("あ.pdf", "digest");
        assertThat(book.createDownloadUrl(), is(Constants.URL_DOWNLOAD + "?d=digest&f=%E3%81%82.pdf&optimize=1"));
    }
}