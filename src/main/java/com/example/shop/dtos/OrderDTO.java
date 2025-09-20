package com.example.shop.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.shop.models.Order;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private Long id;
    
    private String orderNumber;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    private Order.OrderStatus status = Order.OrderStatus.PENDING;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;
    
    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Final amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Final amount must be greater than 0")
    private BigDecimal finalAmount;
    
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    
    // Delivery information
    @Size(max = 500, message = "Delivery address must not exceed 500 characters")
    private String deliveryAddress;
    
    @Size(max = 100, message = "Delivery city must not exceed 100 characters")
    private String deliveryCity;
    
    @Size(max = 100, message = "Delivery state must not exceed 100 characters")
    private String deliveryState;
    
    @Size(max = 20, message = "Delivery pincode must not exceed 20 characters")
    private String deliveryPincode;
    
    @Size(max = 15, message = "Contact phone must not exceed 15 characters")
    private String contactPhone;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    private Order.PaymentMethod paymentMethod;
    private Order.PaymentStatus paymentStatus = Order.PaymentStatus.PENDING;
    
    private List<OrderItemDTO> orderItems;
    private Integer totalItems = 0;
}
