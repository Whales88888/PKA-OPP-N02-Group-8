package com.phenikaa.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "borrowings")
public class Borrowing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Sách không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @NotNull(message = "Độc giả không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;
    
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    private BorrowStatus status = BorrowStatus.BORROWED;
    
    @Column(name = "renewal_count")
    private Integer renewalCount = 0;
    
    @Column(name = "max_renewals")
    private Integer maxRenewals = 2;
    
    @Column(name = "fine_amount")
    private Double fineAmount = 0.0;
    
    @Column(name = "book_condition_borrowed")
    private String bookConditionBorrowed;
    
    @Column(name = "book_condition_returned")
    private String bookConditionReturned;
    
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Thủ thư xử lý
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "librarian_id")
    private Librarian processedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (borrowDate == null) {
            borrowDate = LocalDate.now();
        }
        if (dueDate == null) {
            // Mặc định cho mượn 14 ngày
            dueDate = borrowDate.plusDays(14);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isOverdue() {
        return status == BorrowStatus.BORROWED && LocalDate.now().isAfter(dueDate);
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    public boolean canRenew() {
        return status == BorrowStatus.BORROWED && 
               renewalCount < maxRenewals && 
               !isOverdue();
    }
    
    public void renewBorrowing(int additionalDays) {
        if (canRenew()) {
            dueDate = dueDate.plusDays(additionalDays);
            renewalCount++;
            updatedAt = LocalDateTime.now();
        }
    }
    
    public void returnBook() {
        returnDate = LocalDate.now();
        status = BorrowStatus.RETURNED;
        
        // Tính phạt nếu trả muộn
        if (isOverdue()) {
            long daysOverdue = getDaysOverdue();
            fineAmount = daysOverdue * 5000.0; // 5000 VND/ngày
        }
        
        updatedAt = LocalDateTime.now();
    }
    
    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        long daysOverdue = getDaysOverdue();
        return daysOverdue * 5000.0; // 5000 VND/ngày
    }
    
    public enum BorrowStatus {
        BORROWED("Đang mượn"),
        RETURNED("Đã trả"),
        OVERDUE("Quá hạn"),
        LOST("Mất sách"),
        DAMAGED("Hư hỏng");
        
        private final String displayName;
        
        BorrowStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public Integer getMaxRenewals() {
        return maxRenewals;
    }

    public void setMaxRenewals(Integer maxRenewals) {
        this.maxRenewals = maxRenewals;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getBookConditionBorrowed() {
        return bookConditionBorrowed;
    }

    public void setBookConditionBorrowed(String bookConditionBorrowed) {
        this.bookConditionBorrowed = bookConditionBorrowed;
    }

    public String getBookConditionReturned() {
        return bookConditionReturned;
    }

    public void setBookConditionReturned(String bookConditionReturned) {
        this.bookConditionReturned = bookConditionReturned;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Librarian getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Librarian processedBy) {
        this.processedBy = processedBy;
    }
}