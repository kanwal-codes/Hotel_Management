package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an admin user who can log in and manage the system.
 * Stores hashed password, not plain text.
 */
@Entity
@Table(name = "admin_users")
public class AdminUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Login username - must be unique
    @Column(nullable = false, unique = true, length = 50)
    @NotNull
    private String username;
    
    // Email address - must be unique
    @Column(nullable = false, unique = true, length = 100)
    @NotNull
    private String email;
    
    // Password hash - never store plain passwords
    @Column(name = "password", nullable = false, length = 255)
    @NotNull
    private String passwordHash;
    
    // Role determines permissions: ADMIN or MANAGER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Role role;
    
    // Whether this account is active or disabled
    @Column(nullable = false)
    private boolean active = true;
    
    // All actions this admin has performed (for audit trail)
    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs = new ArrayList<>();
    
    // Default constructor required by JPA
    public AdminUser() {
    }
    
    // Constructor for creating new admin users
    public AdminUser(String username, String email, String passwordHash, Role role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }
    
    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }
}



