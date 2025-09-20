package com.example.shop.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "customers")
public class Customer extends BaseClass {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    @Column(nullable = false)
    private String phone;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(length = 500)
    private String address;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 20, message = "Pincode must not exceed 20 characters")
    private String pincode;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType = CustomerType.INDIVIDUAL;
    
    // For school/institution customers
    @Size(max = 200, message = "School/Institution name must not exceed 200 characters")
    @Column(name = "institution_name")
    private String institutionName;
    
    @Size(max = 100, message = "Contact person must not exceed 100 characters")
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Size(max = 50, message = "GST number must not exceed 50 characters")
    @Column(name = "gst_number")
    private String gstNumber;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
    
    // Enum for customer types
    public enum CustomerType {
        INDIVIDUAL("Individual"),
        SCHOOL("School"),
        INSTITUTION("Educational Institution"),
        BULK_BUYER("Bulk Buyer");
        
        private final String displayName;
        
        CustomerType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
