package com.phenikaa.library.controller;

public class StatsDto {
    private long totalBooks;
    private long availableBooks;
    private long borrowedBooks;
    private long totalReaders;
    private long activeReaders;
    private long readersWithBorrowedBooks;
    private long totalBorrowings;
    private long currentBorrowings;
    private long overdueBorrowings;
    private long returnedBorrowings;

    // Getters and setters
    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
    public long getAvailableBooks() { return availableBooks; }
    public void setAvailableBooks(long availableBooks) { this.availableBooks = availableBooks; }
    public long getBorrowedBooks() { return borrowedBooks; }
    public void setBorrowedBooks(long borrowedBooks) { this.borrowedBooks = borrowedBooks; }
    public long getTotalReaders() { return totalReaders; }
    public void setTotalReaders(long totalReaders) { this.totalReaders = totalReaders; }
    public long getActiveReaders() { return activeReaders; }
    public void setActiveReaders(long activeReaders) { this.activeReaders = activeReaders; }
    public long getReadersWithBorrowedBooks() { return readersWithBorrowedBooks; }
    public void setReadersWithBorrowedBooks(long readersWithBorrowedBooks) { this.readersWithBorrowedBooks = readersWithBorrowedBooks; }
    public long getTotalBorrowings() { return totalBorrowings; }
    public void setTotalBorrowings(long totalBorrowings) { this.totalBorrowings = totalBorrowings; }
    public long getCurrentBorrowings() { return currentBorrowings; }
    public void setCurrentBorrowings(long currentBorrowings) { this.currentBorrowings = currentBorrowings; }
    public long getOverdueBorrowings() { return overdueBorrowings; }
    public void setOverdueBorrowings(long overdueBorrowings) { this.overdueBorrowings = overdueBorrowings; }
    public long getReturnedBorrowings() { return returnedBorrowings; }
    public void setReturnedBorrowings(long returnedBorrowings) { this.returnedBorrowings = returnedBorrowings; }
} 