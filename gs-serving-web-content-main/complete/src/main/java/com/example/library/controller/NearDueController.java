package com.example.library.controller;

import com.example.library.service.BorrowingSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class NearDueController {

    @Autowired
    private BorrowingSlipService borrowingSlipService;

    @GetMapping("/near-due")
    public String nearDueBooks(Model model) {
        try {
            model.addAttribute("slips", borrowingSlipService.getSlipsNearDueDate(LocalDate.now(), LocalDate.now().plusDays(3)));
            return "near-due";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
            // Optional: resource cleanup if needed
        }
    }
}