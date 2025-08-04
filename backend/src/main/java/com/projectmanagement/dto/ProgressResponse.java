package com.projectmanagement.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ProgressResponse {
    private Long projectId;
    private String projectName;
    private int totalTasks;
    private int completedTasks;
    private double completionPercentage;
    private String projectHealth;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Integer> taskStatusBreakdown;
    private List<MilestoneResponse> milestones;
    private List<TaskProgressResponse> taskProgress;
    private List<TeamMemberProgressResponse> teamProgress;
    private List<BurndownDataResponse> burndownData;
    private List<ActivityFeedResponse> activityFeed;

    public ProgressResponse() {}

    public ProgressResponse(Long projectId, String projectName, int totalTasks, int completedTasks, 
                           double completionPercentage, String projectHealth, LocalDate startDate, LocalDate endDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.completionPercentage = completionPercentage;
        this.projectHealth = projectHealth;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public int getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }

    public String getProjectHealth() { return projectHealth; }
    public void setProjectHealth(String projectHealth) { this.projectHealth = projectHealth; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Map<String, Integer> getTaskStatusBreakdown() { return taskStatusBreakdown; }
    public void setTaskStatusBreakdown(Map<String, Integer> taskStatusBreakdown) { this.taskStatusBreakdown = taskStatusBreakdown; }

    public List<MilestoneResponse> getMilestones() { return milestones; }
    public void setMilestones(List<MilestoneResponse> milestones) { this.milestones = milestones; }

    public List<TaskProgressResponse> getTaskProgress() { return taskProgress; }
    public void setTaskProgress(List<TaskProgressResponse> taskProgress) { this.taskProgress = taskProgress; }

    public List<TeamMemberProgressResponse> getTeamProgress() { return teamProgress; }
    public void setTeamProgress(List<TeamMemberProgressResponse> teamProgress) { this.teamProgress = teamProgress; }

    public List<BurndownDataResponse> getBurndownData() { return burndownData; }
    public void setBurndownData(List<BurndownDataResponse> burndownData) { this.burndownData = burndownData; }

    public List<ActivityFeedResponse> getActivityFeed() { return activityFeed; }
    public void setActivityFeed(List<ActivityFeedResponse> activityFeed) { this.activityFeed = activityFeed; }
} 