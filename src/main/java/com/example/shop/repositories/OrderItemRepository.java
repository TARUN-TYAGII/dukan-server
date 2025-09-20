package com.example.shop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop.models.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Find order items by order
    List<OrderItem> findByOrderId(Long orderId);
    
    // Find order items by book
    List<OrderItem> findByBookId(Long bookId);
    
    // Find order items by order and book
    List<OrderItem> findByOrderIdAndBookId(Long orderId, Long bookId);
    
    // Calculate total quantity sold for a book
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.book.id = :bookId")
    Long calculateTotalQuantitySoldForBook(@Param("bookId") Long bookId);
    
    // Find best selling books with quantities
    @Query("SELECT oi.book.id, oi.book.title, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi JOIN oi.book b " +
           "WHERE b.isActive = true " +
           "GROUP BY oi.book.id, oi.book.title " +
           "ORDER BY totalSold DESC")
    List<Object[]> findBestSellingBooksWithQuantity();
    
    // Find order items by grade
    @Query("SELECT oi FROM OrderItem oi WHERE oi.bookGrade = :grade")
    List<OrderItem> findByBookGrade(@Param("grade") Integer grade);
    
    // Find order items by subject
    @Query("SELECT oi FROM OrderItem oi WHERE oi.bookSubject = :subject")
    List<OrderItem> findByBookSubject(@Param("subject") String subject);
    
    // Calculate revenue by book
    @Query("SELECT oi.book.id, oi.book.title, SUM(oi.totalPrice) as totalRevenue " +
           "FROM OrderItem oi JOIN oi.book b " +
           "WHERE b.isActive = true " +
           "GROUP BY oi.book.id, oi.book.title " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> calculateRevenueByBook();
    
    // Find order items in date range
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderDate BETWEEN :startDate AND :endDate")
    List<OrderItem> findOrderItemsByDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
                                             @Param("endDate") java.time.LocalDateTime endDate);
}
