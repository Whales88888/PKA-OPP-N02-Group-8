package com.example.library.service;

import com.example.library.model.BorrowingSlip;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DueDateCheckerTest {

    @Test
    void isNearDueDateReturnsTrueIfWithin3Days() {
        BorrowingSlip slip = new BorrowingSlip();
        slip.setDueDate(LocalDate.now().plusDays(2));
        DueDateChecker checker = new DueDateChecker();
        assertTrue(checker.isNearDueDate(slip));
    }

    @Test
    void isNearDueDateReturnsFalseIfNotWithin3Days() {
        BorrowingSlip slip = new BorrowingSlip();
        slip.setDueDate(LocalDate.now().plusDays(10));
        DueDateChecker checker = new DueDateChecker();
        assertFalse(checker.isNearDueDate(slip));
    }
}
