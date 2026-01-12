package com.hotel.service;

import com.hotel.model.AdminUser;
import com.hotel.model.AuditLog;
import com.hotel.repository.AuditLogRepository;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;

// handles activity logging to the database
// creates audit log entries for all admin actions
public class ActivityLogService {
    
    private AuditLogRepository auditLogRepository;
    private LoggerService logger;
    private EntityManager em;
    
    public ActivityLogService(EntityManager em) {
        this.em = em;
        this.auditLogRepository = new AuditLogRepository(em);
        this.logger = LoggerService.getInstance();
    }
    
    // logs an admin action to the database
    // also writes to log file for backup
    public void logActivity(String actor, String action, String entityType, Long entityId, String message) {
        em.getTransaction().begin();
        
        try {
            AuditLog auditLog = new AuditLog(actor, action, entityType, entityId, message);
            auditLogRepository.save(auditLog);
            
            em.getTransaction().commit();
            
            // also write to log file
            logger.logActivity(actor, action, entityType, entityId, message);
            
        } catch (Exception e) {
            em.getTransaction().rollback();
            logger.logError("Failed to log activity", e);
        }
    }
    
    // logs an activity using an adminuser object
    // extracts the username automatically
    public void logActivity(AdminUser admin, String action, String entityType, Long entityId, String message) {
        logActivity(admin.getUsername(), action, entityType, entityId, message);
    }
}



