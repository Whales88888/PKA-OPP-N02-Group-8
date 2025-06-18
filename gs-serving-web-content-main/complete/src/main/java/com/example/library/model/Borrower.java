package com.example.library.model;

public class Borrower {
    private String borrowerID;
    private String borrowerName;
    private String phoneNumber;

    public Borrower(String borrowerID, String borrowerName, String phoneNumber) {
        this.borrowerID = borrowerID;
        this.borrowerName = borrowerName;
        this.phoneNumber = phoneNumber;
    }
    public String getBorrowerID() { return borrowerID; }
    public String getBorrowerName() { return borrowerName; }
    public String getPhoneNumber() { return phoneNumber; }
}
