package com.projectmanagement.repository;

import com.projectmanagement.model.ProjectNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectNoteRepository extends JpaRepository<ProjectNote, Long> {
    
    Optional<ProjectNote> findByProjectId(Long projectId);
} 