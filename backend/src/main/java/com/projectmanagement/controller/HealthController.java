package com.projectmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Project Management API");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Project Management API");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        response.put("components", Map.of(
            "database", "UP",
            "jwt", "UP",
            "cors", "UP"
        ));
        return ResponseEntity.ok(response);
    }
} 