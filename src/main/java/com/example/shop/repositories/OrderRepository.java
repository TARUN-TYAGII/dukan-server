package com.example.shop.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find by order number
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Find orders by customer
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    
    // Find orders by status
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    
    // Find orders by date range
    List<Order> findByOrderDateBetweenOrderByOrderDateDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find recent orders
    List<Order> findTop10ByOrderByOrderDateDesc();
    
    // Search orders
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNumber IS NULL OR o.orderNumber LIKE CONCAT('%', :orderNumber, '%')) AND " +
           "(:customerId IS NULL OR o.customer.id = :customerId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.orderDate <= :endDate)")
    Page<Order> searchOrders(@Param("orderNumber") String orderNumber,
                            @Param("customerId") Long customerId,
                            @Param("status") Order.OrderStatus status,
                            @Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate,
                            Pageable pageable);
    
    // Count orders by status
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();
    
    // Calculate total sales
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status IN ('DELIVERED', 'CONFIRMED')")
    BigDecimal calculateTotalSales();
    
    // Calculate sales by date range
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status IN ('DELIVERED', 'CONFIRMED') AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateSalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find pending orders
    List<Order> findByStatusInOrderByOrderDateAsc(List<Order.OrderStatus> statuses);
    
    // Monthly sales report
    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.finalAmount), COUNT(o) " +
           "FROM Order o WHERE o.status IN ('DELIVERED', 'CONFIRMED') " +
           "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
           "ORDER BY YEAR(o.orderDate) DESC, MONTH(o.orderDate) DESC")
    List<Object[]> getMonthlySalesReport();
    
    // Check if order number exists
    boolean existsByOrderNumber(String orderNumber);
}
