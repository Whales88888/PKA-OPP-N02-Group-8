package com.example.library.service;

import com.example.library.model.BorrowingSlip;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BorrowingSlipService {
    public List<BorrowingSlip> getSlipsByBorrowerID(List<BorrowingSlip> slips, String borrowerID) {
        return slips.stream()
            .filter(slip -> slip.getBorrower().getBorrowerID().equals(borrowerID))
            .collect(Collectors.toList());
    }
}
