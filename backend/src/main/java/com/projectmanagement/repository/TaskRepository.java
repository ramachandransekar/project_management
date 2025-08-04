package com.projectmanagement.repository;

import com.projectmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId")
    List<Task> findByAssigneeId(@Param("assigneeId") Long assigneeId);
    
    @Query("SELECT t FROM Task t WHERE t.createdBy.id = :createdById")
    List<Task> findByCreatedById(@Param("createdById") Long createdById);
    
    @Query("SELECT t FROM Task t WHERE t.status = :status")
    List<Task> findByStatus(@Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.priority = :priority")
    List<Task> findByPriority(@Param("priority") Task.Priority priority);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") Task.TaskStatus status);
    
    /**
     * Count tasks by status for a specific user (created by or assigned to)
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.id = :userId OR t.assignee.id = :userId) AND t.status = :status")
    long countByUserAndStatus(@Param("userId") Long userId, @Param("status") Task.TaskStatus status);
    
    /**
     * Count all tasks for a specific user (created by or assigned to)
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id = :userId OR t.assignee.id = :userId")
    long countByUser(@Param("userId") Long userId);
    
    /**
     * Count pending tasks for a specific user (not DONE status)
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.id = :userId OR t.assignee.id = :userId) AND t.status != 'DONE'")
    long countPendingTasksByUser(@Param("userId") Long userId);
} 