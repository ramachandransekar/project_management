package com.projectmanagement.controller;

import com.projectmanagement.dto.TimeEntryRequest;
import com.projectmanagement.dto.TimeEntryResponse;
import com.projectmanagement.dto.TimeSummaryResponse;
import com.projectmanagement.model.TimeEntry;
import com.projectmanagement.security.UserDetailsImpl;
import com.projectmanagement.service.TimeTrackingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/time-tracking")
public class TimeTrackingController {
    
    @Autowired
    private TimeTrackingService timeTrackingService;
    
    /**
     * Create a new time entry
     */
    @PostMapping("/entries")
    public ResponseEntity<TimeEntryResponse> createTimeEntry(
            @Valid @RequestBody TimeEntryRequest request,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            TimeEntryResponse response = timeTrackingService.createTimeEntry(request, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user's time entries
     */
    @GetMapping("/entries")
    public ResponseEntity<List<TimeEntryResponse>> getUserTimeEntries(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<TimeEntryResponse> entries = timeTrackingService.getUserTimeEntries(userDetails.getUsername());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user's time entries by date range
     */
    @GetMapping("/entries/range")
    public ResponseEntity<List<TimeEntryResponse>> getUserTimeEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<TimeEntryResponse> entries = timeTrackingService.getUserTimeEntriesByDateRange(
                userDetails.getUsername(), startDate, endDate);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get project time entries
     */
    @GetMapping("/projects/{projectId}/entries")
    public ResponseEntity<List<TimeEntryResponse>> getProjectTimeEntries(@PathVariable Long projectId) {
        try {
            List<TimeEntryResponse> entries = timeTrackingService.getProjectTimeEntries(projectId);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get project time entries by date range
     */
    @GetMapping("/projects/{projectId}/entries/range")
    public ResponseEntity<List<TimeEntryResponse>> getProjectTimeEntriesByDateRange(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TimeEntryResponse> entries = timeTrackingService.getProjectTimeEntriesByDateRange(projectId, startDate, endDate);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get project time summary
     */
    @GetMapping("/projects/{projectId}/summary")
    public ResponseEntity<TimeSummaryResponse> getProjectTimeSummary(
            @PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            TimeSummaryResponse summary = timeTrackingService.getProjectTimeSummary(projectId, startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user time summary
     */
    @GetMapping("/summary")
    public ResponseEntity<TimeSummaryResponse> getUserTimeSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            TimeSummaryResponse summary = timeTrackingService.getUserTimeSummary(userDetails.getUsername(), startDate, endDate);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update time entry status
     */
    @PutMapping("/entries/{entryId}/status")
    public ResponseEntity<TimeEntryResponse> updateTimeEntryStatus(
            @PathVariable Long entryId,
            @RequestParam String status,
            Authentication authentication) {
        try {
            TimeEntry.TimeEntryStatus entryStatus = TimeEntry.TimeEntryStatus.valueOf(status.toUpperCase());
            TimeEntryResponse response = timeTrackingService.updateTimeEntryStatus(entryId, entryStatus);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Submit time entries for approval
     */
    @PostMapping("/entries/submit")
    public ResponseEntity<List<TimeEntryResponse>> submitTimeEntriesForApproval(
            @RequestBody List<Long> timeEntryIds,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<TimeEntryResponse> responses = timeTrackingService.submitTimeEntriesForApproval(timeEntryIds, userDetails.getUsername());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get time entries by status
     */
    @GetMapping("/entries/status/{status}")
    public ResponseEntity<List<TimeEntryResponse>> getTimeEntriesByStatus(@PathVariable String status) {
        try {
            TimeEntry.TimeEntryStatus entryStatus = TimeEntry.TimeEntryStatus.valueOf(status.toUpperCase());
            List<TimeEntryResponse> entries = timeTrackingService.getTimeEntriesByStatus(entryStatus);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get user time entries by status
     */
    @GetMapping("/entries/status/{status}/user")
    public ResponseEntity<List<TimeEntryResponse>> getUserTimeEntriesByStatus(
            @PathVariable String status,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            TimeEntry.TimeEntryStatus entryStatus = TimeEntry.TimeEntryStatus.valueOf(status.toUpperCase());
            List<TimeEntryResponse> entries = timeTrackingService.getUserTimeEntriesByStatus(userDetails.getUsername(), entryStatus);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete time entry
     */
    @DeleteMapping("/entries/{entryId}")
    public ResponseEntity<Void> deleteTimeEntry(
            @PathVariable Long entryId,
            Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            timeTrackingService.deleteTimeEntry(entryId, userDetails.getUsername());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 