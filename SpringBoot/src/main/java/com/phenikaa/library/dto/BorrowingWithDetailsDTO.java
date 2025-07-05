package com.phenikaa.library.dto;

import com.phenikaa.library.model.Borrowing;
import com.phenikaa.library.model.Book;
import com.phenikaa.library.model.Reader;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BorrowingWithDetailsDTO {
    private Long id;
    private BookDTO book;
    private ReaderDTO reader;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private String bookCondition;
    private LocalDateTime createdAt;

    // Constructor from Borrowing entity
    public BorrowingWithDetailsDTO(Borrowing borrowing) {
        this.id = borrowing.getId();
        this.borrowDate = borrowing.getBorrowDate();
        this.dueDate = borrowing.getDueDate();
        this.returnDate = borrowing.getReturnDate();
        this.status = borrowing.getStatus();
        this.bookCondition = borrowing.getBookCondition();
        this.createdAt = borrowing.getCreatedAt();
        
        // Convert Book to BookDTO
        if (borrowing.getBook() != null) {
            this.book = new BookDTO(borrowing.getBook());
        }
        
        // Convert Reader to ReaderDTO
        if (borrowing.getReader() != null) {
            this.reader = new ReaderDTO(borrowing.getReader());
        }
    }

    // Nested DTOs
    public static class BookDTO {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private String category;
        private Integer quantity;
        private Integer availableQuantity;
        private String publisher;
        private Integer publishYear;

        public BookDTO(Book book) {
            this.id = book.getId();
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.isbn = book.getIsbn();
            this.category = book.getCategory();
            this.quantity = book.getQuantity();
            this.availableQuantity = book.getAvailableQuantity();
            this.publisher = book.getPublisher();
            this.publishYear = book.getPublishYear();
        }

        // Getters
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getIsbn() { return isbn; }
        public String getCategory() { return category; }
        public Integer getQuantity() { return quantity; }
        public Integer getAvailableQuantity() { return availableQuantity; }
        public String getPublisher() { return publisher; }
        public Integer getPublishYear() { return publishYear; }
    }

    public static class ReaderDTO {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private Boolean isActive;

        public ReaderDTO(Reader reader) {
            this.id = reader.getId();
            this.name = reader.getName();
            this.email = reader.getEmail();
            this.phone = reader.getPhone();
            this.address = reader.getAddress();
            this.isActive = reader.getIsActive();
        }

        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public Boolean getIsActive() { return isActive; }
    }

    // Getters
    public Long getId() { return id; }
    public BookDTO getBook() { return book; }
    public ReaderDTO getReader() { return reader; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public String getBookCondition() { return bookCondition; }
    public LocalDateTime getCreatedAt() { return createdAt; }
} 