package com.example.shop.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "books")
public class Book extends BaseClass {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    @Column(nullable = false)
    private String author;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(length = 1000)
    private String description;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;
    
    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "MRP must be greater than 0")
    @Column(nullable = false)
    private Double mrp;
    
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private Double discount;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;
    
    // School-specific fields
    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    @Column(nullable = false)
    private Integer grade;
    
    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject must not exceed 100 characters")
    @Column(nullable = false)
    private String subject;
    
    @NotNull(message = "Board is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Board board;
    
    // @Pattern(regexp = "^(97[89])?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    @Column(unique = true)
    private String isbn;
    
    @Size(max = 255, message = "Publisher must not exceed 255 characters")
    private String publisher;
    
    @Size(max = 50, message = "Edition must not exceed 50 characters")
    private String edition;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Enum for education boards
    public enum Board {
        CBSE("Central Board of Secondary Education"),
        ICSE("Indian Certificate of Secondary Education"),
        STATE_BOARD("State Board"),
        IGCSE("International General Certificate of Secondary Education"),
        IB("International Baccalaureate"),
        NCERT("National Council of Educational Research and Training");
        
        private final String displayName;
        
        Board(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
