package com.example.library.service;

import com.example.library.model.Borrower;
import com.example.library.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BorrowerService {
    @Autowired
    private BorrowerRepository borrowerRepository;

    public List<Borrower> getAllBorrowers() {
        try {
            return borrowerRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách bạn đọc", e);
        } finally {
        }
    }

    public Borrower addBorrower(Borrower borrower) {
        try {
            return borrowerRepository.save(borrower);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm bạn đọc", e);
        } finally {
        }
    }
}