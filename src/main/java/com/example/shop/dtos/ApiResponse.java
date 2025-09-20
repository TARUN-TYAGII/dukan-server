package com.example.shop.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String error;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }
    
    public static <T> ApiResponse<T> error(String error, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return error("ERROR", message);
    }
}
