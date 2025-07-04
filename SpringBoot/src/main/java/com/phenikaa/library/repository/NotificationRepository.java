package com.phenikaa.library.repository;

import com.phenikaa.library.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Tìm thông báo theo người dùng
    List<Notification> findByTargetUserIdAndTargetUserType(Long userId, Notification.UserType userType);
    
    // Tìm thông báo chưa đọc
    List<Notification> findByTargetUserIdAndTargetUserTypeAndIsReadFalse(Long userId, Notification.UserType userType);
    
    // Tìm thông báo theo loại
    List<Notification> findByType(Notification.NotificationType type);
    
    // Tìm thông báo theo mức độ ưu tiên
    List<Notification> findByPriority(Notification.NotificationPriority priority);
    
    // Tìm thông báo chưa hết hạn
    @Query("SELECT n FROM Notification n WHERE n.expiresAt IS NULL OR n.expiresAt > CURRENT_TIMESTAMP")
    List<Notification> findActiveNotifications();
    
    // Đếm thông báo chưa đọc của người dùng
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.targetUserId = :userId AND n.targetUserType = :userType AND n.isRead = false")
    Long countUnreadNotifications(@Param("userId") Long userId, @Param("userType") Notification.UserType userType);
    
    // Thông báo gần đây
    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(Pageable pageable);

    List<Notification> findByIsReadFalse();
}