package com.example.shop.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.example.shop.dtos.CreateOrderRequest;
import com.example.shop.dtos.OrderDTO;
import com.example.shop.dtos.SearchRequest;
import com.example.shop.models.Order;
import com.example.shop.services.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        try {
            return orderService.getOrderById(id)
                    .map(order -> ResponseEntity.ok(ApiResponse.success(order, "Order found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Order not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve order: " + e.getMessage()));
        }
    }
    
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            return orderService.getOrderByOrderNumber(orderNumber)
                    .map(order -> ResponseEntity.ok(ApiResponse.success(order, "Order found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Order not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve order: " + e.getMessage()));
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getRecentOrders() {
        try {
            List<OrderDTO> orders = orderService.getRecentOrders();
            return ResponseEntity.ok(ApiResponse.success(orders, "Recent orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve recent orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> searchOrders(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Order.OrderStatus orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .orderNumber(orderNumber)
                    .customerId(customerId)
                    .orderStatus(orderStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
            
            Page<OrderDTO> orders = orderService.searchOrders(searchRequest);
            return ResponseEntity.ok(ApiResponse.success(orders, "Search completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderDTO createdOrder = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdOrder, "Order created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create order: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam Order.OrderStatus status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Order status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update order status: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ApiResponse<OrderDTO>> updatePaymentStatus(
            @PathVariable Long id, 
            @RequestParam Order.PaymentStatus paymentStatus) {
        try {
            OrderDTO updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success(updatedOrder, "Payment status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update payment status: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Order cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to cancel order: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analytics/total-sales")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalSales() {
        try {
            BigDecimal totalSales = orderService.calculateTotalSales();
            return ResponseEntity.ok(ApiResponse.success(totalSales, "Total sales calculated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to calculate total sales: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analytics/sales-by-date-range")
    public ResponseEntity<ApiResponse<BigDecimal>> getSalesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            
            BigDecimal sales = orderService.calculateSalesByDateRange(start, end);
            return ResponseEntity.ok(ApiResponse.success(sales, "Sales by date range calculated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to calculate sales: " + e.getMessage()));
        }
    }
}
