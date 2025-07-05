package com.phenikaa.library.controller;

import com.phenikaa.library.model.Book;
import com.phenikaa.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    // Hiển thị danh sách sách
    @GetMapping
    public String listBooks(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "title") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(required = false) String search,
                           @RequestParam(required = false) String category,
                           Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> bookPage;
        if (search != null && !search.trim().isEmpty()) {
            bookPage = bookService.searchBooks(search, null, category, null, pageable);
        } else if (category != null && !category.trim().isEmpty()) {
            bookPage = bookService.searchBooks(null, null, category, null, pageable);
        } else {
            bookPage = bookService.searchBooks(null, null, null, null, pageable);
        }
        
        List<String> categories = bookService.getAllCategories();
        
        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalElements", bookPage.getTotalElements());
        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentSearch", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        
        return "books/list";
    }
    
    // Hiển thị form thêm sách mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", bookService.getAllCategories());
        model.addAttribute("publishers", bookService.getAllPublishers());
        return "books/form";
    }
    
    // Xử lý thêm sách mới
    @PostMapping
    public String createBook(@Valid @ModelAttribute Book book, 
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", bookService.getAllCategories());
            model.addAttribute("publishers", bookService.getAllPublishers());
            return "books/form";
        }
        
        try {
            bookService.saveBook(book);
            return "redirect:/books?success=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", bookService.getAllCategories());
            model.addAttribute("publishers", bookService.getAllPublishers());
            return "books/form";
        }
    }
    
    // Hiển thị chi tiết sách
    @GetMapping("/{id}")
    public String showBookDetail(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "books/detail";
        }
        return "redirect:/books?error=notfound";
    }
    
    // Hiển thị form chỉnh sửa sách
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            model.addAttribute("categories", bookService.getAllCategories());
            model.addAttribute("publishers", bookService.getAllPublishers());
            return "books/form";
        }
        return "redirect:/books?error=notfound";
    }
    
    // Xử lý cập nhật sách
    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id, 
                           @Valid @ModelAttribute Book book,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            book.setId(id);
            model.addAttribute("categories", bookService.getAllCategories());
            model.addAttribute("publishers", bookService.getAllPublishers());
            return "books/form";
        }
        
        try {
            bookService.updateBook(id, book);
            return "redirect:/books/" + id + "?success=updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            book.setId(id);
            model.addAttribute("categories", bookService.getAllCategories());
            model.addAttribute("publishers", bookService.getAllPublishers());
            return "books/form";
        }
    }
    
    // Xóa sách
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return "redirect:/books?success=deleted";
        } catch (Exception e) {
            return "redirect:/books?error=" + e.getMessage();
        }
    }
    
    // REST API Endpoints
    @RestController
    @RequestMapping("/api/books")
    public static class BookRestController {
        
        @Autowired
        private BookService bookService;
        
        @GetMapping
        public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable) {
            return ResponseEntity.ok(bookService.getAllBooks(pageable));
        }
        
        @GetMapping("/{id}")
        public ResponseEntity<Book> getBookById(@PathVariable Long id) {
            return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        }
        
        @PostMapping
        public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
            try {
                Book savedBook = bookService.saveBook(book);
                return ResponseEntity.ok(savedBook);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        @PutMapping("/{id}")
        public ResponseEntity<Book> updateBook(@PathVariable Long id, 
                                             @Valid @RequestBody Book book) {
            try {
                Book updatedBook = bookService.updateBook(id, book);
                return ResponseEntity.ok(updatedBook);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
            try {
                bookService.deleteBook(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        @GetMapping("/search")
        public ResponseEntity<Page<Book>> searchBooks(
                @RequestParam(required = false) String title,
                @RequestParam(required = false) String author,
                @RequestParam(required = false) String category,
                @RequestParam(required = false) String isbn,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> books = bookService.searchBooks(title, author, category, isbn, pageable);
            return ResponseEntity.ok(books);
        }
        
        @GetMapping("/categories")
        public ResponseEntity<List<String>> getCategories() {
            return ResponseEntity.ok(bookService.getAllCategories());
        }
        
        @GetMapping("/available")
        public ResponseEntity<List<Book>> getAvailableBooks() {
            return ResponseEntity.ok(bookService.getAvailableBooks());
        }
        
        @GetMapping("/statistics")
        public ResponseEntity<Object> getBookStatistics() {
            return ResponseEntity.ok(new Object() {
                public final Long totalBooks = bookService.getTotalBooksCount();
                public final Long availableBooks = bookService.getAvailableBooksCount();
                public final Long borrowedBooks = bookService.getBorrowedBooksCount();
            });
        }
    }
}