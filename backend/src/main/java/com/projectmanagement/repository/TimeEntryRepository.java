package com.projectmanagement.repository;

import com.projectmanagement.model.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    
    // Find time entries by user
    List<TimeEntry> findByUserIdOrderByEntryDateDesc(Long userId);
    
    // Find time entries by user and date range
    List<TimeEntry> findByUserIdAndEntryDateBetweenOrderByEntryDateDesc(
        Long userId, LocalDate startDate, LocalDate endDate);
    
    // Find time entries by task
    List<TimeEntry> findByTaskIdOrderByEntryDateDesc(Long taskId);
    
    // Find time entries by project (through task)
    @Query("SELECT te FROM TimeEntry te WHERE te.task.project.id = :projectId ORDER BY te.entryDate DESC")
    List<TimeEntry> findByProjectId(@Param("projectId") Long projectId);
    
    // Find time entries by project and date range
    @Query("SELECT te FROM TimeEntry te WHERE te.task.project.id = :projectId AND te.entryDate BETWEEN :startDate AND :endDate ORDER BY te.entryDate DESC")
    List<TimeEntry> findByProjectIdAndDateRange(
        @Param("projectId") Long projectId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
    
    // Find time entries by user and project
    @Query("SELECT te FROM TimeEntry te WHERE te.user.id = :userId AND te.task.project.id = :projectId ORDER BY te.entryDate DESC")
    List<TimeEntry> findByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
    
    // Find time entries by status
    List<TimeEntry> findByStatusOrderByEntryDateDesc(TimeEntry.TimeEntryStatus status);
    
    // Find time entries by user and status
    List<TimeEntry> findByUserIdAndStatusOrderByEntryDateDesc(Long userId, TimeEntry.TimeEntryStatus status);
    
    // Get total duration by user and date range
    @Query("SELECT SUM(te.durationMinutes) FROM TimeEntry te WHERE te.user.id = :userId AND te.entryDate BETWEEN :startDate AND :endDate")
    Integer getTotalDurationByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get total billable duration by user and date range
    @Query("SELECT SUM(te.durationMinutes) FROM TimeEntry te WHERE te.user.id = :userId AND te.isBillable = true AND te.entryDate BETWEEN :startDate AND :endDate")
    Integer getTotalBillableDurationByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get total duration by project and date range
    @Query("SELECT SUM(te.durationMinutes) FROM TimeEntry te WHERE te.task.project.id = :projectId AND te.entryDate BETWEEN :startDate AND :endDate")
    Integer getTotalDurationByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get total duration by user
    @Query("SELECT SUM(te.durationMinutes) FROM TimeEntry te WHERE te.user.id = :userId")
    Integer getTotalDurationByUser(@Param("userId") Long userId);
    
    // Get total duration by project
    @Query("SELECT SUM(te.durationMinutes) FROM TimeEntry te WHERE te.task.project.id = :projectId")
    Integer getTotalDurationByProject(@Param("projectId") Long projectId);
} 