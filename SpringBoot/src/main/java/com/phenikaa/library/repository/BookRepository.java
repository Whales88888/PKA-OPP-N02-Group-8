package com.phenikaa.library.repository;

import com.phenikaa.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Tìm kiếm sách theo tiêu đề
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Tìm kiếm sách theo tác giả
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Tìm kiếm sách theo thể loại
    List<Book> findByCategoryIgnoreCase(String category);
    
    // Tìm kiếm sách theo ISBN
    Optional<Book> findByIsbn(String isbn);
    
    // Tìm kiếm sách có sẵn
    List<Book> findByAvailableQuantityGreaterThan(Integer quantity);
    
    // Tìm kiếm sách theo trạng thái
    List<Book> findByStatus(Book.BookStatus status);
    
    // Tìm kiếm sách theo nhà xuất bản
    List<Book> findByPublisherContainingIgnoreCase(String publisher);
    
    // Tìm kiếm sách theo năm xuất bản
    List<Book> findByPublicationYear(Integer year);
    
    // Tìm kiếm tổng hợp
    @Query("SELECT b FROM Book b WHERE " +
           "(:title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category = '' OR LOWER(b.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
           "(:isbn = '' OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')))")
    List<Book> findByAdvancedSearch(
        @Param("title") String title,
        @Param("author") String author,
        @Param("category") String category,
        @Param("isbn") String isbn
    );
    
    // Lấy danh sách thể loại sách
    @Query("SELECT DISTINCT b.category FROM Book b ORDER BY b.category")
    List<String> findAllCategories();
    
    // Lấy danh sách nhà xuất bản
    @Query("SELECT DISTINCT b.publisher FROM Book b WHERE b.publisher IS NOT NULL ORDER BY b.publisher")
    List<String> findAllPublishers();
    
    // Thống kê sách
    @Query("SELECT COUNT(b) FROM Book b")
    Long countTotalBooks();
    
    @Query("SELECT SUM(b.availableQuantity) FROM Book b")
    Long countAvailableBooks();
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableQuantity = 0")
    Long countBorrowedBooks();
    
    // Sách được mượn nhiều nhất
    @Query("SELECT b FROM Book b LEFT JOIN b.borrowings br " +
           "GROUP BY b ORDER BY COUNT(br) DESC")
    List<Book> findMostBorrowedBooks(Pageable pageable);
    
    // Sách mới thêm
    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    List<Book> findRecentlyAddedBooks(Pageable pageable);
    
    // Kiểm tra ISBN đã tồn tại
    boolean existsByIsbn(String isbn);
    
    // Sách theo vị trí kệ
    List<Book> findByShelfLocationContainingIgnoreCase(String shelfLocation);

    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR :title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR :author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(b.category) LIKE LOWER(CONCAT('%', :category, '%'))) AND " +
           "(:isbn IS NULL OR :isbn = '' OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')))")
    Page<Book> searchBooks(@Param("title") String title,
                          @Param("author") String author,
                          @Param("category") String category,
                          @Param("isbn") String isbn,
                          Pageable pageable);
}