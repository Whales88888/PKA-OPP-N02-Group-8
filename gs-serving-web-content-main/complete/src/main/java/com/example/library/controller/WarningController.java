package com.example.library.controller;

import com.example.library.model.BorrowingSlip;
import com.example.library.model.Borrower;
import com.example.library.model.Book;
import com.example.library.service.BorrowingSlipService;
import com.example.library.service.DueDateChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class WarningController {
    @Autowired
    private BorrowingSlipService slipService;

    // Dữ liệu mẫu
    private List<BorrowingSlip> getSampleSlips() {
        Borrower b = new Borrower("BD001", "Nguyen Van A", "0123456789");
        Book book1 = new Book("B001", "Lập trình Java", "Nguyen Van Anh");
        Book book2 = new Book("B002", "Cấu trúc dữ liệu", "Nguyen Van Binh");
        Date now = new Date();
        Date dueSoon = new Date(now.getTime() + 2L * 24 * 60 * 60 * 1000); // còn 2 ngày
        Date dueLate = new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000); // còn 7 ngày
        return Arrays.asList(
            new BorrowingSlip("S001", b, book1, now, dueSoon),
            new BorrowingSlip("S002", b, book2, now, dueLate)
        );
    }

    @GetMapping("/near-due")
    public String getNearDueBooks(@RequestParam String borrowerID, Model model) {
        List<BorrowingSlip> slips = slipService.getSlipsByBorrowerID(getSampleSlips(), borrowerID)
            .stream()
            .filter(DueDateChecker::isNearDueDate)
            .toList();
        model.addAttribute("slips", slips);
        return "near-due";
    }
}
