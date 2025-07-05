package com.phenikaa.library.controller;

import com.phenikaa.library.model.Borrowing;
import com.phenikaa.library.model.Book;
import com.phenikaa.library.model.Reader;
import com.phenikaa.library.model.Librarian;
import com.phenikaa.library.service.BorrowingService;
import com.phenikaa.library.service.BookService;
import com.phenikaa.library.service.ReaderService;
import com.phenikaa.library.exception.BusinessLogicException;
import com.phenikaa.library.exception.ResourceNotFoundException;
import com.phenikaa.library.dto.BorrowingWithDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReaderService readerService;
    
    // Hiển thị danh sách giao dịch mượn/trả
    @GetMapping
    public String listBorrowings(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "borrowDate") String sortBy,
                                @RequestParam(defaultValue = "desc") String sortDir,
                                @RequestParam(required = false) Long readerId,
                                @RequestParam(required = false) Long bookId,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Borrowing> borrowingPage = borrowingService.searchBorrowings(
            readerId, bookId, status, startDate, endDate, pageable);
        
        model.addAttribute("borrowings", borrowingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", borrowingPage.getTotalPages());
        model.addAttribute("totalElements", borrowingPage.getTotalElements());
        model.addAttribute("readers", readerService.getAllReaders());
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("currentReaderId", readerId);
        model.addAttribute("currentBookId", bookId);
        model.addAttribute("currentStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        
        return "borrowings/list";
    }
    
    // Hiển thị form mượn sách mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("borrowing", new Borrowing());
        model.addAttribute("readers", readerService.getAllReaders());
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        return "borrowings/form";
    }
    
    // Xử lý mượn sách mới (MVC, không phải REST API)
    @PostMapping
    public String createBorrowing(@RequestParam Long bookId,
                                 @RequestParam Long readerId,
                                 Model model) {
        try {
            // Pass null for borrowDate and dueDate to match new service signature
            Borrowing borrowing = borrowingService.createBorrowing(bookId, readerId, null, null);
            return "redirect:/borrowings?success=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("borrowing", new Borrowing());
            model.addAttribute("readers", readerService.getAllReaders());
            model.addAttribute("availableBooks", bookService.getAvailableBooks());
            return "borrowings/form";
        }
    }
    
    // Hiển thị chi tiết giao dịch
    @GetMapping("/{id}")
    public String showBorrowingDetail(@PathVariable Long id, Model model) {
        Optional<Borrowing> borrowing = borrowingService.getBorrowingById(id);
        if (borrowing.isPresent()) {
            model.addAttribute("borrowing", borrowing.get());
            return "borrowings/detail";
        }
        return "redirect:/borrowings?error=notfound";
    }
    
    // Xử lý trả sách
    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id,
                           @RequestParam(defaultValue = "Tốt") String bookCondition) {
        try {
            borrowingService.returnBook(id, bookCondition);
            return "redirect:/borrowings/" + id + "?success=returned";
        } catch (Exception e) {
            return "redirect:/borrowings/" + id + "?error=" + e.getMessage();
        }
    }
    
    // Xử lý gia hạn
    @PostMapping("/{id}/renew")
    public String renewBorrowing(@PathVariable Long id,
                               @RequestParam(defaultValue = "14") int additionalDays) {
        try {
            borrowingService.renewBorrowing(id, additionalDays);
            return "redirect:/borrowings/" + id + "?success=renewed";
        } catch (Exception e) {
            return "redirect:/borrowings/" + id + "?error=" + e.getMessage();
        }
    }
    
    // Hiển thị sách quá hạn
    @GetMapping("/overdue")
    public String showOverdueBorrowings(Model model) {
        List<Borrowing> overdueBorrowings = borrowingService.getOverdueBorrowings();
        model.addAttribute("borrowings", overdueBorrowings);
        model.addAttribute("pageTitle", "Sách Quá Hạn");
        return "borrowings/overdue";
    }
    
    // Hiển thị sách sắp hết hạn
    @GetMapping("/due-soon")
    public String showDueSoonBorrowings(@RequestParam(defaultValue = "7") int days, Model model) {
        List<Borrowing> dueSoonBorrowings = borrowingService.getBorrowingsDueSoon(days);
        model.addAttribute("borrowings", dueSoonBorrowings);
        model.addAttribute("days", days);
        model.addAttribute("pageTitle", "Sách Sắp Hết Hạn");
        return "borrowings/due-soon";
    }
    
    // REST API Endpoints
    @RestController
    @RequestMapping("/api/borrowings")
    public static class BorrowingRestController {
        
        @Autowired
        private BorrowingService borrowingService;
        
        @Autowired
        private ReaderService readerService;
        
        @Autowired
        private BookService bookService;
        
        @GetMapping
        public ResponseEntity<Page<BorrowingWithDetailsDTO>> getAllBorrowings(Pageable pageable) {
            try {
                Page<Borrowing> borrowings = borrowingService.getAllBorrowings(pageable);
                Page<BorrowingWithDetailsDTO> dtos = borrowings.map(BorrowingWithDetailsDTO::new);
                return ResponseEntity.ok(dtos);
            } catch (Exception e) {
                System.err.println("Error getting borrowings: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Page.empty());
            }
        }
        
        @GetMapping("/{id}")
        public ResponseEntity<BorrowingWithDetailsDTO> getBorrowingById(@PathVariable Long id) {
            try {
                return borrowingService.getBorrowingById(id)
                    .map(BorrowingWithDetailsDTO::new)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
            } catch (Exception e) {
                System.err.println("Error getting borrowing by ID: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).build();
            }
        }
        
        @PostMapping
        public ResponseEntity<?> createBorrowing(@RequestBody CreateBorrowingRequest request) {
            try {
                System.out.println("Creating borrowing - bookId: " + request.getBookId() + 
                                 ", readerId: " + request.getReaderId() + 
                                 ", borrowDate: " + request.getBorrowDate() + 
                                 ", dueDate: " + request.getDueDate());
                
                Borrowing borrowing = borrowingService.createBorrowing(
                    request.getBookId(), 
                    request.getReaderId(), 
                    request.getBorrowDate(), 
                    request.getDueDate()
                );
                return ResponseEntity.ok(new BorrowingWithDetailsDTO(borrowing));
            } catch (BusinessLogicException | ResourceNotFoundException e) {
                System.err.println("Business error: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                System.err.println("System error: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau."));
            }
        }
        
        @PostMapping("/{id}/return")
        public ResponseEntity<?> returnBook(@PathVariable Long id, @RequestBody ReturnBookRequest request) {
            try {
                System.out.println("Returning book with ID: " + id + ", condition: " + request.getBookCondition());
                Borrowing borrowing = borrowingService.returnBook(id, request.getBookCondition());
                return ResponseEntity.ok(new BorrowingWithDetailsDTO(borrowing));
            } catch (BusinessLogicException | ResourceNotFoundException e) {
                System.err.println("Business error: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                System.err.println("System error: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau."));
            }
        }
        
        @PostMapping("/{id}/renew")
        public ResponseEntity<?> renewBorrowing(@PathVariable Long id, @RequestBody RenewBorrowingRequest request) {
            try {
                Borrowing borrowing = borrowingService.renewBorrowing(id, request.getAdditionalDays());
                return ResponseEntity.ok(new BorrowingWithDetailsDTO(borrowing));
            } catch (BusinessLogicException | ResourceNotFoundException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of("error", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau."));
            }
        }
        
        @GetMapping("/current")
        public ResponseEntity<List<BorrowingWithDetailsDTO>> getCurrentBorrowings() {
            try {
                List<Borrowing> borrowings = borrowingService.getCurrentBorrowings();
                List<BorrowingWithDetailsDTO> dtos = borrowings.stream()
                    .map(BorrowingWithDetailsDTO::new)
                    .toList();
                return ResponseEntity.ok(dtos);
            } catch (Exception e) {
                System.err.println("Error getting current borrowings: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(List.of());
            }
        }
        
        @GetMapping("/overdue")
        public ResponseEntity<List<BorrowingWithDetailsDTO>> getOverdueBorrowings() {
            try {
                List<Borrowing> borrowings = borrowingService.getOverdueBorrowings();
                List<BorrowingWithDetailsDTO> dtos = borrowings.stream()
                    .map(BorrowingWithDetailsDTO::new)
                    .toList();
                return ResponseEntity.ok(dtos);
            } catch (Exception e) {
                System.err.println("Error getting overdue borrowings: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(List.of());
            }
        }
        
        @GetMapping("/due-soon")
        public ResponseEntity<List<BorrowingWithDetailsDTO>> getBorrowingsDueSoon(
                @RequestParam(defaultValue = "7") int days) {
            try {
                List<Borrowing> borrowings = borrowingService.getBorrowingsDueSoon(days);
                List<BorrowingWithDetailsDTO> dtos = borrowings.stream()
                    .map(BorrowingWithDetailsDTO::new)
                    .toList();
                return ResponseEntity.ok(dtos);
            } catch (Exception e) {
                System.err.println("Error getting due soon borrowings: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(List.of());
            }
        }
        
        @GetMapping("/statistics")
        public ResponseEntity<Object> getBorrowingStatistics() {
            try {
                return ResponseEntity.ok(new Object() {
                    public final Long totalBorrowings = borrowingService.getTotalBorrowingsCount();
                    public final Long currentBorrowings = borrowingService.getCurrentBorrowingsCount();
                    public final Long overdueBorrowings = borrowingService.getOverdueBorrowingsCount();
                    public final Long returnedBorrowings = borrowingService.getReturnedBorrowingsCount();
                });
            } catch (Exception e) {
                System.err.println("Error getting statistics: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Không thể lấy thống kê"));
            }
        }
        
        @GetMapping("/reader/{readerId}")
        public ResponseEntity<?> getBorrowingsByReader(@PathVariable Long readerId) {
            try {
                Optional<Reader> reader = readerService.getReaderById(readerId);
                if (reader.isPresent()) {
                    List<Borrowing> borrowings = borrowingService.getBorrowingsByReader(reader.get());
                    List<BorrowingWithDetailsDTO> dtos = borrowings.stream()
                        .map(BorrowingWithDetailsDTO::new)
                        .toList();
                    return ResponseEntity.ok(dtos);
                }
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy độc giả với ID: " + readerId));
            } catch (Exception e) {
                System.err.println("Error getting borrowings by reader: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau."));
            }
        }
        
        @GetMapping("/book/{bookId}")
        public ResponseEntity<?> getBorrowingsByBook(@PathVariable Long bookId) {
            try {
                Optional<Book> book = bookService.getBookById(bookId);
                if (book.isPresent()) {
                    List<Borrowing> borrowings = borrowingService.getBorrowingsByBook(book.get());
                    List<BorrowingWithDetailsDTO> dtos = borrowings.stream()
                        .map(BorrowingWithDetailsDTO::new)
                        .toList();
                    return ResponseEntity.ok(dtos);
                }
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy sách với ID: " + bookId));
            } catch (Exception e) {
                System.err.println("Error getting borrowings by book: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Có lỗi xảy ra trong hệ thống. Vui lòng thử lại sau."));
            }
        }
        
        @GetMapping("/all")
        public ResponseEntity<List<BorrowingWithDetailsDTO>> getAllBorrowingsHistory() {
            try {
                List<Borrowing> borrowings = borrowingService.getAllBorrowings();
                List<BorrowingWithDetailsDTO> dtos = borrowings.stream()
                    .map(BorrowingWithDetailsDTO::new)
                    .toList();
                return ResponseEntity.ok(dtos);
            } catch (Exception e) {
                System.err.println("Error getting all borrowings: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(List.of());
            }
        }
    }
    
    // Request DTOs
    public static class CreateBorrowingRequest {
        private Long bookId;
        private Long readerId;
        private LocalDate borrowDate;
        private LocalDate dueDate;
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getReaderId() { return readerId; }
        public void setReaderId(Long readerId) { this.readerId = readerId; }
        public LocalDate getBorrowDate() { return borrowDate; }
        public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    }
    
    public static class ReturnBookRequest {
        private String bookCondition = "good";
        
        public String getBookCondition() { return bookCondition; }
        public void setBookCondition(String bookCondition) { this.bookCondition = bookCondition; }
    }
    
    public static class RenewBorrowingRequest {
        private int additionalDays = 14;
        
        public int getAdditionalDays() { return additionalDays; }
        public void setAdditionalDays(int additionalDays) { this.additionalDays = additionalDays; }
    }
}