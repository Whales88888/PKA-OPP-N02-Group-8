package com.phenikaa.library.controller;

import com.phenikaa.library.model.Book;
import com.phenikaa.library.model.Borrowing;
import com.phenikaa.library.model.Notification;
import com.phenikaa.library.service.BookService;
import com.phenikaa.library.service.BorrowingService;
import com.phenikaa.library.service.NotificationService;
import com.phenikaa.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class MainController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReaderService readerService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private NotificationService notificationService;
    
    // Trang chủ - Dashboard
    @GetMapping("")
    public String dashboardPage(Model model) {
        // Thống kê tổng quan
        model.addAttribute("totalBooks", bookService.getTotalBooksCount());
        model.addAttribute("availableBooks", bookService.getAvailableBooksCount());
        model.addAttribute("totalReaders", readerService.getTotalReadersCount());
        model.addAttribute("activeReaders", readerService.getActiveReadersCount());
        model.addAttribute("totalBorrowings", borrowingService.getTotalBorrowingsCount());
        model.addAttribute("activeBorrowings", borrowingService.getActiveBorrowingsCount());
        model.addAttribute("overdueBorrowings", borrowingService.getOverdueBorrowingsCount());
        
        // Dữ liệu cho biểu đồ
        model.addAttribute("borrowingChartData", getBorrowingChartData());
        model.addAttribute("categoryChartData", getCategoryChartData());
        model.addAttribute("popularBooksData", getPopularBooksData());
        
        // Dữ liệu gần đây
        model.addAttribute("recentBooks", bookService.getRecentlyAddedBooks(5));
        model.addAttribute("recentBorrowings", borrowingService.getRecentBorrowings(5));
        model.addAttribute("recentActivities", getRecentActivities());
        model.addAttribute("notifications", notificationService.getUnreadNotifications());
        
        return "app/dashboard/index";
    }
    
    // Trang tìm kiếm nâng cao
    @GetMapping("/advanced-search")
    public String advancedSearch(@RequestParam(required = false) String q,
                               @RequestParam(required = false) String type,
                               Model model) {
        if (q != null && !q.trim().isEmpty()) {
            if ("books".equals(type) || type == null) {
                model.addAttribute("books", bookService.searchBooksByTitle(q));
            }
            if ("readers".equals(type) || type == null) {
                model.addAttribute("readers", readerService.searchReadersByName(q));
            }
        }
        
        model.addAttribute("searchQuery", q);
        model.addAttribute("searchType", type);
        return "app/search/advanced";
    }
    
    // Trang báo cáo thống kê
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("bookStats", getBookStatistics());
        model.addAttribute("readerStats", getReaderStatistics());
        model.addAttribute("borrowingStats", getBorrowingStatistics());
        return "app/reports/index";
    }
    
    // Trang thông báo
    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("notifications", notificationService.getAllNotifications());
        return "app/notifications/list";
    }
    
    // Trang thông tin thủ thư
    @GetMapping("/librarian-info")
    public String librarianInfo(Model model) {
        // Thông tin thủ thư hiện tại (có thể lấy từ session hoặc database)
        model.addAttribute("librarian", getCurrentLibrarian());
        return "app/librarian/info";
    }
    
    // Trang cài đặt
    @GetMapping("/settings")
    public String settingsPage(Model model) {
        return "app/settings/index";
    }
    
    // Trang 404
    @GetMapping("/not-found")
    public String notFound() {
        return "app/not-found";
    }
    
    // Helper methods
    private List<Map<String, Object>> getBorrowingChartData() {
        // Dữ liệu mẫu cho biểu đồ mượn sách theo tháng
        return List.of(
            Map.of("month", "T1", "count", 45),
            Map.of("month", "T2", "count", 52),
            Map.of("month", "T3", "count", 38),
            Map.of("month", "T4", "count", 61),
            Map.of("month", "T5", "count", 55),
            Map.of("month", "T6", "count", 48)
        );
    }
    
    private List<Map<String, Object>> getCategoryChartData() {
        // Dữ liệu mẫu cho biểu đồ phân bố thể loại
        return List.of(
            Map.of("category", "Công nghệ", "count", 25),
            Map.of("category", "Văn học", "count", 30),
            Map.of("category", "Kinh tế", "count", 20),
            Map.of("category", "Giáo dục", "count", 15),
            Map.of("category", "Khác", "count", 10)
        );
    }
    
    private List<Map<String, Object>> getPopularBooksData() {
        // Dữ liệu mẫu cho sách được mượn nhiều nhất
        return List.of(
            Map.of("title", "Java Programming", "borrowCount", 15),
            Map.of("title", "Spring Framework", "borrowCount", 12),
            Map.of("title", "Database Design", "borrowCount", 10),
            Map.of("title", "Web Development", "borrowCount", 8),
            Map.of("title", "Data Structures", "borrowCount", 6)
        );
    }
    
    private List<Map<String, Object>> getRecentActivities() {
        // Dữ liệu mẫu cho hoạt động gần đây
        return List.of(
            Map.of("type", "BORROW", "title", "Mượn sách", "description", "Nguyễn Văn A mượn sách Java Programming", "timestamp", java.time.LocalDateTime.now().minusHours(2)),
            Map.of("type", "RETURN", "title", "Trả sách", "description", "Trần Thị B trả sách Spring Framework", "timestamp", java.time.LocalDateTime.now().minusHours(4)),
            Map.of("type", "ADD", "title", "Thêm sách", "description", "Thêm sách mới Database Design", "timestamp", java.time.LocalDateTime.now().minusHours(6))
        );
    }
    
    private Map<String, Object> getBookStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookService.getTotalBooksCount());
        stats.put("availableBooks", bookService.getAvailableBooksCount());
        stats.put("borrowedBooks", bookService.getBorrowedBooksCount());
        stats.put("categories", bookService.getAllCategories().size());
        return stats;
    }
    
    private Map<String, Object> getReaderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReaders", readerService.getTotalReadersCount());
        stats.put("activeReaders", readerService.getActiveReadersCount());
        stats.put("suspendedReaders", readerService.getSuspendedReadersCount());
        stats.put("newReadersThisMonth", readerService.getNewReadersThisMonth());
        return stats;
    }
    
    private Map<String, Object> getBorrowingStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBorrowings", borrowingService.getTotalBorrowingsCount());
        stats.put("activeBorrowings", borrowingService.getActiveBorrowingsCount());
        stats.put("overdueBorrowings", borrowingService.getOverdueBorrowingsCount());
        stats.put("returnedBorrowings", borrowingService.getReturnedBorrowingsCount());
        return stats;
    }
    
    private Map<String, Object> getCurrentLibrarian() {
        // Dữ liệu mẫu cho thủ thư hiện tại
        Map<String, Object> librarian = new HashMap<>();
        librarian.put("name", "Nguyễn Văn Thủ thư");
        librarian.put("email", "thuthu@phenikaa.edu.vn");
        librarian.put("phone", "0123456789");
        librarian.put("role", "Thủ thư chính");
        librarian.put("joinDate", java.time.LocalDate.of(2020, 1, 1));
        return librarian;
    }
} 