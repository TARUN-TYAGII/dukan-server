package com.example.shop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop.models.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    // Find active customers
    List<Customer> findByIsActiveTrue();
    
    // Find by email
    Optional<Customer> findByEmailAndIsActiveTrue(String email);
    
    // Find by phone
    Optional<Customer> findByPhoneAndIsActiveTrue(String phone);
    
    // Find by customer type
    List<Customer> findByCustomerTypeAndIsActiveTrue(Customer.CustomerType customerType);
    
    // Find by city
    List<Customer> findByCityAndIsActiveTrue(String city);
    
    // Search customers
    @Query("SELECT c FROM Customer c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:customerType IS NULL OR c.customerType = :customerType) AND " +
           "c.isActive = true")
    Page<Customer> searchCustomers(@Param("name") String name,
                                  @Param("email") String email,
                                  @Param("phone") String phone,
                                  @Param("customerType") Customer.CustomerType customerType,
                                  Pageable pageable);
    
    // Check if email exists
    boolean existsByEmailAndIsActiveTrue(String email);
    
    // Check if phone exists
    boolean existsByPhoneAndIsActiveTrue(String phone);
    
    // Find customers with orders
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.orders o WHERE c.isActive = true")
    List<Customer> findCustomersWithOrders();
    
    // Count customers by type
    @Query("SELECT c.customerType, COUNT(c) FROM Customer c WHERE c.isActive = true GROUP BY c.customerType")
    List<Object[]> countCustomersByType();
    
    // Find top customers by order value
    @Query("SELECT c FROM Customer c JOIN c.orders o WHERE c.isActive = true " +
           "GROUP BY c.id ORDER BY SUM(o.finalAmount) DESC")
    List<Customer> findTopCustomersByOrderValue(Pageable pageable);
}
