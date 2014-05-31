package org.hogel.bookscan.model;

import org.hogel.bookscan.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OptimizedBook {
    private String filename;
    private String digest;

    public OptimizedBook() {
    }

    public OptimizedBook(String filename, String digest) {
        this.filename = filename;
        this.digest = digest;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String createDownloadUrl() {
        try {
            StringBuilder builder = new StringBuilder(Constants.URL_DOWNLOAD);
            builder.append("?d=").append(digest);
            builder.append("&f=").append(URLEncoder.encode(filename, "UTF-8"));
            builder.append("&optimize=1");
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
