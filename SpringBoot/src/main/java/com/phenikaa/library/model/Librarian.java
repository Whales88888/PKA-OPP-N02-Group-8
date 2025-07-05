package com.phenikaa.library.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "librarians")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Librarian {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tên thủ thư không được để trống")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(nullable = false)
    private String password;
    
    private String phone;
    
    @Column(name = "employee_id")
    private String employeeId;
    
    @Enumerated(EnumType.STRING)
    private LibrarianRole role = LibrarianRole.LIBRARIAN;
    
    @Enumerated(EnumType.STRING)
    private LibrarianStatus status = LibrarianStatus.ACTIVE;
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    private String department;
    
    @Column(name = "work_shift")
    private String workShift;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Thông báo
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Notification> notifications;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (hireDate == null) {
            hireDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isActive() {
        return status == LibrarianStatus.ACTIVE;
    }
    
    public boolean hasRole(LibrarianRole requiredRole) {
        return this.role == requiredRole || this.role == LibrarianRole.ADMIN;
    }
    
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    public String getDisplayName() {
        return name + " (" + role.getDisplayName() + ")";
    }
    
    public enum LibrarianRole {
        ADMIN("Quản trị viên"),
        MANAGER("Quản lý"),
        LIBRARIAN("Thủ thư"),
        ASSISTANT("Trợ lý");
        
        private final String displayName;
        
        LibrarianRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum LibrarianStatus {
        ACTIVE("Hoạt động"),
        INACTIVE("Không hoạt động"),
        SUSPENDED("Tạm khóa");
        
        private final String displayName;
        
        LibrarianStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public void setId(long id) { this.id = id; }
    public long getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}