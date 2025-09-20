package com.example.shop.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    private Long id;
    
    private Long orderId;
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;
    
    // Book details at time of order
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private Integer bookGrade;
    private String bookSubject;
    
    // Current book details (for display)
    private String currentBookTitle;
    private String currentBookImage;
    private Double currentBookPrice;
    private Integer currentBookStock;
}
