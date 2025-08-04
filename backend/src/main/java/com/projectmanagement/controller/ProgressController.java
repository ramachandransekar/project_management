package com.projectmanagement.controller;

import com.projectmanagement.dto.ProgressResponse;
import com.projectmanagement.dto.TeamMemberProgressResponse;
import com.projectmanagement.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*")
public class ProgressController {
    
    @Autowired
    private ProgressService progressService;
    
    /**
     * Get comprehensive progress data for a specific project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProgressResponse> getProjectProgress(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            ProgressResponse progress = progressService.getProjectProgress(projectId, username);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get progress for all user projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ProgressResponse>> getAllProjectsProgress(
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<ProgressResponse> progressList = progressService.getAllProjectsProgress(username);
            return ResponseEntity.ok(progressList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get team member progress leaderboard for a project
     */
    @GetMapping("/project/{projectId}/leaderboard")
    public ResponseEntity<List<TeamMemberProgressResponse>> getTeamLeaderboard(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<TeamMemberProgressResponse> leaderboard = progressService.getTeamLeaderboard(projectId, username);
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 