package com.example.shop.controllers;

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
import com.example.shop.dtos.CustomerDTO;
import com.example.shop.dtos.SearchRequest;
import com.example.shop.models.Customer;
import com.example.shop.services.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers() {
        try {
            List<CustomerDTO> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(ApiResponse.success(customers, "Customers retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(@PathVariable Long id) {
        try {
            return customerService.getCustomerById(id)
                    .map(customer -> ResponseEntity.ok(ApiResponse.success(customer, "Customer found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Customer not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customer: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByEmail(@PathVariable String email) {
        try {
            return customerService.getCustomerByEmail(email)
                    .map(customer -> ResponseEntity.ok(ApiResponse.success(customer, "Customer found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Customer not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customer: " + e.getMessage()));
        }
    }
    
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByPhone(@PathVariable String phone) {
        try {
            return customerService.getCustomerByPhone(phone)
                    .map(customer -> ResponseEntity.ok(ApiResponse.success(customer, "Customer found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Customer not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customer: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{customerType}")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByType(@PathVariable Customer.CustomerType customerType) {
        try {
            List<CustomerDTO> customers = customerService.getCustomersByType(customerType);
            return ResponseEntity.ok(ApiResponse.success(customers, "Customers retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByCity(@PathVariable String city) {
        try {
            List<CustomerDTO> customers = customerService.getCustomersByCity(city);
            return ResponseEntity.ok(ApiResponse.success(customers, "Customers retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CustomerDTO>>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Customer.CustomerType customerType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .customerType(customerType)
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
            
            Page<CustomerDTO> customers = customerService.searchCustomers(searchRequest);
            return ResponseEntity.ok(ApiResponse.success(customers, "Search completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/with-orders")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersWithOrders() {
        try {
            List<CustomerDTO> customers = customerService.getCustomersWithOrders();
            return ResponseEntity.ok(ApiResponse.success(customers, "Customers with orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/top-customers")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getTopCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<CustomerDTO> customers = customerService.getTopCustomers(limit);
            return ResponseEntity.ok(ApiResponse.success(customers, "Top customers retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve top customers: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdCustomer, "Customer created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create customer: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(
            @PathVariable Long id, 
            @Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedCustomer, "Customer updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update customer: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Customer deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete customer: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
        try {
            boolean available = customerService.isEmailAvailable(email);
            return ResponseEntity.ok(ApiResponse.success(available, 
                    available ? "Email is available" : "Email is already taken"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check email availability: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneAvailability(@RequestParam String phone) {
        try {
            boolean available = customerService.isPhoneAvailable(phone);
            return ResponseEntity.ok(ApiResponse.success(available, 
                    available ? "Phone is available" : "Phone is already taken"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check phone availability: " + e.getMessage()));
        }
    }
}
