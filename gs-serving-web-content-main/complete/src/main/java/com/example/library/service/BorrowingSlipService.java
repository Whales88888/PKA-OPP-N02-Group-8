package com.example.library.service;

import com.example.library.model.BorrowingSlip;
import com.example.library.model.Borrower;
import com.example.library.repository.BorrowingSlipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingSlipService {
    @Autowired
    private BorrowingSlipRepository borrowingSlipRepository;

    public List<BorrowingSlip> getBorrowingSlipsByBorrower(Borrower borrower) {
        try {
            return borrowingSlipRepository.findByBorrower(borrower);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy phiếu mượn", e);
        } finally {
        }
    }

    public List<BorrowingSlip> getSlipsNearDueDate(LocalDate from, LocalDate to) {
        try {
            return borrowingSlipRepository.findByDueDateBetween(from, to);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy phiếu mượn gần đến hạn", e);
        } finally {
        }
    }

    public BorrowingSlip addBorrowingSlip(BorrowingSlip slip) {
        try {
            return borrowingSlipRepository.save(slip);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm phiếu mượn", e);
        } finally {
        }
    }
}