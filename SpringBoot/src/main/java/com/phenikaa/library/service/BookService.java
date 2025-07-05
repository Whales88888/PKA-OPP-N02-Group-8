package com.phenikaa.library.service;

import com.phenikaa.library.exception.BusinessLogicException;
import com.phenikaa.library.exception.ResourceNotFoundException;
import com.phenikaa.library.model.Book;
import com.phenikaa.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    // CRUD Operations
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public Book saveBook(Book book) {
        validateBook(book);
        return bookRepository.save(book);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        return bookRepository.findById(id)
            .map(book -> {
                book.setTitle(bookDetails.getTitle());
                book.setAuthor(bookDetails.getAuthor());
                book.setIsbn(bookDetails.getIsbn());
                book.setCategory(bookDetails.getCategory());
                book.setPublisher(bookDetails.getPublisher());
                book.setPublishYear(bookDetails.getPublishYear());
                book.setQuantity(bookDetails.getQuantity());
                book.setDescription(bookDetails.getDescription());
                // book.setShelfLocation(bookDetails.getShelfLocation());
                
                // Cập nhật số lượng có sẵn nếu cần
                if (book.getAvailableQuantity() > bookDetails.getQuantity()) {
                    book.setAvailableQuantity(bookDetails.getQuantity());
                }
                
                return bookRepository.save(book);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Sách", "ID", id));
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sách", "ID", id));
        
        // Kiểm tra xem sách có đang được mượn không
        if (book.getAvailableQuantity() < book.getQuantity()) {
            throw new BusinessLogicException("Không thể xóa sách đang được mượn");
        }
        
        bookRepository.delete(book);
    }
    
    // Search Operations
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    public List<Book> searchBooksByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }
    
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    public Page<Book> searchBooks(String title, String author, String category, String isbn, Pageable pageable) {
        return bookRepository.searchBooks(title, author, category, isbn, pageable);
    }
    
    // Business Logic
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableQuantityGreaterThan(0);
    }
    
    // public List<Book> getBooksByStatus(Book.BookStatus status) {
    //     return bookRepository.findByStatus(status);
    // }
    
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }
    
    public List<String> getAllPublishers() {
        return bookRepository.findAllPublishers();
    }
    
    public List<Book> getMostBorrowedBooks(int limit) {
        return bookRepository.findMostBorrowedBooks(Pageable.ofSize(limit));
    }
    
    public List<Book> getRecentlyAddedBooks(int limit) {
        return bookRepository.findRecentlyAddedBooks(Pageable.ofSize(limit));
    }
    
    // Statistics
    public Long getTotalBooksCount() {
        return bookRepository.countTotalBooks();
    }
    
    public Long getAvailableBooksCount() {
        return bookRepository.countAvailableBooks();
    }
    
    public Long getBorrowedBooksCount() {
        return bookRepository.countBorrowedBooks();
    }
    
    // Validation
    private void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề sách không được để trống");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Tác giả không được để trống");
        }
        
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Thể loại không được để trống");
        }
        
        if (book.getQuantity() == null || book.getQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng sách phải lớn hơn hoặc bằng 0");
        }
        
        // Kiểm tra ISBN nếu có
        if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
            if (book.getId() == null && bookRepository.existsByIsbn(book.getIsbn())) {
                throw new IllegalArgumentException("ISBN đã tồn tại trong hệ thống");
            }
        }
    }
    
    // Business methods for borrowing
    @Transactional
    public boolean borrowBook(Long bookId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                if (book.isAvailable()) {
                    book.borrowBook();
                    bookRepository.save(book);
                    System.out.println("Book borrowed successfully: " + bookId + ", available: " + book.getAvailableQuantity());
                    return true;
                } else {
                    System.err.println("Book not available: " + bookId + ", available: " + book.getAvailableQuantity());
                }
            } else {
                System.err.println("Book not found: " + bookId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error borrowing book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Transactional
    public boolean returnBook(Long bookId) {
        try {
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                book.returnBook();
                bookRepository.save(book);
                System.out.println("Book returned successfully: " + bookId + ", available: " + book.getAvailableQuantity());
                return true;
            } else {
                System.err.println("Book not found: " + bookId);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error returning book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isBookAvailable(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        return bookOpt.map(Book::isAvailable).orElse(false);
    }

    public List<Book> advancedSearch(String title, String author, String category, String isbn) {
        return bookRepository.findByAdvancedSearch(
            title != null ? title : "",
            author != null ? author : "",
            category != null ? category : "",
            isbn != null ? isbn : ""
        );
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
}