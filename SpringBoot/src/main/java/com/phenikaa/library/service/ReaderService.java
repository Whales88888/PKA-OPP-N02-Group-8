package com.phenikaa.library.service;

import com.phenikaa.library.exception.ResourceNotFoundException;
import com.phenikaa.library.model.Reader;
import com.phenikaa.library.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReaderService {
    @Autowired
    private ReaderRepository readerRepository;

    // CRUD Operations
    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }

    public Optional<Reader> getReaderById(Long id) {
        return readerRepository.findById(id);
    }

    public Reader saveReader(Reader reader) {
        validateReader(reader);
        return readerRepository.save(reader);
    }

    public Reader updateReader(Long id, Reader readerDetails) {
        return readerRepository.findById(id)
            .map(reader -> {
                reader.setName(readerDetails.getName());
                reader.setEmail(readerDetails.getEmail());
                reader.setPhone(readerDetails.getPhone());
                reader.setAddress(readerDetails.getAddress());
                reader.setIsActive(readerDetails.getIsActive());
                return readerRepository.save(reader);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Độc giả", "ID", id));
    }

    public void deleteReader(Long id) {
        Reader reader = readerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Độc giả", "ID", id));
        readerRepository.delete(reader);
    }

    // Search Operations
    public Optional<Reader> findByEmail(String email) {
        return readerRepository.findByEmail(email);
    }

    public List<Reader> searchReadersByName(String name) {
        return readerRepository.findByNameContainingIgnoreCase(name);
    }

    public Page<Reader> searchReaders(String name, String email, Pageable pageable) {
        return readerRepository.searchReaders(name, email, pageable);
    }

    public List<Reader> getActiveReaders() {
        return readerRepository.findByIsActiveTrue();
    }

    public List<Reader> getInactiveReaders() {
        return readerRepository.findByIsActiveFalse();
    }

    public List<Reader> getRecentlyRegistered(int limit) {
        return readerRepository.findRecentlyRegistered(Pageable.ofSize(limit));
    }

    // Statistics
    public Long getTotalReadersCount() {
        return readerRepository.countTotalReaders();
    }

    public Long getActiveReadersCount() {
        return readerRepository.countActiveReaders();
    }

    public Long getInactiveReadersCount() {
        return readerRepository.countInactiveReaders();
    }

    public Long getNewReadersThisMonth() {
        return readerRepository.countNewReadersThisMonth();
    }

    // Validation
    private void validateReader(Reader reader) {
        if (reader.getName() == null || reader.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên độc giả không được để trống");
        }
        if (reader.getEmail() == null || reader.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (!isValidEmail(reader.getEmail())) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (reader.getId() == null && readerRepository.existsByEmail(reader.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }
        if (reader.getPhone() != null && !reader.getPhone().trim().isEmpty()) {
            if (!isValidPhone(reader.getPhone())) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ");
            }
            if (reader.getId() == null && readerRepository.existsByPhone(reader.getPhone())) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại trong hệ thống");
            }
        }
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    private boolean isValidPhone(String phone) {
        return phone.matches("^[0-9]{10,11}$");
    }
}