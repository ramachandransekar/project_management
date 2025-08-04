package com.projectmanagement.service;

import com.projectmanagement.dto.TimeEntryRequest;
import com.projectmanagement.dto.TimeEntryResponse;
import com.projectmanagement.dto.TimeSummaryResponse;
import com.projectmanagement.model.Task;
import com.projectmanagement.model.TimeEntry;
import com.projectmanagement.model.User;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.TimeEntryRepository;
import com.projectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimeTrackingService {
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new time entry
     */
    public TimeEntryResponse createTimeEntry(TimeEntryRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Task task = taskRepository.findById(request.getTaskId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setTask(task);
        timeEntry.setUser(user);
        timeEntry.setEntryDate(request.getEntryDate());
        timeEntry.setStartTime(request.getStartTime());
        timeEntry.setEndTime(request.getEndTime());
        timeEntry.setDurationMinutes(request.getDurationMinutes());
        timeEntry.setDescription(request.getDescription());
        timeEntry.setIsBillable(request.getIsBillable());
        timeEntry.setStatus(TimeEntry.TimeEntryStatus.DRAFT);
        
        TimeEntry savedEntry = timeEntryRepository.save(timeEntry);
        return new TimeEntryResponse(savedEntry);
    }
    
    /**
     * Get time entries for a user
     */
    public List<TimeEntryResponse> getUserTimeEntries(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<TimeEntry> timeEntries = timeEntryRepository.findByUserIdOrderByEntryDateDesc(user.getId());
        return timeEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get time entries for a user within a date range
     */
    public List<TimeEntryResponse> getUserTimeEntriesByDateRange(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<TimeEntry> timeEntries = timeEntryRepository.findByUserIdAndEntryDateBetweenOrderByEntryDateDesc(
            user.getId(), startDate, endDate);
        return timeEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get time entries for a project
     */
    public List<TimeEntryResponse> getProjectTimeEntries(Long projectId) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProjectId(projectId);
        return timeEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get time entries for a project within a date range
     */
    public List<TimeEntryResponse> getProjectTimeEntriesByDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProjectIdAndDateRange(projectId, startDate, endDate);
        return timeEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get time summary for a project
     */
    public TimeSummaryResponse getProjectTimeSummary(Long projectId, LocalDate startDate, LocalDate endDate) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByProjectIdAndDateRange(projectId, startDate, endDate);
        
        Integer totalMinutes = timeEntries.stream()
            .mapToInt(TimeEntry::getDurationMinutes)
            .sum();
        
        Integer billableMinutes = timeEntries.stream()
            .filter(TimeEntry::getIsBillable)
            .mapToInt(TimeEntry::getDurationMinutes)
            .sum();
        
        Integer nonBillableMinutes = totalMinutes - billableMinutes;
        
        // Get project name
        String projectName = timeEntries.isEmpty() ? "Unknown Project" : 
            timeEntries.get(0).getTask().getProject().getName();
        
        TimeSummaryResponse summary = new TimeSummaryResponse(projectId, projectName, totalMinutes, billableMinutes, nonBillableMinutes);
        summary.setTimeEntries(timeEntries.stream().map(TimeEntryResponse::new).collect(Collectors.toList()));
        
        return summary;
    }
    
    /**
     * Get user time summary
     */
    public TimeSummaryResponse getUserTimeSummary(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Integer totalMinutes = timeEntryRepository.getTotalDurationByUserAndDateRange(user.getId(), startDate, endDate);
        Integer billableMinutes = timeEntryRepository.getTotalBillableDurationByUserAndDateRange(user.getId(), startDate, endDate);
        
        totalMinutes = totalMinutes != null ? totalMinutes : 0;
        billableMinutes = billableMinutes != null ? billableMinutes : 0;
        Integer nonBillableMinutes = totalMinutes - billableMinutes;
        
        TimeSummaryResponse summary = new TimeSummaryResponse(null, user.getUsername(), totalMinutes, billableMinutes, nonBillableMinutes);
        
        List<TimeEntry> timeEntries = timeEntryRepository.findByUserIdAndEntryDateBetweenOrderByEntryDateDesc(
            user.getId(), startDate, endDate);
        summary.setTimeEntries(timeEntries.stream().map(TimeEntryResponse::new).collect(Collectors.toList()));
        
        return summary;
    }
    
    /**
     * Update time entry status
     */
    public TimeEntryResponse updateTimeEntryStatus(Long timeEntryId, TimeEntry.TimeEntryStatus status) {
        TimeEntry timeEntry = timeEntryRepository.findById(timeEntryId)
            .orElseThrow(() -> new RuntimeException("Time entry not found"));
        
        timeEntry.setStatus(status);
        TimeEntry savedEntry = timeEntryRepository.save(timeEntry);
        return new TimeEntryResponse(savedEntry);
    }
    
    /**
     * Submit time entries for approval
     */
    public List<TimeEntryResponse> submitTimeEntriesForApproval(List<Long> timeEntryIds, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<TimeEntry> timeEntries = timeEntryRepository.findAllById(timeEntryIds);
        
        // Verify all time entries belong to the user
        timeEntries.forEach(entry -> {
            if (!entry.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to time entry");
            }
        });
        
        timeEntries.forEach(entry -> entry.setStatus(TimeEntry.TimeEntryStatus.SUBMITTED));
        List<TimeEntry> savedEntries = timeEntryRepository.saveAll(timeEntries);
        
        return savedEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get time entries by status
     */
    public List<TimeEntryResponse> getTimeEntriesByStatus(TimeEntry.TimeEntryStatus status) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByStatusOrderByEntryDateDesc(status);
        return timeEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get user time entries by status
     */
    public List<TimeEntryResponse> getUserTimeEntriesByStatus(String username, TimeEntry.TimeEntryStatus status) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<TimeEntry> timeEntries = timeEntryRepository.findByUserIdAndStatusOrderByEntryDateDesc(user.getId(), status);
        return timeEntries.stream()
            .map(TimeEntryResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Delete time entry
     */
    public void deleteTimeEntry(Long timeEntryId, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        TimeEntry timeEntry = timeEntryRepository.findById(timeEntryId)
            .orElseThrow(() -> new RuntimeException("Time entry not found"));
        
        if (!timeEntry.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to time entry");
        }
        
        timeEntryRepository.delete(timeEntry);
    }
} 