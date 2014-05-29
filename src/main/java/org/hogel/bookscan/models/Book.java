package org.hogel.bookscan.models;

import org.hogel.bookscan.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Book {
    private String filename;
    private String hash;
    private String digest;

    public Book() {
    }

    public Book(String filename, String hash, String digest) {
        this.filename = filename;
        this.hash = hash;
        this.digest = digest;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getDownloadUrl() {
        try {
            return String.format("%s?d=%s&f=%s", Constants.URL_DOWNLOAD, digest, URLEncoder.encode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
