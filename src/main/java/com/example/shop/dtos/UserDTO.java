package com.example.shop.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.shop.models.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    // Password field - only for creation/update, not for display
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;
    
    @NotNull(message = "Role is required")
    private User.Role role = User.Role.STAFF;
    
    private Boolean isActive = true;
    private LocalDateTime lastLogin;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 20, message = "ZIP code must not exceed 20 characters")
    private String zip;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
