package library;

import java.io.Serializable;
import java.util.Date;

public class BorrowingSlip implements Serializable {
    private String slipID;
    private Borrower borrower;
    private Book book;
    private Date borrowDate;
    private Date dueDate;

    public BorrowingSlip(String slipID, Borrower borrower, Book book, Date borrowDate, Date dueDate) {
        this.slipID = slipID;
        this.borrower = borrower;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public String getSlipID() { return slipID; }
    public Borrower getBorrower() { return borrower; }
    public Book getBook() { return book; }
    public Date getBorrowDate() { return borrowDate; }
    public Date getDueDate() { return dueDate; }

    @Override
    public String toString() {
        return "BorrowingSlip{" +
                "slipID='" + slipID + '\'' +
                ", borrower=" + borrower +
                ", book=" + book +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                '}';
    }
}