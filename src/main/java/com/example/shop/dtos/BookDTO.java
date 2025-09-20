package com.example.shop.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.shop.models.Book;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;
    
    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "MRP must be greater than 0")
    private Double mrp;
    
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private Double discount;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    private Integer grade;
    
    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    private String subject;
    
    @NotNull(message = "Board is required")
    private Book.Board board;
    
    // @Pattern(regexp = "^(97[89])?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    private String isbn;
    
    @Size(max = 255, message = "Publisher must not exceed 255 characters")
    private String publisher;
    
    @Size(max = 50, message = "Edition must not exceed 50 characters")
    private String edition;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    private Boolean isActive = true;
    
    private Long categoryId;
    private String categoryName;
    
    // Helper method to check if book is in stock
    public boolean isInStock() {
        return quantity != null && quantity > 0;
    }
    
    // Helper method to calculate discounted price
    public Double getDiscountedPrice() {
        if (price != null && discount != null && discount > 0) {
            return price - (price * discount / 100);
        }
        return price;
    }
}
