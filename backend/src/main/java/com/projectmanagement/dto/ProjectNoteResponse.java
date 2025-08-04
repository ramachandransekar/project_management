package com.projectmanagement.dto;

import com.projectmanagement.model.ProjectNote;
import java.time.LocalDateTime;

public class ProjectNoteResponse {
    private Long id;
    private Long projectId;
    private String content;
    private Long lastUpdatedById;
    private String lastUpdatedByName;
    private LocalDateTime lastUpdatedAt;
    
    public ProjectNoteResponse() {}
    
    public ProjectNoteResponse(ProjectNote projectNote) {
        this.id = projectNote.getId();
        this.projectId = projectNote.getProject().getId();
        this.content = projectNote.getContent();
        if (projectNote.getLastUpdatedBy() != null) {
            this.lastUpdatedById = projectNote.getLastUpdatedBy().getId();
            this.lastUpdatedByName = projectNote.getLastUpdatedBy().getFullName();
        }
        this.lastUpdatedAt = projectNote.getLastUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getLastUpdatedById() {
        return lastUpdatedById;
    }
    
    public void setLastUpdatedById(Long lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
    }
    
    public String getLastUpdatedByName() {
        return lastUpdatedByName;
    }
    
    public void setLastUpdatedByName(String lastUpdatedByName) {
        this.lastUpdatedByName = lastUpdatedByName;
    }
    
    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
} 