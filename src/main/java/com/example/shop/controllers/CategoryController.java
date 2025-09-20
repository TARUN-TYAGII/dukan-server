package com.example.shop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dtos.ApiResponse;
import com.example.shop.dtos.CategoryDTO;
import com.example.shop.models.Category;
import com.example.shop.services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        try {
            return categoryService.getCategoryById(id)
                    .map(category -> ResponseEntity.ok(ApiResponse.success(category, "Category found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Category not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve category: " + e.getMessage()));
        }
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryByName(@PathVariable String name) {
        try {
            return categoryService.getCategoryByName(name)
                    .map(category -> ResponseEntity.ok(ApiResponse.success(category, "Category found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Category not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve category: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{categoryType}")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoriesByType(@PathVariable Category.CategoryType categoryType) {
        try {
            List<CategoryDTO> categories = categoryService.getCategoriesByType(categoryType);
            return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }
    
    @GetMapping("/with-books")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoriesWithBooks() {
        try {
            List<CategoryDTO> categories = categoryService.getCategoriesWithBooks();
            return ResponseEntity.ok(ApiResponse.success(categories, "Categories with books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve categories: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdCategory, "Category created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create category: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update category: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete category: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-name")
    public ResponseEntity<ApiResponse<Boolean>> checkNameAvailability(@RequestParam String name) {
        try {
            boolean available = categoryService.isNameAvailable(name);
            return ResponseEntity.ok(ApiResponse.success(available, 
                    available ? "Name is available" : "Name is already taken"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check name availability: " + e.getMessage()));
        }
    }
}
