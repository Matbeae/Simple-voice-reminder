package com.example.simplevoicereminder;

public class Recording {
    private final String fileName;
    private final String date;

    public Recording(String fileName, String date) {
        this.fileName = fileName;
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDate() {
        return date;
    }
}