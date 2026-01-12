package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Represents a single payment transaction.
 * Each payment is linked to a billing record.
 */
@Entity
@Table(name = "payment")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many payments belong to one billing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    @NotNull
    private Billing billing;
    
    // Payment method: CASH, CARD, or POINTS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentMethod method;
    
    // Amount paid in this transaction
    @Column(nullable = false)
    @Positive
    private double amount;
    
    // When this payment was made
    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;
    
    // Default constructor - sets timestamp automatically
    public Payment() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor for creating new payments
    public Payment(Billing billing, PaymentMethod method, double amount) {
        this.billing = billing;
        this.method = method;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Billing getBilling() {
        return billing;
    }
    
    public void setBilling(Billing billing) {
        this.billing = billing;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



