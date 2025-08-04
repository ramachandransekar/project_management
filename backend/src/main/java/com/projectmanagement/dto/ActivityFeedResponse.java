package com.projectmanagement.dto;

import java.time.LocalDateTime;

public class ActivityFeedResponse {
    private Long id;
    private String activityType;
    private String description;
    private String userName;
    private LocalDateTime timestamp;
    private String projectName;
    private String taskTitle;

    public ActivityFeedResponse() {}

    public ActivityFeedResponse(Long id, String activityType, String description, String userName, 
                              LocalDateTime timestamp, String projectName, String taskTitle) {
        this.id = id;
        this.activityType = activityType;
        this.description = description;
        this.userName = userName;
        this.timestamp = timestamp;
        this.projectName = projectName;
        this.taskTitle = taskTitle;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
} 