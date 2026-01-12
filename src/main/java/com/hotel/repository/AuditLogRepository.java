package com.hotel.repository;

import com.hotel.model.AuditLog;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// repository for auditlog database operations
public class AuditLogRepository {
    private EntityManager em;
    
    public AuditLogRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves an audit log entry to the database
    public AuditLog save(AuditLog auditLog) {
        if (auditLog.getId() == null) {
            em.persist(auditLog);
        } else {
            auditLog = em.merge(auditLog);
        }
        return auditLog;
    }
    
    // finds an audit log entry by its id
    public Optional<AuditLog> findById(Long id) {
        return Optional.ofNullable(em.find(AuditLog.class, id));
    }
    
    // gets all audit log entries, newest first
    public List<AuditLog> findAll() {
        TypedQuery<AuditLog> query = em.createQuery("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC", AuditLog.class);
        return query.getResultList();
    }
    
    // gets all audit log entries for a specific admin user
    // shows what actions that admin has performed
    public List<AuditLog> findByActor(String actor) {
        TypedQuery<AuditLog> query = em.createQuery(
            "SELECT a FROM AuditLog a WHERE a.actor = :actor ORDER BY a.timestamp DESC", AuditLog.class);
        query.setParameter("actor", actor);
        return query.getResultList();
    }
    
    // gets audit log entries within a date range
    // useful for generating activity reports for specific time periods
    public List<AuditLog> findByDateRange(LocalDateTime start, LocalDateTime end) {
        TypedQuery<AuditLog> query = em.createQuery(
            "SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC", AuditLog.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }
}



