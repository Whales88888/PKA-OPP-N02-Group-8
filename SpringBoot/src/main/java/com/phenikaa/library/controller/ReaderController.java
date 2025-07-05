package com.phenikaa.library.controller;

import com.phenikaa.library.model.Reader;
import com.phenikaa.library.service.ReaderService;
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
@RequestMapping("/readers")
public class ReaderController {
    
    @Autowired
    private ReaderService readerService;
    
    // Hiển thị danh sách độc giả
    @GetMapping
    public String listReaders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "name") String sortBy,
                             @RequestParam(defaultValue = "asc") String sortDir,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) String email,
                             @RequestParam(required = false) Boolean isActive,
                             Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Reader> readerPage = readerService.searchReaders(search, email, pageable);
        
        model.addAttribute("readers", readerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", readerPage.getTotalPages());
        model.addAttribute("totalElements", readerPage.getTotalElements());
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentEmail", email);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        
        return "readers/list";
    }
    
    // Hiển thị form thêm độc giả mới
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reader", new Reader());
        return "readers/form";
    }
    
    // Xử lý thêm độc giả mới
    @PostMapping
    public String createReader(@Valid @ModelAttribute Reader reader, 
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "readers/form";
        }
        
        try {
            readerService.saveReader(reader);
            return "redirect:/readers?success=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "readers/form";
        }
    }
    
    // Hiển thị chi tiết độc giả
    @GetMapping("/{id}")
    public String showReaderDetail(@PathVariable Long id, Model model) {
        Optional<Reader> reader = readerService.getReaderById(id);
        if (reader.isPresent()) {
            model.addAttribute("reader", reader.get());
            return "readers/detail";
        }
        return "redirect:/readers?error=notfound";
    }
    
    // Hiển thị form chỉnh sửa độc giả
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Reader> reader = readerService.getReaderById(id);
        if (reader.isPresent()) {
            model.addAttribute("reader", reader.get());
            return "readers/form";
        }
        return "redirect:/readers?error=notfound";
    }
    
    // Xử lý cập nhật độc giả
    @PostMapping("/{id}")
    public String updateReader(@PathVariable Long id, 
                              @Valid @ModelAttribute Reader reader,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            reader.setId(id);
            return "readers/form";
        }
        
        try {
            readerService.updateReader(id, reader);
            return "redirect:/readers/" + id + "?success=updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            reader.setId(id);
            return "readers/form";
        }
    }
    
    // Xóa độc giả
    @PostMapping("/{id}/delete")
    public String deleteReader(@PathVariable Long id) {
        try {
            readerService.deleteReader(id);
            return "redirect:/readers?success=deleted";
        } catch (Exception e) {
            return "redirect:/readers?error=" + e.getMessage();
        }
    }
    
    // REST API Endpoints
    @RestController
    @RequestMapping("/api/readers")
    public static class ReaderRestController {
        
        @Autowired
        private ReaderService readerService;
        
        @GetMapping
        public ResponseEntity<List<Reader>> getAllReaders() {
            return ResponseEntity.ok(readerService.getAllReaders());
        }
        
        @GetMapping("/{id}")
        public ResponseEntity<Reader> getReaderById(@PathVariable Long id) {
            return readerService.getReaderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        }
        
        @PostMapping
        public ResponseEntity<Reader> createReader(@Valid @RequestBody Reader reader) {
            try {
                Reader savedReader = readerService.saveReader(reader);
                return ResponseEntity.ok(savedReader);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        @PutMapping("/{id}")
        public ResponseEntity<Reader> updateReader(@PathVariable Long id, 
                                                  @Valid @RequestBody Reader reader) {
            try {
                Reader updated = readerService.updateReader(id, reader);
                return ResponseEntity.ok(updated);
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }
        
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
            try {
                readerService.deleteReader(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.notFound().build();
            }
        }
        
        @GetMapping("/search")
        public ResponseEntity<Page<Reader>> searchReaders(
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String email,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Reader> result = readerService.searchReaders(name, email, pageable);
            return ResponseEntity.ok(result);
        }
        
        @GetMapping("/recent")
        public ResponseEntity<List<Reader>> getRecentlyRegistered(@RequestParam(defaultValue = "10") int limit) {
            return ResponseEntity.ok(readerService.getRecentlyRegistered(limit));
        }
        
        @GetMapping("/statistics")
        public ResponseEntity<Object> getReaderStatistics() {
            return ResponseEntity.ok(new Object() {
                public final Long totalReaders = readerService.getTotalReadersCount();
                public final Long activeReaders = readerService.getActiveReadersCount();
                public final Long inactiveReaders = readerService.getInactiveReadersCount();
                public final Long newReadersThisMonth = readerService.getNewReadersThisMonth();
            });
        }
    }
}