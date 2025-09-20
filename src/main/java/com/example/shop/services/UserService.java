package com.example.shop.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dtos.SearchRequest;
import com.example.shop.dtos.UserDTO;
import com.example.shop.models.User;
import com.example.shop.repositories.UserRepository;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Note: You'll need to add spring-boot-starter-security dependency for PasswordEncoder
    // @Autowired
    // private PasswordEncoder passwordEncoder;
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getIsActive())
                .map(this::convertToDTO);
    }
    
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .map(this::convertToDTO);
    }
    
    public List<UserDTO> getUsersByRole(User.Role role) {
        return userRepository.findByRoleAndIsActiveTrue(role)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> searchUsers(SearchRequest searchRequest) {
        return userRepository.searchUsers(
                searchRequest.getName(),
                searchRequest.getEmail(),
                searchRequest.getRole()
        ).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<UserDTO> getAdminUsers() {
        List<User.Role> adminRoles = List.of(User.Role.ADMIN, User.Role.MANAGER);
        return userRepository.findByRoleInAndIsActiveTrue(adminRoles)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO createUser(UserDTO userDTO) {
        // Check email uniqueness
        if (userRepository.existsByEmailAndIsActiveTrue(userDTO.getEmail())) {
            throw new RuntimeException("User with email " + userDTO.getEmail() + " already exists");
        }
        
        User user = convertToEntity(userDTO);
        user.setIsActive(true);
        
        // Hash password (uncomment when security is added)
        // if (userDTO.getPassword() != null) {
        //     user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        // }
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check email uniqueness if changed
        if (!userDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIsActiveTrue(userDTO.getEmail())) {
                throw new RuntimeException("User with email " + userDTO.getEmail() + " already exists");
            }
        }
        
        String oldPassword = existingUser.getPassword();
        BeanUtils.copyProperties(userDTO, existingUser, "id", "createdAt", "updatedAt", "password", "lastLogin");
        
        // Update password only if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            // existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            existingUser.setPassword(userDTO.getPassword()); // Remove this when security is added
        } else {
            existingUser.setPassword(oldPassword);
        }
        
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    public UserDTO changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify old password (uncomment when security is added)
        // if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        //     throw new RuntimeException("Invalid old password");
        // }
        
        // For now, simple string comparison (remove when security is added)
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        
        // user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword); // Remove this when security is added
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    public void updateLastLogin(String email) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmailAndIsActiveTrue(email);
    }
    
    // Simple login method (replace with proper authentication when security is added)
    public Optional<UserDTO> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Simple password check (replace with proper authentication)
            if (password.equals(user.getPassword())) {
                updateLastLogin(email);
                return Optional.of(convertToDTO(user));
            }
        }
        return Optional.empty();
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                // Don't include password in DTO for security
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .zip(user.getZip())
                .country(user.getCountry())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    private User convertToEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .role(dto.getRole())
                .isActive(dto.getIsActive())
                .lastLogin(dto.getLastLogin())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .zip(dto.getZip())
                .country(dto.getCountry())
                .build();
    }
}
