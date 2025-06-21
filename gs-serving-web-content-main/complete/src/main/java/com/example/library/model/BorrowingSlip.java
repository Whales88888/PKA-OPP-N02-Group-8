package com.example.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class BorrowingSlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Borrower borrower;

    @ManyToOne
    private Book book;

    private LocalDate borrowDate;
    private LocalDate dueDate;

    public BorrowingSlip() {}
    public BorrowingSlip(Borrower borrower, Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.borrower = borrower;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }
    public Long getId() { return id; }
    public Borrower getBorrower() { return borrower; }
    public void setBorrower(Borrower borrower) { this.borrower = borrower; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}