package com.projectmanagement.dto;

import java.time.LocalDate;

public class MilestoneResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
    private String status;

    public MilestoneResponse() {}

    public MilestoneResponse(Long id, String name, String description, LocalDate dueDate, boolean completed, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 