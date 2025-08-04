package com.projectmanagement.dto;

import com.projectmanagement.model.ActivityLog;
import java.time.LocalDateTime;

public class ActivityLogResponse {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private ActivityLog.ActivityType activityType;
    private String description;
    private String entityType;
    private Long entityId;
    private LocalDateTime createdAt;
    
    public ActivityLogResponse() {}
    
    public ActivityLogResponse(ActivityLog activityLog) {
        this.id = activityLog.getId();
        this.userId = activityLog.getUser().getId();
        this.username = activityLog.getUser().getUsername();
        this.fullName = activityLog.getUser().getFullName();
        this.activityType = activityLog.getActivityType();
        this.description = activityLog.getDescription();
        this.entityType = activityLog.getEntityType();
        this.entityId = activityLog.getEntityId();
        this.createdAt = activityLog.getCreatedAt();
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
    
    public ActivityLog.ActivityType getActivityType() {
        return activityType;
    }
    
    public void setActivityType(ActivityLog.ActivityType activityType) {
        this.activityType = activityType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 