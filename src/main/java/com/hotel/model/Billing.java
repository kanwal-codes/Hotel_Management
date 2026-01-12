package com.hotel.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the billing/invoice for a reservation.
 * Tracks all money-related stuff: subtotal, tax, discounts, payments, balance.
 */
@Entity
@Table(name = "billing")
public class Billing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // One reservation has one billing record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    @NotNull
    private Reservation reservation;
    
    // Price before tax and discounts
    @Column(nullable = false)
    private double subtotal;
    
    // Tax rate - default is 10%
    @Column(name = "tax_rate", nullable = false)
    private double taxRate = 0.10;
    
    // Calculated tax amount
    @Column(name = "tax_amount", nullable = false)
    private double taxAmount;
    
    // Discount amount applied (in dollars, not percentage)
    @Column(name = "discount_value", nullable = false)
    private double discountValue = 0.0;
    
    // How many loyalty points were used for this billing
    @Column(name = "loyalty_redeemed_points", nullable = false)
    private int loyaltyRedeemedPoints = 0;
    
    // Final total after everything
    @Column(name = "total_amount", nullable = false)
    private double totalAmount;
    
    // How much has been paid so far
    @Column(name = "paid_amount", nullable = false)
    private double paidAmount = 0.0;
    
    // How much is still owed
    @Column(name = "balance_amount", nullable = false)
    private double balanceAmount;
    
    // Status: PENDING, PARTIAL, PAID
    @Column(name = "payment_status", length = 50)
    private String paymentStatus = "PENDING";
    
    // One billing can have multiple payments (deposits, partial payments, etc.)
    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
    
    // Default constructor required by JPA
    public Billing() {
    }
    
    // Constructor that calculates tax and initial totals
    public Billing(Reservation reservation, double subtotal) {
        this.reservation = reservation;
        this.subtotal = subtotal;
        this.taxRate = 0.10;
        this.taxAmount = subtotal * taxRate;
        this.totalAmount = subtotal + taxAmount;
        this.balanceAmount = totalAmount;
    }
    
    // Standard getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    public double getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
    
    public double getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }
    
    public int getLoyaltyRedeemedPoints() {
        return loyaltyRedeemedPoints;
    }
    
    public void setLoyaltyRedeemedPoints(int loyaltyRedeemedPoints) {
        this.loyaltyRedeemedPoints = loyaltyRedeemedPoints;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public double getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public double getBalanceAmount() {
        return balanceAmount;
    }
    
    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }
    
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}



