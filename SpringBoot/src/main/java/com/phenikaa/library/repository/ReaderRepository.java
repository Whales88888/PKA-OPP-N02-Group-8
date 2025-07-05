package com.phenikaa.library.repository;

import com.phenikaa.library.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    // Find by email
    Optional<Reader> findByEmail(String email);
    // Find by phone
    Optional<Reader> findByPhone(String phone);
    // Find by name (search)
    List<Reader> findByNameContainingIgnoreCase(String name);
    // Find all active/inactive
    List<Reader> findByIsActiveTrue();
    List<Reader> findByIsActiveFalse();
    // Simple search by name/email
    @Query("SELECT r FROM Reader r WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (:email IS NULL OR LOWER(r.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Reader> searchReaders(@Param("name") String name, @Param("email") String email, Pageable pageable);
    // Statistics
    @Query("SELECT COUNT(r) FROM Reader r")
    Long countTotalReaders();
    @Query("SELECT COUNT(r) FROM Reader r WHERE r.isActive = true")
    Long countActiveReaders();
    @Query("SELECT COUNT(r) FROM Reader r WHERE r.isActive = false")
    Long countInactiveReaders();
    // Recently registered
    @Query("SELECT r FROM Reader r ORDER BY r.registrationDate DESC")
    List<Reader> findRecentlyRegistered(Pageable pageable);
    // Email/phone existence
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    // New readers this month
    @Query("SELECT COUNT(r) FROM Reader r WHERE MONTH(r.registrationDate) = MONTH(CURRENT_DATE) AND YEAR(r.registrationDate) = YEAR(CURRENT_DATE)")
    long countNewReadersThisMonth();
}