package com.example.shop.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseClass {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;
    
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;
    
    @NotNull(message = "Role is required")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STAFF;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Optional address fields for admin users
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(length = 500)
    private String address;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 20, message = "ZIP code must not exceed 20 characters")
    private String zip;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    // Enum for user roles (admin/staff users)
    public enum Role {
        ADMIN("Administrator"),
        MANAGER("Manager"),
        STAFF("Staff"),
        INVENTORY_MANAGER("Inventory Manager"),
        SALES_PERSON("Sales Person");
        
        private final String displayName;
        
        Role(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

