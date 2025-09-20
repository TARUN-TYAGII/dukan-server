package com.example.shop.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.shop.models.Customer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 20, message = "Pincode must not exceed 20 characters")
    private String pincode;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    private Customer.CustomerType customerType = Customer.CustomerType.INDIVIDUAL;
    
    // For school/institution customers
    @Size(max = 200, message = "School/Institution name must not exceed 200 characters")
    private String institutionName;
    
    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    private String contactPerson;
    
    @Size(max = 50, message = "GST number must not exceed 50 characters")
    private String gstNumber;
    
    private Boolean isActive = true;
    
    private Integer totalOrders = 0;
    private Double totalOrderValue = 0.0;
}
