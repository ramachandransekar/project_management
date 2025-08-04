package com.projectmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("status", "error");
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("status", "error");
        
        // Authentication-related errors
        if (ex.getMessage().contains("Username is already taken")) {
            return ResponseEntity.badRequest().body(response);
        } else if (ex.getMessage().contains("Email is already in use")) {
            return ResponseEntity.badRequest().body(response);
        } else if (ex.getMessage().contains("Invalid username or password")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Resource-related errors
        if (ex.getMessage().equals("User not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (ex.getMessage().equals("Project not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else if (ex.getMessage().equals("Access denied")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else if (ex.getMessage().contains("Invalid priority") || 
                   ex.getMessage().contains("Invalid template") ||
                   ex.getMessage().contains("Invalid status")) {
            return ResponseEntity.badRequest().body(response);
        } else if (ex.getMessage().contains("Start date cannot be after end date")) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "An unexpected error occurred");
        response.put("status", "error");
        response.put("details", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 