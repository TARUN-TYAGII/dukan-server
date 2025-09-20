package com.example.shop.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseClass {
   
    @NotNull(message = "Order number is required")
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Order status is required")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    @Builder.Default
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Final amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Final amount must be greater than 0")
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
    
    // Delivery information
    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;
    
    @Size(max = 100, message = "Delivery city must not exceed 100 characters")
    @Column(name = "delivery_city")
    private String deliveryCity;
    
    @Size(max = 100, message = "Delivery state must not exceed 100 characters")
    @Column(name = "delivery_state")
    private String deliveryState;
    
    @Size(max = 20, message = "Delivery pincode must not exceed 20 characters")
    @Column(name = "delivery_pincode")
    private String deliveryPincode;
    
    @Size(max = 15, message = "Contact phone must not exceed 15 characters")
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(length = 1000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    // Enums
    public enum OrderStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        PROCESSING("Processing"),
        SHIPPED("Shipped"),
        DELIVERED("Delivered"),
        CANCELLED("Cancelled"),
        RETURNED("Returned");
        
        private final String displayName;
        
        OrderStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentMethod {
        CASH_ON_DELIVERY("Cash on Delivery"),
        ONLINE_PAYMENT("Online Payment"),
        BANK_TRANSFER("Bank Transfer"),
        UPI("UPI"),
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentStatus {
        PENDING("Pending"),
        PAID("Paid"),
        FAILED("Failed"),
        REFUNDED("Refunded"),
        PARTIAL("Partial");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
