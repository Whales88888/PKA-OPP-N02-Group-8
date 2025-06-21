package com.example.library.repository;

import com.example.library.model.BorrowingSlip;
import com.example.library.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BorrowingSlipRepository extends JpaRepository<BorrowingSlip, Long> {
    List<BorrowingSlip> findByBorrower(Borrower borrower);
    List<BorrowingSlip> findByDueDateBetween(LocalDate from, LocalDate to);
}