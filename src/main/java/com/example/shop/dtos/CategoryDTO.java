package com.example.shop.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.shop.models.Category;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private Category.CategoryType categoryType;
    
    private Boolean isActive = true;
    
    private Integer bookCount = 0; // Number of books in this category
}
