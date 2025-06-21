package com.example.library.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class BorrowingSlipTest {

    @Test
    void testBorrowingSlipConstructorAndGetters() {
        Borrower borrower = new Borrower("Name");
        Book book = new Book("Title", "Author");
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(7);

        BorrowingSlip slip = new BorrowingSlip(borrower, book, borrowDate, dueDate);

        assertEquals(borrower, slip.getBorrower());
        assertEquals(book, slip.getBook());
        assertEquals(borrowDate, slip.getBorrowDate());
        assertEquals(dueDate, slip.getDueDate());
    }
}
