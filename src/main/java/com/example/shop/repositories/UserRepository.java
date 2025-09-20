package com.example.shop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.shop.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Find by email (for login)
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    // Find by role
    List<User> findByRoleAndIsActiveTrue(User.Role role);
    
    // Check if email exists
    boolean existsByEmailAndIsActiveTrue(String email);
    
    // Find users by name (search)
    List<User> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    // Count users by role
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.isActive = true GROUP BY u.role")
    List<Object[]> countUsersByRole();
    
    // Find admins
    List<User> findByRoleInAndIsActiveTrue(List<User.Role> roles);
    
    // Search users
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "u.isActive = true")
    List<User> searchUsers(@Param("name") String name,
                          @Param("email") String email,
                          @Param("role") User.Role role);
}
