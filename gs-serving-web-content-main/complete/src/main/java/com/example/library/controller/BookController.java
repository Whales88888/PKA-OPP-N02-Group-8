package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public String listBooks(Model model) {
        try {
            model.addAttribute("books", bookService.getAllBooks());
            return "books";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
        }
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book, Model model) {
        try {
            bookService.addBook(book);
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
        }
    }
}