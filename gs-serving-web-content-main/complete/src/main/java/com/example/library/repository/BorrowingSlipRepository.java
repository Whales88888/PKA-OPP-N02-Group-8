package com.example.library.repository;

import com.example.library.model.BorrowingSlip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingSlipRepository extends JpaRepository<BorrowingSlip, String> {}
