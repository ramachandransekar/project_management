package com.projectmanagement.dto;

import com.projectmanagement.model.TimeEntry;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimeEntryResponse {
    
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long projectId;
    private String projectName;
    private Long userId;
    private String userName;
    private LocalDate entryDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private String description;
    private Boolean isBillable;
    private String status;
    private String createdAt;
    
    public TimeEntryResponse() {}
    
    public TimeEntryResponse(TimeEntry timeEntry) {
        this.id = timeEntry.getId();
        this.taskId = timeEntry.getTask().getId();
        this.taskTitle = timeEntry.getTask().getTitle();
        this.projectId = timeEntry.getTask().getProject().getId();
        this.projectName = timeEntry.getTask().getProject().getName();
        this.userId = timeEntry.getUser().getId();
        this.userName = timeEntry.getUser().getUsername();
        this.entryDate = timeEntry.getEntryDate();
        this.startTime = timeEntry.getStartTime();
        this.endTime = timeEntry.getEndTime();
        this.durationMinutes = timeEntry.getDurationMinutes();
        this.description = timeEntry.getDescription();
        this.isBillable = timeEntry.getIsBillable();
        this.status = timeEntry.getStatus().name();
        this.createdAt = timeEntry.getCreatedAt().toString();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskTitle() {
        return taskTitle;
    }
    
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public LocalDate getEntryDate() {
        return entryDate;
    }
    
    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsBillable() {
        return isBillable;
    }
    
    public void setIsBillable(Boolean isBillable) {
        this.isBillable = isBillable;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
} 