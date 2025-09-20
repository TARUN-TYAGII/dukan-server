package com.example.shop.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.shop.dtos.SearchRequest;
import com.example.shop.dtos.UserDTO;
import com.example.shop.models.User;
import com.example.shop.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            return userService.getUserById(id)
                    .map(user -> ResponseEntity.ok(ApiResponse.success(user, "User found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("User not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        try {
            return userService.getUserByEmail(email)
                    .map(user -> ResponseEntity.ok(ApiResponse.success(user, "User found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("User not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable User.Role role) {
        try {
            List<UserDTO> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) User.Role role) {
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .name(name)
                    .email(email)
                    .role(role)
                    .build();
            
            List<UserDTO> users = userService.searchUsers(searchRequest);
            return ResponseEntity.ok(ApiResponse.success(users, "Search completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAdminUsers() {
        try {
            List<UserDTO> users = userService.getAdminUsers();
            return ResponseEntity.ok(ApiResponse.success(users, "Admin users retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve admin users: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdUser, "User created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create user: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "User updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<UserDTO>> changePassword(
            @PathVariable Long id, 
            @RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Both oldPassword and newPassword are required"));
            }
            
            UserDTO updatedUser = userService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            return ResponseEntity.ok(ApiResponse.success(available, 
                    available ? "Email is available" : "Email is already taken"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check email availability: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDTO>> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            if (email == null || password == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Email and password are required"));
            }
            
            return userService.login(email, password)
                    .map(user -> ResponseEntity.ok(ApiResponse.success(user, "Login successful")))
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(ApiResponse.error("Invalid email or password")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }
}
