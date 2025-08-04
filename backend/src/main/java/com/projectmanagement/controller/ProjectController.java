package com.projectmanagement.controller;

import com.projectmanagement.dto.ProjectCreateRequest;
import com.projectmanagement.dto.ProjectUpdateRequest;
import com.projectmanagement.dto.ProjectResponse;
import com.projectmanagement.dto.ProjectTemplateResponse;
import com.projectmanagement.service.ProjectService;
import com.projectmanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectCreateRequest request,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ProjectResponse project = projectService.createProject(request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get all projects for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getUserProjects(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<ProjectResponse> projects = projectService.getUserProjects(userDetails.getUsername());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get project by ID
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ProjectResponse project = projectService.getProjectById(projectId, userDetails.getUsername());
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().equals("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get available project templates
     */
    @GetMapping("/templates")
    public ResponseEntity<List<ProjectTemplateResponse>> getProjectTemplates() {
        try {
            List<ProjectTemplateResponse> templates = projectService.getProjectTemplates();
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Search projects by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @RequestParam String keyword,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<ProjectResponse> projects = projectService.searchProjects(keyword, userDetails.getUsername());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get projects by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByStatus(
            @PathVariable String status,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<ProjectResponse> projects = projectService.getProjectsByStatus(status, userDetails.getUsername());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get projects by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByPriority(
            @PathVariable String priority,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<ProjectResponse> projects = projectService.getProjectsByPriority(priority, userDetails.getUsername());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get project statistics for the authenticated user
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProjectService.ProjectStatistics> getProjectStatistics(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ProjectService.ProjectStatistics statistics = projectService.getProjectStatistics(userDetails.getUsername());
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update a project
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ProjectResponse project = projectService.updateProject(projectId, request, userDetails.getUsername());
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().equals("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete a project
     */
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            projectService.deleteProject(projectId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().equals("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "Project API is running"));
    }
} 