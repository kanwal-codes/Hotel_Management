package com.hotel.repository;

import com.hotel.model.AdminUser;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

// repository for adminuser database operations
public class AdminUserRepository {
    private EntityManager em;
    
    public AdminUserRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves an admin user to the database
    public AdminUser save(AdminUser adminUser) {
        if (adminUser.getId() == null) {
            em.persist(adminUser);
        } else {
            adminUser = em.merge(adminUser);
        }
        return adminUser;
    }
    
    // finds an admin user by their id
    public Optional<AdminUser> findById(Long id) {
        return Optional.ofNullable(em.find(AdminUser.class, id));
    }
    
    // finds an admin user by username
    // used during login to find the user account
    public Optional<AdminUser> findByUsername(String username) {
        TypedQuery<AdminUser> query = em.createQuery(
            "SELECT a FROM AdminUser a WHERE a.username = :username", AdminUser.class);
        query.setParameter("username", username);
        List<AdminUser> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // finds an admin user by email address
    public Optional<AdminUser> findByEmail(String email) {
        TypedQuery<AdminUser> query = em.createQuery(
            "SELECT a FROM AdminUser a WHERE a.email = :email", AdminUser.class);
        query.setParameter("email", email);
        List<AdminUser> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // gets all admin users in the database
    public List<AdminUser> findAll() {
        TypedQuery<AdminUser> query = em.createQuery("SELECT a FROM AdminUser a", AdminUser.class);
        return query.getResultList();
    }
    
    // gets only active admin users (not disabled accounts)
    public List<AdminUser> findActive() {
        TypedQuery<AdminUser> query = em.createQuery(
            "SELECT a FROM AdminUser a WHERE a.active = true", AdminUser.class);
        return query.getResultList();
    }
}



