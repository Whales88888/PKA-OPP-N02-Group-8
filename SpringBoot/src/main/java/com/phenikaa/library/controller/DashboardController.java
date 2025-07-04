package com.phenikaa.library.controller;

import com.phenikaa.library.service.BookService;
import com.phenikaa.library.service.ReaderService;
import com.phenikaa.library.service.BorrowingService;
import com.phenikaa.library.service.NotificationService;
import com.phenikaa.library.model.Book;
import com.phenikaa.library.model.Reader;
import com.phenikaa.library.model.Borrowing;
import com.phenikaa.library.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard/api/dashboard")
public class DashboardController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReaderService readerService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/stats")
    public StatsDto getStats() {
        StatsDto stats = new StatsDto();
        stats.setTotalBooks(bookService.getTotalBooksCount());
        stats.setAvailableBooks(bookService.getAvailableBooksCount());
        stats.setBorrowedBooks(bookService.getBorrowedBooksCount());
        stats.setTotalReaders(readerService.getTotalReadersCount());
        stats.setActiveReaders(readerService.getActiveReadersCount());
        stats.setReadersWithBorrowedBooks(readerService.getReadersWithBorrowedBooksCount());
        stats.setTotalBorrowings(borrowingService.getTotalBorrowingsCount());
        stats.setCurrentBorrowings(borrowingService.getCurrentBorrowingsCount());
        stats.setOverdueBorrowings(borrowingService.getOverdueBorrowingsCount());
        stats.setReturnedBorrowings(borrowingService.getReturnedBorrowingsCount());
        return stats;
    }
    
    @GetMapping("/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();
        
        activities.put("recentBooks", bookService.getRecentlyAddedBooks(5));
        activities.put("recentReaders", readerService.getRecentlyRegistered(5));
        activities.put("recentBorrowings", borrowingService.getRecentBorrowings(10));
        activities.put("overdueBorrowings", borrowingService.getOverdueBorrowings());
        activities.put("dueSoonBorrowings", borrowingService.getBorrowingsDueSoon(7));
        
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        List<Notification> notifications = notificationService.getRecentNotifications(20);
        return ResponseEntity.ok(notifications);
    }
    
    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/readers")
    public List<Reader> getAllReaders() {
        return readerService.getAllReaders();
    }

    @GetMapping("/borrowings")
    public List<Borrowing> getAllBorrowings() {
        return borrowingService.getAllBorrowings();
    }
}