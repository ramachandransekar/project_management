package com.projectmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectNoteRequest {
    @NotBlank
    private String content;
    
    public ProjectNoteRequest() {}
    
    public ProjectNoteRequest(String content) {
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