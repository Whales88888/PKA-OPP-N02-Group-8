package com.phenikaa.library.service;

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
    private ReaderService readerService;
    
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
    public Borrowing createBorrowing(Long bookId, Long readerId, Librarian processedBy) {
        // Kiểm tra sách
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với ID: " + bookId));
        
        // Kiểm tra độc giả
        Reader reader = readerRepository.findById(readerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả với ID: " + readerId));
        
        // Validate business rules
        validateBorrowing(book, reader);
        
        // Tạo giao dịch mượn
        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setReader(reader);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(LocalDate.now().plusDays(14)); // Mặc định 14 ngày
        borrowing.setStatus(Borrowing.BorrowStatus.BORROWED);
        borrowing.setProcessedBy(processedBy);
        borrowing.setBookConditionBorrowed("Tốt");
        
        // Cập nhật số lượng sách và độc giả
        bookService.borrowBook(bookId);
        readerService.borrowBook(readerId);
        
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        
        // Tạo thông báo
        notificationService.createBorrowingNotification(savedBorrowing, "BORROWED");
        
        return savedBorrowing;
    }
    
    @Transactional
    public Borrowing returnBook(Long borrowingId, String bookCondition, Librarian processedBy) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch mượn với ID: " + borrowingId));
        
        if (borrowing.getStatus() != Borrowing.BorrowStatus.BORROWED) {
            throw new RuntimeException("Sách đã được trả hoặc có trạng thái không hợp lệ");
        }
        
        // Cập nhật thông tin trả sách
        borrowing.setReturnDate(LocalDate.now());
        borrowing.setBookConditionReturned(bookCondition);
        borrowing.setStatus(Borrowing.BorrowStatus.RETURNED);
        borrowing.setProcessedBy(processedBy);
        
        // Tính phạt nếu trả muộn
        if (borrowing.isOverdue()) {
            double fine = borrowing.calculateFine();
            borrowing.setFineAmount(fine);
        }
        
        // Cập nhật số lượng sách và độc giả
        bookService.returnBook(borrowing.getBook().getId());
        readerService.returnBook(borrowing.getReader().getId());
        
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        
        // Tạo thông báo
        notificationService.createBorrowingNotification(savedBorrowing, "RETURNED");
        
        return savedBorrowing;
    }
    
    @Transactional
    public Borrowing renewBorrowing(Long borrowingId, int additionalDays) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch mượn với ID: " + borrowingId));
        
        if (!borrowing.canRenew()) {
            throw new RuntimeException("Không thể gia hạn giao dịch này");
        }
        
        borrowing.renewBorrowing(additionalDays);
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        
        // Tạo thông báo
        notificationService.createBorrowingNotification(savedBorrowing, "RENEWED");
        
        return savedBorrowing;
    }
    
    // Search and Filter Operations
    public List<Borrowing> getBorrowingsByReader(Reader reader) {
        return borrowingRepository.findByReader(reader);
    }
    
    public List<Borrowing> getBorrowingsByBook(Book book) {
        return borrowingRepository.findByBook(book);
    }
    
    public List<Borrowing> getBorrowingsByStatus(Borrowing.BorrowStatus status) {
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
    
    public Page<Borrowing> searchBorrowings(Long readerId, Long bookId, Borrowing.BorrowStatus status,
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
    
    // Validation
    private void validateBorrowing(Book book, Reader reader) {
        // Kiểm tra sách có sẵn không
        if (!book.isAvailable()) {
            throw new RuntimeException("Sách không có sẵn để mượn");
        }
        
        // Kiểm tra độc giả có thể mượn sách không
        if (!reader.canBorrowBooks()) {
            throw new RuntimeException("Độc giả không thể mượn sách (đã hết hạn hoặc đạt giới hạn)");
        }
        
        // Kiểm tra độc giả đã mượn sách này chưa
        if (borrowingRepository.isBookCurrentlyBorrowedByReader(reader, book)) {
            throw new RuntimeException("Độc giả đã mượn sách này rồi");
        }
    }
    
    // Business Logic Methods
    public boolean isBookAvailableForBorrowing(Long bookId) {
        return bookRepository.findById(bookId)
            .map(Book::isAvailable)
            .orElse(false);
    }
    
    public boolean canReaderBorrowMoreBooks(Long readerId) {
        return readerRepository.findById(readerId)
            .map(Reader::canBorrowBooks)
            .orElse(false);
    }
    
    public Integer getCurrentBorrowingCountByReader(Long readerId) {
        Reader reader = readerRepository.findById(readerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả"));
        return borrowingRepository.countCurrentBorrowingsByReader(reader);
    }
    
    @Transactional
    public void processOverdueNotifications() {
        List<Borrowing> overdueBorrowings = getOverdueBorrowings();
        for (Borrowing borrowing : overdueBorrowings) {
            notificationService.createOverdueNotification(borrowing);
        }
    }
    
    @Transactional
    public void processDueSoonNotifications(int days) {
        List<Borrowing> dueSoonBorrowings = getBorrowingsDueSoon(days);
        for (Borrowing borrowing : dueSoonBorrowings) {
            notificationService.createDueSoonNotification(borrowing);
        }
    }
    
    public ReaderService getReaderService() { return readerService; }
    public BookService getBookService() { return bookService; }
}