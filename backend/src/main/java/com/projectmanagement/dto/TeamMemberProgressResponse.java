package com.projectmanagement.dto;

public class TeamMemberProgressResponse {
    private Long userId;
    private String username;
    private String fullName;
    private int totalTasks;
    private int completedTasks;
    private double completionRate;
    private int rank;

    public TeamMemberProgressResponse() {}

    public TeamMemberProgressResponse(Long userId, String username, String fullName, 
                                    int totalTasks, int completedTasks, double completionRate, int rank) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.completionRate = completionRate;
        this.rank = rank;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
} 