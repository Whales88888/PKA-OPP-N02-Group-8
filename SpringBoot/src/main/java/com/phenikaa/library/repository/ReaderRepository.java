package com.phenikaa.library.repository;

import com.phenikaa.library.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    
    // Tìm kiếm độc giả theo email
    Optional<Reader> findByEmail(String email);
    
    // Tìm kiếm độc giả theo mã sinh viên
    Optional<Reader> findByStudentId(String studentId);
    
    // Tìm kiếm độc giả theo tên
    List<Reader> findByNameContainingIgnoreCase(String name);
    
    // Tìm kiếm độc giả theo số điện thoại
    Optional<Reader> findByPhone(String phone);
    
    // Tìm kiếm độc giả theo loại
    List<Reader> findByReaderType(Reader.ReaderType readerType);
    
    // Tìm kiếm độc giả theo trạng thái
    List<Reader> findByStatus(Reader.ReaderStatus status);
    
    // Tìm kiếm độc giả hết hạn
    List<Reader> findByExpiryDateBefore(LocalDate date);
    
    // Tìm kiếm độc giả sắp hết hạn
    List<Reader> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Tìm kiếm tổng hợp
    @Query("SELECT r FROM Reader r WHERE " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(r.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:readerType IS NULL OR r.readerType = :readerType) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Reader> searchReaders(@Param("name") String name,
                              @Param("email") String email,
                              @Param("readerType") Reader.ReaderType readerType,
                              @Param("status") Reader.ReaderStatus status,
                              Pageable pageable);
    
    // Thống kê độc giả
    @Query("SELECT COUNT(r) FROM Reader r")
    Long countTotalReaders();
    
    @Query("SELECT COUNT(r) FROM Reader r WHERE r.status = 'ACTIVE'")
    Long countActiveReaders();
    
    @Query("SELECT COUNT(r) FROM Reader r WHERE r.currentBorrowedBooks > 0")
    Long countReadersWithBorrowedBooks();
    
    // Độc giả có sách quá hạn
    @Query("SELECT DISTINCT r FROM Reader r JOIN r.borrowings b " +
           "WHERE b.status = 'BORROWED' AND b.dueDate < CURRENT_DATE")
    List<Reader> findReadersWithOverdueBooks();
    
    // Độc giả mượn sách nhiều nhất
    @Query("SELECT r FROM Reader r ORDER BY r.currentBorrowedBooks DESC")
    List<Reader> findTopBorrowers(Pageable pageable);
    
    // Độc giả mới đăng ký
    @Query("SELECT r FROM Reader r ORDER BY r.registrationDate DESC")
    List<Reader> findRecentlyRegistered(Pageable pageable);
    
    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);
    
    // Kiểm tra mã sinh viên đã tồn tại
    boolean existsByStudentId(String studentId);
    
    // Kiểm tra số điện thoại đã tồn tại
    boolean existsByPhone(String phone);

    long countByStatus(Reader.ReaderStatus status);
    @Query("SELECT COUNT(r) FROM Reader r WHERE MONTH(r.registrationDate) = MONTH(CURRENT_DATE) AND YEAR(r.registrationDate) = YEAR(CURRENT_DATE)")
    long countNewReadersThisMonth();
}