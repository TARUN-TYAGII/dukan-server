package com.example.shop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find active books
    List<Book> findByIsActiveTrue();
    
    // Find by grade
    List<Book> findByGradeAndIsActiveTrue(Integer grade);
    
    // Find by subject
    List<Book> findBySubjectContainingIgnoreCaseAndIsActiveTrue(String subject);
    
    // Find by board
    List<Book> findByBoardAndIsActiveTrue(Book.Board board);
    
    // Find by category
    List<Book> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    // Find by title (search)
    List<Book> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
    
    // Find by author (search)
    List<Book> findByAuthorContainingIgnoreCaseAndIsActiveTrue(String author);
    
    // Find by ISBN
    Optional<Book> findByIsbnAndIsActiveTrue(String isbn);
    
    // Find books with low stock
    List<Book> findByQuantityLessThanAndIsActiveTrue(Integer threshold);
    
    // Search books with multiple criteria
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:grade IS NULL OR b.grade = :grade) AND " +
           "(:subject IS NULL OR LOWER(b.subject) LIKE LOWER(CONCAT('%', :subject, '%'))) AND " +
           "(:board IS NULL OR b.board = :board) AND " +
           "b.isActive = true")
    Page<Book> searchBooks(@Param("title") String title,
                          @Param("author") String author, 
                          @Param("grade") Integer grade,
                          @Param("subject") String subject,
                          @Param("board") Book.Board board,
                          Pageable pageable);
    
    // Count books by grade
    @Query("SELECT b.grade, COUNT(b) FROM Book b WHERE b.isActive = true GROUP BY b.grade")
    List<Object[]> countBooksByGrade();
    
    // Find bestsellers (most ordered books)
    @Query("SELECT b FROM Book b JOIN OrderItem oi ON b.id = oi.book.id " +
           "WHERE b.isActive = true " +
           "GROUP BY b.id " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Book> findBestSellingBooks(Pageable pageable);
}
