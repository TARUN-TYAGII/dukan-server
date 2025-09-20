package com.example.shop.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dtos.CreateOrderRequest;
import com.example.shop.dtos.OrderDTO;
import com.example.shop.dtos.OrderItemDTO;
import com.example.shop.dtos.SearchRequest;
import com.example.shop.models.Book;
import com.example.shop.models.Customer;
import com.example.shop.models.Order;
import com.example.shop.models.OrderItem;
import com.example.shop.repositories.BookRepository;
import com.example.shop.repositories.CustomerRepository;
import com.example.shop.repositories.OrderItemRepository;
import com.example.shop.repositories.OrderRepository;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookService bookService;
    
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<OrderDTO> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<OrderDTO> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::convertToDTO);
    }
    
    public List<OrderDTO> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<OrderDTO> getRecentOrders() {
        return orderRepository.findTop10ByOrderByOrderDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<OrderDTO> searchOrders(SearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getSortDirection()), 
                           searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (searchRequest.getStartDate() != null) {
            startDate = LocalDateTime.parse(searchRequest.getStartDate() + "T00:00:00");
        }
        if (searchRequest.getEndDate() != null) {
            endDate = LocalDateTime.parse(searchRequest.getEndDate() + "T23:59:59");
        }
        
        return orderRepository.searchOrders(
                searchRequest.getOrderNumber(),
                searchRequest.getCustomerId(),
                searchRequest.getOrderStatus(),
                startDate,
                endDate,
                pageable
        ).map(this::convertToDTO);
    }
    
    public OrderDTO createOrder(CreateOrderRequest request) {
        // Validate customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryCity(request.getDeliveryCity())
                .deliveryState(request.getDeliveryState())
                .deliveryPincode(request.getDeliveryPincode())
                .contactPhone(request.getContactPhone())
                .notes(request.getNotes())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(Order.PaymentStatus.PENDING)
                .discountAmount(BigDecimal.ZERO)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CreateOrderRequest.CreateOrderItemRequest itemRequest : request.getOrderItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found: " + itemRequest.getBookId()));
            
            // Check stock availability
            if (book.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book: " + book.getTitle() + 
                                         ". Available: " + book.getQuantity() + 
                                         ", Requested: " + itemRequest.getQuantity());
            }
            
            BigDecimal unitPrice = BigDecimal.valueOf(book.getPrice());
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .book(book)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .discountAmount(BigDecimal.ZERO)
                    .totalPrice(itemTotal)
                    .bookTitle(book.getTitle())
                    .bookAuthor(book.getAuthor())
                    .bookIsbn(book.getIsbn())
                    .bookGrade(book.getGrade())
                    .bookSubject(book.getSubject())
                    .build();
            
            orderItemRepository.save(orderItem);
            totalAmount = totalAmount.add(itemTotal);
            
            // Reduce stock
            bookService.reduceStock(book.getId(), itemRequest.getQuantity());
        }
        
        // Update order totals
        savedOrder.setTotalAmount(totalAmount);
        savedOrder.setFinalAmount(totalAmount.subtract(savedOrder.getDiscountAmount()));
        
        Order finalOrder = orderRepository.save(savedOrder);
        return convertToDTO(finalOrder);
    }
    
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        
        if (status == Order.OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }
    
    public OrderDTO updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setPaymentStatus(paymentStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }
    
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }
        
        // Restore stock for all items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            Book book = item.getBook();
            book.setQuantity(book.getQuantity() + item.getQuantity());
            bookRepository.save(book);
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    public BigDecimal calculateTotalSales() {
        BigDecimal totalSales = orderRepository.calculateTotalSales();
        return totalSales != null ? totalSales : BigDecimal.ZERO;
    }
    
    public BigDecimal calculateSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal sales = orderRepository.calculateSalesByDateRange(startDate, endDate);
        return sales != null ? sales : BigDecimal.ZERO;
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String orderNumber = "ORD" + timestamp;
        
        // Ensure uniqueness
        while (orderRepository.existsByOrderNumber(orderNumber)) {
            orderNumber = "ORD" + timestamp + System.currentTimeMillis() % 1000;
        }
        
        return orderNumber;
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .customerPhone(order.getCustomer().getPhone())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryCity(order.getDeliveryCity())
                .deliveryState(order.getDeliveryState())
                .deliveryPincode(order.getDeliveryPincode())
                .contactPhone(order.getContactPhone())
                .notes(order.getNotes())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .build();
        
        // Load order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
        
        dto.setOrderItems(orderItemDTOs);
        dto.setTotalItems(orderItems.size());
        
        return dto;
    }
    
    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = OrderItemDTO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .bookId(orderItem.getBook().getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .discountAmount(orderItem.getDiscountAmount())
                .totalPrice(orderItem.getTotalPrice())
                .bookTitle(orderItem.getBookTitle())
                .bookAuthor(orderItem.getBookAuthor())
                .bookIsbn(orderItem.getBookIsbn())
                .bookGrade(orderItem.getBookGrade())
                .bookSubject(orderItem.getBookSubject())
                .build();
        
        // Add current book details
        Book currentBook = orderItem.getBook();
        if (currentBook != null) {
            dto.setCurrentBookTitle(currentBook.getTitle());
            dto.setCurrentBookImage(currentBook.getImage());
            dto.setCurrentBookPrice(currentBook.getPrice());
            dto.setCurrentBookStock(currentBook.getQuantity());
        }
        
        return dto;
    }
}
