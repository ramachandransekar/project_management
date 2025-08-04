package com.projectmanagement.dto;

import java.time.LocalDate;

public class BurndownDataResponse {
    private LocalDate date;
    private int remainingTasks;
    private int idealRemainingTasks;

    public BurndownDataResponse() {}

    public BurndownDataResponse(LocalDate date, int remainingTasks, int idealRemainingTasks) {
        this.date = date;
        this.remainingTasks = remainingTasks;
        this.idealRemainingTasks = idealRemainingTasks;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getRemainingTasks() { return remainingTasks; }
    public void setRemainingTasks(int remainingTasks) { this.remainingTasks = remainingTasks; }

    public int getIdealRemainingTasks() { return idealRemainingTasks; }
    public void setIdealRemainingTasks(int idealRemainingTasks) { this.idealRemainingTasks = idealRemainingTasks; }
} 