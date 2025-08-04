package com.projectmanagement.dto;

import java.time.LocalDate;
import java.util.List;

public class TaskProgressResponse {
    private Long id;
    private String title;
    private String status;
    private double progressPercentage;
    private LocalDate dueDate;
    private String assigneeName;
    private List<SubtaskResponse> subtasks;

    public TaskProgressResponse() {}

    public TaskProgressResponse(Long id, String title, String status, double progressPercentage, 
                               LocalDate dueDate, String assigneeName) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.dueDate = dueDate;
        this.assigneeName = assigneeName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

    public List<SubtaskResponse> getSubtasks() { return subtasks; }
    public void setSubtasks(List<SubtaskResponse> subtasks) { this.subtasks = subtasks; }
} 