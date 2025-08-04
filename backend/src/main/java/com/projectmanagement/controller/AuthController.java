package com.projectmanagement.controller;

import com.projectmanagement.dto.AuthResponse;
import com.projectmanagement.dto.LoginRequest;
import com.projectmanagement.dto.SignupRequest;
import com.projectmanagement.dto.SignupResponse;
import com.projectmanagement.dto.AdminUserCreationRequest;
import com.projectmanagement.dto.AdminUserCreationResponse;
import com.projectmanagement.dto.UserListResponse;
import com.projectmanagement.model.User;
import com.projectmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            SignupResponse response = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        try {
            List<User> users = authService.getAllUsers();
            List<UserListResponse> userResponses = users.stream()
                    .map(UserListResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/users/list")
    public ResponseEntity<List<UserListResponse>> getUsersList() {
        try {
            List<User> users = authService.getAllUsers();
            List<UserListResponse> userResponses = users.stream()
                    .map(UserListResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/create-user")
    public ResponseEntity<?> createUserByAdmin(@Valid @RequestBody AdminUserCreationRequest request) {
        try {
            AdminUserCreationResponse response = authService.createUserByAdmin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
} 