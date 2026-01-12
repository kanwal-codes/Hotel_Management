package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Stores a log entry for every admin action in the system.
 * Creates an audit trail so we can see who did what and when.
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // When this action happened
    @Column(nullable = false)
    @NotNull
    private LocalDateTime timestamp;
    
    // Who performed the action (username)
    @Column(nullable = false, length = 100)
    @NotNull
    private String actor;
    
    // What action was performed (like "CREATE_RESERVATION", "APPLY_DISCOUNT")
    @Column(nullable = false, length = 100)
    @NotNull
    private String action;
    
    // What type of entity was affected (like "Reservation", "Billing")
    @Column(name = "entity_type", nullable = false, length = 100)
    @NotNull
    private String entityType;
    
    // ID of the specific entity that was affected
    @Column(name = "entity_id")
    private Long entityId;
    
    // Descriptive message about what happened
    @Column(length = 500)
    private String message;
    
    // Link to the admin user who performed the action (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;
    
    // Default constructor - sets timestamp automatically
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor for creating new audit log entries
    public AuditLog(String actor, String action, String entityType, Long entityId, String message) {
        this.actor = actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getActor() {
        return actor;
    }
    
    public void setActor(String actor) {
        this.actor = actor;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public AdminUser getAdminUser() {
        return adminUser;
    }
    
    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }
}



