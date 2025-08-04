package com.projectmanagement.service;

import com.projectmanagement.dto.AuthResponse;
import com.projectmanagement.dto.LoginRequest;
import com.projectmanagement.dto.SignupRequest;
import com.projectmanagement.dto.SignupResponse;
import com.projectmanagement.dto.AdminUserCreationRequest;
import com.projectmanagement.dto.AdminUserCreationResponse;
import com.projectmanagement.model.User;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.JwtUtils;
import com.projectmanagement.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return new AuthResponse(jwt, 
                               userDetails.getId(), 
                               userDetails.getUsername(), 
                               userDetails.getEmail(),
                               userDetails.getFirstName(),
                               userDetails.getLastName());
    }

    public SignupResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), 
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()));
        
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());

        userRepository.save(user);

        // Return signup response without authentication
        return new SignupResponse("User registered successfully!", 
                                 user.getId(), 
                                 user.getUsername(), 
                                 user.getEmail(),
                                 user.getFirstName(),
                                 user.getLastName());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public AdminUserCreationResponse createUserByAdmin(AdminUserCreationRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user account
        User user = new User(request.getUsername(), 
                           request.getEmail(),
                           encoder.encode(request.getPassword()));
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);

        // Return admin user creation response
        return new AdminUserCreationResponse(
            "User created successfully!", 
            savedUser.getId(), 
            savedUser.getUsername(), 
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName()
        );
    }
} 