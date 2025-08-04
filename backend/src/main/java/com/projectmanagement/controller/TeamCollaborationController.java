package com.projectmanagement.controller;

import com.projectmanagement.dto.*;
import com.projectmanagement.model.User;
import com.projectmanagement.security.UserDetailsImpl;
import com.projectmanagement.service.TeamCollaborationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamCollaborationController {
    
    @Autowired
    private TeamCollaborationService teamCollaborationService;
    
    private User getUserFromAuthentication(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = new User();
        currentUser.setId(userDetails.getId());
        currentUser.setUsername(userDetails.getUsername());
        currentUser.setEmail(userDetails.getEmail());
        currentUser.setFirstName(userDetails.getFirstName());
        currentUser.setLastName(userDetails.getLastName());
        return currentUser;
    }
    
    // Project Member Management
    @PostMapping("/project/{projectId}/add-member")
    public ResponseEntity<ProjectMemberResponse> addMemberToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request,
            Authentication authentication) {
        
        User currentUser = getUserFromAuthentication(authentication);
        
        ProjectMemberResponse response = teamCollaborationService.addMemberToProject(projectId, request, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/project/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        List<ProjectMemberResponse> members = teamCollaborationService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }
    
    @DeleteMapping("/project/{projectId}/remove-member/{userId}")
    public ResponseEntity<Void> removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication) {
        
        User currentUser = getUserFromAuthentication(authentication);
        
        teamCollaborationService.removeMemberFromProject(projectId, userId, currentUser);
        return ResponseEntity.ok().build();
    }
    
    // Activity Log Management
    @GetMapping("/project/{projectId}/activity")
    public ResponseEntity<List<ActivityLogResponse>> getProjectActivity(@PathVariable Long projectId) {
        List<ActivityLogResponse> activities = teamCollaborationService.getProjectActivity(projectId);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/project/{projectId}/activity/recent")
    public ResponseEntity<List<ActivityLogResponse>> getRecentProjectActivity(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ActivityLogResponse> activities = teamCollaborationService.getRecentProjectActivity(projectId, limit);
        return ResponseEntity.ok(activities);
    }
    
    // Project Notes Management
    @PostMapping("/project/{projectId}/note")
    public ResponseEntity<ProjectNoteResponse> createOrUpdateProjectNote(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectNoteRequest request,
            Authentication authentication) {
        
        User currentUser = getUserFromAuthentication(authentication);
        
        ProjectNoteResponse response = teamCollaborationService.createOrUpdateProjectNote(projectId, request, currentUser);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/project/{projectId}/note")
    public ResponseEntity<ProjectNoteResponse> getProjectNote(@PathVariable Long projectId) {
        ProjectNoteResponse response = teamCollaborationService.getProjectNote(projectId);
        return ResponseEntity.ok(response);
    }
    
    // Task Comments
    @PostMapping("/tasks/{taskId}/comment")
    public ResponseEntity<TaskCommentResponse> addTaskComment(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskCommentRequest request,
            Authentication authentication) {
        
        User currentUser = getUserFromAuthentication(authentication);
        
        TaskCommentResponse response = teamCollaborationService.addTaskComment(taskId, request, currentUser);
        return ResponseEntity.ok(response);
    }
} 