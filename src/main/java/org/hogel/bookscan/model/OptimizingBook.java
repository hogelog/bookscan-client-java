package org.hogel.bookscan.model;

import java.util.Date;

public class OptimizingBook {
    private String filename;
    private String type;
    private String requestedAt;
    private String status;

    public OptimizingBook() {
    }

    public OptimizingBook(String filename, String type, String requestedAt, String status) {
        this.filename = filename;
        this.type = type;
        this.requestedAt = requestedAt;
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
