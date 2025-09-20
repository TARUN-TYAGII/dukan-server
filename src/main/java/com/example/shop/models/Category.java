package com.example.shop.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "categories")
public class Category extends BaseClass {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books;
    
    // Enum for category types specific to school books
    public enum CategoryType {
        GRADE_LEVEL("Grade Level"),
        SUBJECT("Subject"),
        BOOK_TYPE("Book Type"),
        BOARD("Educational Board"),
        LANGUAGE("Language");
        
        private final String displayName;
        
        CategoryType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
