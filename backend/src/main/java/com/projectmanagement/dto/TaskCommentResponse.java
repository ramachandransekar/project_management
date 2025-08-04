package com.projectmanagement.dto;

import java.time.LocalDateTime;

public class TaskCommentResponse {
    
    private Long id;
    private String content;
    private Long taskId;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    
    // Constructors
    public TaskCommentResponse() {}
    
    public TaskCommentResponse(Long id, String content, Long taskId, 
                             Long createdById, String createdByName, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.taskId = taskId;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Long getCreatedById() {
        return createdById;
    }
    
    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
    
    public String getCreatedByName() {
        return createdByName;
    }
    
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 