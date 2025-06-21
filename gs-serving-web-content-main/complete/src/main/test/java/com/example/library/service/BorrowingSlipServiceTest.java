package com.example.library.service;

import com.example.library.model.Borrower;
import com.example.library.model.BorrowingSlip;
import com.example.library.repository.BorrowingSlipRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BorrowingSlipServiceTest {

    @Test
    void getBorrowingSlipsByBorrowerReturnsList() {
        BorrowingSlipRepository repo = mock(BorrowingSlipRepository.class);
        Borrower borrower = new Borrower();
        when(repo.findByBorrower(borrower)).thenReturn(Collections.emptyList());

        BorrowingSlipService service = new BorrowingSlipService();
        service.borrowingSlipRepository = repo;

        List<BorrowingSlip> result = service.getBorrowingSlipsByBorrower(borrower);
        assertNotNull(result);
    }

    @Test
    void getSlipsNearDueDateReturnsList() {
        BorrowingSlipRepository repo = mock(BorrowingSlipRepository.class);
        when(repo.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        BorrowingSlipService service = new BorrowingSlipService();
        service.borrowingSlipRepository = repo;

        List<BorrowingSlip> result = service.getSlipsNearDueDate(LocalDate.now(), LocalDate.now().plusDays(3));
        assertNotNull(result);
    }
}
