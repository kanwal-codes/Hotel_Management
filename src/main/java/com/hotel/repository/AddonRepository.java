package com.hotel.repository;

import com.hotel.model.ServiceAddon;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

// repository for serviceaddon database operations
public class AddonRepository {
    private EntityManager em;
    
    public AddonRepository(EntityManager em) {
        this.em = em;
    }
    
    // saves an add-on service to the database
    public ServiceAddon save(ServiceAddon addon) {
        if (addon.getId() == null) {
            em.persist(addon);
        } else {
            addon = em.merge(addon);
        }
        return addon;
    }
    
    // finds an add-on service by its id
    public Optional<ServiceAddon> findById(Long id) {
        return Optional.ofNullable(em.find(ServiceAddon.class, id));
    }
    
    // gets all add-on services available in the system
    public List<ServiceAddon> findAll() {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<ServiceAddon> query = em.createQuery("SELECT s FROM ServiceAddon s", ServiceAddon.class);
            List<ServiceAddon> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            System.out.println("[AddonRepository] Found " + results.size() + " service addons");
            return results;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[AddonRepository] ERROR finding all addons: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    // finds an add-on service by its name (like "Wi-Fi" or "Breakfast")
    public Optional<ServiceAddon> findByName(String name) {
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        try {
            TypedQuery<ServiceAddon> query = em.createQuery(
                "SELECT s FROM ServiceAddon s WHERE s.name = :name", ServiceAddon.class);
            query.setParameter("name", name);
            List<ServiceAddon> results = query.getResultList();
            if (!transactionActive) {
                em.getTransaction().commit();
            }
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[AddonRepository] ERROR finding addon by name: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}



