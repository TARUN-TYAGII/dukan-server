package com.example.shop.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dtos.CategoryDTO;
import com.example.shop.models.Category;
import com.example.shop.repositories.CategoryRepository;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<CategoryDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(category -> category.getIsActive())
                .map(this::convertToDTO);
    }
    
    public Optional<CategoryDTO> getCategoryByName(String name) {
        return categoryRepository.findByNameAndIsActiveTrue(name)
                .map(this::convertToDTO);
    }
    
    public List<CategoryDTO> getCategoriesByType(Category.CategoryType categoryType) {
        return categoryRepository.findByCategoryTypeAndIsActiveTrue(categoryType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDTO> getCategoriesWithBooks() {
        return categoryRepository.findCategoriesWithActiveBooks()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Check name uniqueness
        if (categoryRepository.existsByNameAndIsActiveTrue(categoryDTO.getName())) {
            throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists");
        }
        
        Category category = convertToEntity(categoryDTO);
        category.setIsActive(true);
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }
    
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check name uniqueness if changed
        if (!categoryDTO.getName().equals(existingCategory.getName())) {
            if (categoryRepository.existsByNameAndIsActiveTrue(categoryDTO.getName())) {
                throw new RuntimeException("Category with name '" + categoryDTO.getName() + "' already exists");
            }
        }
        
        BeanUtils.copyProperties(categoryDTO, existingCategory, "id", "createdAt", "updatedAt");
        
        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }
    
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check if category has books
        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            long activeBooks = category.getBooks().stream()
                    .filter(book -> book.getIsActive())
                    .count();
            if (activeBooks > 0) {
                throw new RuntimeException("Cannot delete category with active books. Please reassign books first.");
            }
        }
        
        category.setIsActive(false);
        categoryRepository.save(category);
    }
    
    public boolean isNameAvailable(String name) {
        return !categoryRepository.existsByNameAndIsActiveTrue(name);
    }
    
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .categoryType(category.getCategoryType())
                .isActive(category.getIsActive())
                .build();
        
        // Count active books in this category
        if (category.getBooks() != null) {
            int bookCount = (int) category.getBooks().stream()
                    .filter(book -> book.getIsActive())
                    .count();
            dto.setBookCount(bookCount);
        }
        
        return dto;
    }
    
    private Category convertToEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .categoryType(dto.getCategoryType())
                .isActive(dto.getIsActive())
                .build();
    }
}
