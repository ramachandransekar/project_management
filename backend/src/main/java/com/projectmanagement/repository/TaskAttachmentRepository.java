package com.projectmanagement.repository;

import com.projectmanagement.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    
    @Query("SELECT ta FROM TaskAttachment ta WHERE ta.task.id = :taskId ORDER BY ta.uploadedAt DESC")
    List<TaskAttachment> findByTaskIdOrderByUploadedAtDesc(@Param("taskId") Long taskId);
} 