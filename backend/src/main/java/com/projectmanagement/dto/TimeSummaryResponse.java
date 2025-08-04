package com.projectmanagement.dto;

import java.util.List;

public class TimeSummaryResponse {
    
    private Long projectId;
    private String projectName;
    private Integer totalMinutes;
    private Integer billableMinutes;
    private Integer nonBillableMinutes;
    private Double totalHours;
    private Double billableHours;
    private Double nonBillableHours;
    private List<TimeEntryResponse> timeEntries;
    
    public TimeSummaryResponse() {}
    
    public TimeSummaryResponse(Long projectId, String projectName, Integer totalMinutes, 
                              Integer billableMinutes, Integer nonBillableMinutes) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalMinutes = totalMinutes != null ? totalMinutes : 0;
        this.billableMinutes = billableMinutes != null ? billableMinutes : 0;
        this.nonBillableMinutes = nonBillableMinutes != null ? nonBillableMinutes : 0;
        this.totalHours = this.totalMinutes / 60.0;
        this.billableHours = this.billableMinutes / 60.0;
        this.nonBillableHours = this.nonBillableMinutes / 60.0;
    }
    
    // Getters and Setters
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
    
    public Integer getTotalMinutes() {
        return totalMinutes;
    }
    
    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
        this.totalHours = totalMinutes / 60.0;
    }
    
    public Integer getBillableMinutes() {
        return billableMinutes;
    }
    
    public void setBillableMinutes(Integer billableMinutes) {
        this.billableMinutes = billableMinutes;
        this.billableHours = billableMinutes / 60.0;
    }
    
    public Integer getNonBillableMinutes() {
        return nonBillableMinutes;
    }
    
    public void setNonBillableMinutes(Integer nonBillableMinutes) {
        this.nonBillableMinutes = nonBillableMinutes;
        this.nonBillableHours = nonBillableMinutes / 60.0;
    }
    
    public Double getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }
    
    public Double getBillableHours() {
        return billableHours;
    }
    
    public void setBillableHours(Double billableHours) {
        this.billableHours = billableHours;
    }
    
    public Double getNonBillableHours() {
        return nonBillableHours;
    }
    
    public void setNonBillableHours(Double nonBillableHours) {
        this.nonBillableHours = nonBillableHours;
    }
    
    public List<TimeEntryResponse> getTimeEntries() {
        return timeEntries;
    }
    
    public void setTimeEntries(List<TimeEntryResponse> timeEntries) {
        this.timeEntries = timeEntries;
    }
} 