package com.example.shop.dtos;

import java.util.List;

import jakarta.validation.Valid;
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
public class CreateOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @Valid
    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one order item is required")
    private List<CreateOrderItemRequest> orderItems;
    
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
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderItemRequest {
        
        @NotNull(message = "Book ID is required")
        private Long bookId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}
