
package com.example.shop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.shop.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find active categories
    List<Category> findByIsActiveTrue();
    
    // Find by category type
    List<Category> findByCategoryTypeAndIsActiveTrue(Category.CategoryType categoryType);
    
    // Find by name
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    // Check if name exists (for unique validation)
    boolean existsByNameAndIsActiveTrue(String name);
    
    // Find categories with books
    @Query("SELECT DISTINCT c FROM Category c JOIN c.books b WHERE c.isActive = true AND b.isActive = true")
    List<Category> findCategoriesWithActiveBooks();
    
    // Count books in each category
    @Query("SELECT c.name, COUNT(b) FROM Category c LEFT JOIN c.books b WHERE c.isActive = true AND (b.isActive = true OR b IS NULL) GROUP BY c.id, c.name")
    List<Object[]> countBooksByCategory();
}
