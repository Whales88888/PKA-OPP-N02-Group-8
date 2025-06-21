package com.example.library.controller;

import com.example.library.model.Borrower;
import com.example.library.service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/borrowers")
public class BorrowerController {
    @Autowired
    private BorrowerService borrowerService;

    @GetMapping
    public String listBorrowers(Model model) {
        try {
            model.addAttribute("borrowers", borrowerService.getAllBorrowers());
            return "borrowers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
        }
    }

    @PostMapping("/add")
    public String addBorrower(@ModelAttribute Borrower borrower, Model model) {
        try {
            borrowerService.addBorrower(borrower);
            return "redirect:/borrowers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
        }
    }
}