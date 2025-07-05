package com.phenikaa.library.service;

import com.phenikaa.library.exception.BusinessLogicException;
import com.phenikaa.library.exception.ResourceNotFoundException;
import com.phenikaa.library.model.Borrowing;
import com.phenikaa.library.model.Book;
import com.phenikaa.library.model.Reader;
import com.phenikaa.library.model.Librarian;
import com.phenikaa.library.repository.BorrowingRepository;
import com.phenikaa.library.repository.BookRepository;
import com.phenikaa.library.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowingService {
    
    @Autowired
    private BorrowingRepository borrowingRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ReaderRepository readerRepository;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private NotificationService notificationService;
    
    // CRUD Operations
    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }
    
    public Optional<Borrowing> getBorrowingById(Long id) {
        return borrowingRepository.findById(id);
    }
    
    @Transactional
    public Borrowing createBorrowing(Long bookId, Long readerId, LocalDate borrowDate, LocalDate dueDate) {
        try {
            System.out.println("Creating borrowing - bookId: " + bookId + ", readerId: " + readerId + ", borrowDate: " + borrowDate + ", dueDate: " + dueDate);
            
            // Validate input parameters
            if (bookId == null || readerId == null) {
                throw new BusinessLogicException("ID sách và độc giả không được để trống");
            }
            
            Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Sách", "ID", bookId));
            Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new ResourceNotFoundException("Độc giả", "ID", readerId));
            
            System.out.println("Found book: " + book.getTitle() + ", available: " + book.getAvailableQuantity());
            System.out.println("Found reader: " + reader.getName() + ", active: " + reader.getIsActive());
            
            // Validate borrowing
            validateBorrowing(book, reader);
            
            // Set default dates if not provided
            LocalDate actualBorrowDate = borrowDate != null ? borrowDate : LocalDate.now();
            LocalDate actualDueDate = dueDate != null ? dueDate : actualBorrowDate.plusDays(30);
            
            // Validate dates
            if (actualDueDate.isBefore(actualBorrowDate)) {
                throw new BusinessLogicException("Ngày hẹn trả phải sau ngày mượn");
            }
            
            Borrowing borrowing = new Borrowing();
            borrowing.setBook(book);
            borrowing.setReader(reader);
            borrowing.setBorrowDate(actualBorrowDate);
            borrowing.setDueDate(actualDueDate);
            borrowing.setStatus("borrowed");
            borrowing.setBookCondition("good");
            
            System.out.println("Updating book availability for book: " + bookId);
            
            // Update book availability
            if (!bookService.borrowBook(bookId)) {
                throw new BusinessLogicException("Sách không có sẵn để mượn");
            }
            
            Borrowing savedBorrowing = borrowingRepository.save(borrowing);
            System.out.println("Borrowing created successfully: " + savedBorrowing.getId());
            
            try {
                notificationService.createBorrowingNotification(savedBorrowing, "BORROWED");
            } catch (Exception e) {
                // Log error but don't fail the borrowing operation
                System.err.println("Failed to create notification: " + e.getMessage());
            }
            
            return savedBorrowing;
        } catch (BusinessLogicException | ResourceNotFoundException e) {
            // Re-throw business exceptions
            throw e;
        } catch (Exception e) {
            System.err.println("Error in createBorrowing: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessLogicException("Không thể tạo giao dịch mượn sách: " + e.getMessage());
        }
    }
    
    @Transactional
    public Borrowing returnBook(Long borrowingId, String bookCondition) {
        try {
            System.out.println("Returning borrowing: " + borrowingId + ", condition: " + bookCondition);
            
            // Validate input parameters
            if (borrowingId == null) {
                throw new BusinessLogicException("ID giao dịch mượn không được để trống");
            }
            
            Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Giao dịch mượn", "ID", borrowingId));
            
            System.out.println("Found borrowing: " + borrowing.getId() + ", status: " + borrowing.getStatus());
            
            if (!"borrowed".equals(borrowing.getStatus())) {
                throw new BusinessLogicException("Sách đã được trả hoặc có trạng thái không hợp lệ");
            }
            
            borrowing.setReturnDate(LocalDate.now());
            borrowing.setBookCondition(bookCondition != null ? bookCondition : "good");
            borrowing.setStatus("returned");
            
            System.out.println("Updating book availability for book: " + borrowing.getBook().getId());
            
            // Update book availability
            if (!bookService.returnBook(borrowing.getBook().getId())) {
                throw new BusinessLogicException("Không thể cập nhật trạng thái sách");
            }
            
            Borrowing savedBorrowing = borrowingRepository.save(borrowing);
            System.out.println("Borrowing saved successfully: " + savedBorrowing.getId());
            
            try {
                notificationService.createBorrowingNotification(savedBorrowing, "RETURNED");
            } catch (Exception e) {
                // Log error but don't fail the return operation
                System.err.println("Failed to create notification: " + e.getMessage());
            }
            
            return savedBorrowing;
        } catch (BusinessLogicException | ResourceNotFoundException e) {
            // Re-throw business exceptions
            throw e;
        } catch (Exception e) {
            System.err.println("Error in returnBook: " + e.getMessage());
            e.printStackTrace();
            throw new BusinessLogicException("Không thể trả sách: " + e.getMessage());
        }
    }
    
    @Transactional
    public Borrowing renewBorrowing(Long borrowingId, int additionalDays) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
            .orElseThrow(() -> new ResourceNotFoundException("Giao dịch mượn", "ID", borrowingId));
        
        if (!borrowing.canRenew()) {
            throw new BusinessLogicException("Không thể gia hạn giao dịch này");
        }
        
        borrowing.renewBorrowing(additionalDays);
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        
        try {
            notificationService.createBorrowingNotification(savedBorrowing, "RENEWED");
        } catch (Exception e) {
            // Log error but don't fail the renew operation
            System.err.println("Failed to create notification: " + e.getMessage());
        }
        
        return savedBorrowing;
    }
    
    // Search and Filter Operations
    public List<Borrowing> getBorrowingsByReader(Reader reader) {
        return borrowingRepository.findByReader(reader);
    }
    
    public List<Borrowing> getBorrowingsByBook(Book book) {
        return borrowingRepository.findByBook(book);
    }
    
    public List<Borrowing> getBorrowingsByStatus(String status) {
        return borrowingRepository.findByStatus(status);
    }
    
    public List<Borrowing> getCurrentBorrowings() {
        return borrowingRepository.findCurrentBorrowings();
    }
    
    public List<Borrowing> getOverdueBorrowings() {
        return borrowingRepository.findOverdueBorrowings();
    }
    
    public List<Borrowing> getBorrowingsDueSoon(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        return borrowingRepository.findBorrowingsDueSoon(endDate);
    }
    
    public Page<Borrowing> searchBorrowings(Long readerId, Long bookId, String status,
                                          LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return borrowingRepository.searchBorrowings(readerId, bookId, status, startDate, endDate, pageable);
    }
    
    // Statistics
    public Long getTotalBorrowingsCount() {
        return borrowingRepository.countTotalBorrowings();
    }
    
    public Long getCurrentBorrowingsCount() {
        return borrowingRepository.countCurrentBorrowings();
    }
    
    public Long getOverdueBorrowingsCount() {
        return borrowingRepository.countOverdueBorrowings();
    }
    
    public Long getReturnedBorrowingsCount() {
        return borrowingRepository.countReturnedBorrowings();
    }
    
    public Long getActiveBorrowingsCount() {
        return borrowingRepository.countCurrentBorrowings();
    }
    
    public List<Borrowing> getRecentBorrowings(int limit) {
        return borrowingRepository.findRecentBorrowings(Pageable.ofSize(limit));
    }
    
    public Page<Borrowing> getAllBorrowings(Pageable pageable) {
        return borrowingRepository.findAll(pageable);
    }
    
    // Validation
    private void validateBorrowing(Book book, Reader reader) {
        System.out.println("Validating borrowing - book available: " + book.isAvailable() + ", reader active: " + reader.getIsActive());
        
        if (book == null) {
            throw new BusinessLogicException("Sách không tồn tại");
        }
        
        if (reader == null) {
            throw new BusinessLogicException("Độc giả không tồn tại");
        }
        
        if (!book.isAvailable()) {
            throw new BusinessLogicException("Sách không có sẵn để mượn (còn lại: " + book.getAvailableQuantity() + " cuốn)");
        }
        
        if (!reader.getIsActive()) {
            throw new BusinessLogicException("Độc giả không hợp lệ hoặc đã bị khóa");
        }
        
        // Note: Borrowing limit validation removed as Reader entity doesn't have maxBorrowBooks field
        // This can be added later when the Reader entity is enhanced with borrowing limits
    }
}