package com.example.library.controller;

import com.example.library.model.BorrowingSlip;
import com.example.library.model.Borrower;
import com.example.library.service.BorrowingSlipService;
import com.example.library.service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/borrowingslips")
public class BorrowingSlipController {
    @Autowired
    private BorrowingSlipService borrowingSlipService;
    @Autowired
    private BorrowerService borrowerService;

    @GetMapping
    public String listBorrowingSlips(Model model) {
        try {
            model.addAttribute("borrowingslips", borrowingSlipService.getSlipsNearDueDate(LocalDate.now(), LocalDate.now().plusDays(3)));
            return "borrowingslips";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
        }
    }

    @GetMapping("/by-borrower/{borrowerId}")
    public String listByBorrower(@PathVariable Long borrowerId, Model model) {
        try {
            Borrower borrower = borrowerService.getAllBorrowers().stream()
                .filter(b -> b.getId().equals(borrowerId)).findFirst().orElse(null);
            if (borrower == null) throw new RuntimeException("Không tìm thấy bạn đọc");
            List<BorrowingSlip> slips = borrowingSlipService.getBorrowingSlipsByBorrower(borrower);
            model.addAttribute("borrowingslips", slips);
            return "borrowingslips";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        } finally {
        }
    }
}