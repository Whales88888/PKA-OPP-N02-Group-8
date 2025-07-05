package com.phenikaa.library.repository;

import com.phenikaa.library.model.Borrowing;
import com.phenikaa.library.model.Book;
import com.phenikaa.library.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    
    // Tìm kiếm theo độc giả
    List<Borrowing> findByReader(Reader reader);
    
    // Tìm kiếm theo sách
    List<Borrowing> findByBook(Book book);
    
    // Tìm kiếm theo trạng thái
    List<Borrowing> findByStatus(String status);
    
    // Tìm kiếm giao dịch đang mượn
    @Query("SELECT b FROM Borrowing b JOIN FETCH b.book JOIN FETCH b.reader WHERE b.status = 'borrowed'")
    List<Borrowing> findCurrentBorrowings();
    
    // Tìm kiếm giao dịch quá hạn
    @Query("SELECT b FROM Borrowing b JOIN FETCH b.book JOIN FETCH b.reader WHERE b.status = 'borrowed' AND b.dueDate < CURRENT_DATE")
    List<Borrowing> findOverdueBorrowings();
    
    // Tìm kiếm giao dịch sắp hết hạn
    @Query("SELECT b FROM Borrowing b JOIN FETCH b.book JOIN FETCH b.reader WHERE b.status = 'borrowed' AND b.dueDate BETWEEN CURRENT_DATE AND :endDate")
    List<Borrowing> findBorrowingsDueSoon(@Param("endDate") LocalDate endDate);
    
    // Tìm kiếm theo khoảng thời gian mượn
    List<Borrowing> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Tìm kiếm theo khoảng thời gian trả
    List<Borrowing> findByReturnDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Tìm kiếm giao dịch của độc giả theo trạng thái
    List<Borrowing> findByReaderAndStatus(Reader reader, String status);
    
    // Tìm kiếm giao dịch của sách theo trạng thái
    List<Borrowing> findByBookAndStatus(Book book, String status);
    
    // Thống kê giao dịch
    @Query("SELECT COUNT(b) FROM Borrowing b")
    Long countTotalBorrowings();
    
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.status = 'borrowed'")
    Long countCurrentBorrowings();
    
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.status = 'borrowed' AND b.dueDate < CURRENT_DATE")
    Long countOverdueBorrowings();
    
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.returnDate IS NOT NULL")
    Long countReturnedBorrowings();
    
    // Giao dịch gần đây
    @Query("SELECT b FROM Borrowing b ORDER BY b.createdAt DESC")
    List<Borrowing> findRecentBorrowings(Pageable pageable);
    
    // Sách được mượn nhiều nhất
    @Query("SELECT b.book, COUNT(b) as borrowCount FROM Borrowing b GROUP BY b.book ORDER BY borrowCount DESC")
    List<Object[]> findMostBorrowedBooks(Pageable pageable);
    
    // Độc giả mượn sách nhiều nhất
    @Query("SELECT b.reader, COUNT(b) as borrowCount FROM Borrowing b GROUP BY b.reader ORDER BY borrowCount DESC")
    List<Object[]> findMostActiveReaders(Pageable pageable);
    
    // Kiểm tra độc giả có đang mượn sách cụ thể không
    @Query("SELECT COUNT(b) > 0 FROM Borrowing b WHERE b.reader = :reader AND b.book = :book AND b.status = 'borrowed'")
    boolean isBookCurrentlyBorrowedByReader(@Param("reader") Reader reader, @Param("book") Book book);
    
    // Đếm số sách đang mượn của độc giả
    @Query("SELECT COUNT(b) FROM Borrowing b WHERE b.reader = :reader AND b.status = 'borrowed'")
    Integer countCurrentBorrowingsByReader(@Param("reader") Reader reader);
    
    // Tìm kiếm tổng hợp với phân trang
    @Query("SELECT b FROM Borrowing b WHERE " +
           "(:readerId IS NULL OR b.reader.id = :readerId) AND " +
           "(:bookId IS NULL OR b.book.id = :bookId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:startDate IS NULL OR b.borrowDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.borrowDate <= :endDate)")
    Page<Borrowing> searchBorrowings(@Param("readerId") Long readerId,
                                   @Param("bookId") Long bookId,
                                   @Param("status") String status,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   Pageable pageable);
}