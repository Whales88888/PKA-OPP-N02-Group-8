package com.example.library.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class BorrowingSlip {
    @Id
    private String slipId;

    @ManyToOne
    private Borrower borrower;

    @ManyToOne
    private Book book;

    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    @Temporal(TemporalType.DATE)
    private Date dueDate;

    public BorrowingSlip() {}

    public BorrowingSlip(String slipId, Borrower borrower, Book book, Date borrowDate, Date dueDate) {
        this.slipId = slipId;
        this.borrower = borrower;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    // Getters và Setters
    public String getSlipId() { return slipId; }
    public void setSlipId(String slipId) { this.slipId = slipId; }
    public Borrower getBorrower() { return borrower; }
    public void setBorrower(Borrower borrower) { this.borrower = borrower; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
}
