package com.projectmanagement.dto;

import com.projectmanagement.model.ProjectMember;
import java.time.LocalDateTime;

public class ProjectMemberResponse {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private ProjectMember.Role role;
    private LocalDateTime joinedAt;
    
    public ProjectMemberResponse() {}
    
    public ProjectMemberResponse(ProjectMember projectMember) {
        this.id = projectMember.getId();
        this.userId = projectMember.getUser().getId();
        this.username = projectMember.getUser().getUsername();
        this.fullName = projectMember.getUser().getFullName();
        this.email = projectMember.getUser().getEmail();
        this.role = projectMember.getRole();
        this.joinedAt = projectMember.getJoinedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public ProjectMember.Role getRole() {
        return role;
    }
    
    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
} 