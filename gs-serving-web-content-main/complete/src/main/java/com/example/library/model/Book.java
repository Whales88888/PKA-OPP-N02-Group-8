package com.example.library.model;

public class Book {
    private String bookID;
    private String bookName;
    private String author;

    public Book(String bookID, String bookName, String author) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.author = author;
    }
    public String getBookID() { return bookID; }
    public String getBookName() { return bookName; }
    public String getAuthor() { return author; }
}
