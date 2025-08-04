package com.projectmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskCommentRequest {
    
    @NotBlank(message = "Comment content is required")
    private String content;
    
    // Constructors
    public TaskCommentRequest() {}
    
    public TaskCommentRequest(String content) {
        this.content = content;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
} 