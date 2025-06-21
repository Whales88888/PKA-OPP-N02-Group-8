package com.example.library.service;

import com.example.library.model.BorrowingSlip;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BorrowingSlipService {
    public List<BorrowingSlip> getSlipsByBorrowerID(List<BorrowingSlip> slips, String borrowerID) {
        return slips.stream()
            .filter(slip -> {
            if (slip.getBorrower() != null && slip.getBorrower().getId() != null) {
                return slip.getBorrower().getId().equals(borrowerID);
            }
            return false;
            })
            .collect(Collectors.toList());
    }
}
