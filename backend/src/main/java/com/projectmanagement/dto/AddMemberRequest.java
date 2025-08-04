package com.projectmanagement.dto;

import com.projectmanagement.model.ProjectMember;
import jakarta.validation.constraints.NotNull;

public class AddMemberRequest {
    @NotNull
    private Long userId;
    
    private ProjectMember.Role role = ProjectMember.Role.MEMBER;
    
    public AddMemberRequest() {}
    
    public AddMemberRequest(Long userId, ProjectMember.Role role) {
        this.userId = userId;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public ProjectMember.Role getRole() {
        return role;
    }
    
    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
} 