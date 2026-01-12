package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Represents an add-on service guests can purchase.
 * Examples: Wi-Fi, breakfast, parking, spa.
 * Used with Decorator pattern to add to booking price.
 */
@Entity
@Table(name = "service_addon")
public class ServiceAddon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Name of the service like "Wi-Fi" or "Breakfast"
    @Column(nullable = false, length = 100)
    @NotNull
    private String name;
    
    // Price of the service
    @Column(nullable = false)
    @Positive
    private double price;
    
    // How the price is calculated: PER_NIGHT or PER_RESERVATION
    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_model", nullable = false)
    @NotNull
    private PricingModel pricingModel;
    
    // Default constructor required by JPA
    public ServiceAddon() {
    }
    
    // Constructor for creating new add-on services
    public ServiceAddon(String name, double price, PricingModel pricingModel) {
        this.name = name;
        this.price = price;
        this.pricingModel = pricingModel;
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public PricingModel getPricingModel() {
        return pricingModel;
    }
    
    public void setPricingModel(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
    }
}



