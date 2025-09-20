package com.example.shop.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.shop.models.Book;
import com.example.shop.models.Customer;
import com.example.shop.models.Order;
import com.example.shop.models.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    
    // Common search fields
    private String keyword;
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDirection = "ASC";
    
    // Book search fields
    private String title;
    private String author;
    private Integer grade;
    private String subject;
    private Book.Board board;
    private Long categoryId;
    
    // Customer search fields
    private String name;
    private String email;
    private String phone;
    private Customer.CustomerType customerType;
    
    // Order search fields
    private String orderNumber;
    private Long customerId;
    private Order.OrderStatus orderStatus;
    private String startDate;
    private String endDate;
    
    // User search fields
    private User.Role role;
    
    // Filter flags
    private Boolean activeOnly = true;
    private Boolean inStockOnly = false;
}
