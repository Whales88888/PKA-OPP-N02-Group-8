package com.example.library.service;

import com.example.library.model.BorrowingSlip;
import java.util.Date;

public class DueDateChecker {
    public static boolean isNearDueDate(BorrowingSlip slip) {
        Date now = new Date();
        long diff = slip.getDueDate().getTime() - now.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        return days <= 3;
    }
}
