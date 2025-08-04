package com.projectmanagement.repository;

import com.projectmanagement.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    @Query("SELECT al FROM ActivityLog al WHERE al.project.id = :projectId ORDER BY al.createdAt DESC")
    List<ActivityLog> findByProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId);
    
    @Query("SELECT al FROM ActivityLog al WHERE al.project.id = :projectId ORDER BY al.createdAt DESC LIMIT :limit")
    List<ActivityLog> findRecentByProjectId(@Param("projectId") Long projectId, @Param("limit") int limit);
} 