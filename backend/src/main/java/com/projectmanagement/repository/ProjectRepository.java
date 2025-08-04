package com.projectmanagement.repository;

import com.projectmanagement.model.Project;
import com.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    /**
     * Find all projects created by a specific user
     */
    List<Project> findByCreatedByOrderByCreatedAtDesc(User createdBy);
    
    /**
     * Find all projects by status
     */
    List<Project> findByStatusOrderByCreatedAtDesc(Project.ProjectStatus status);
    
    /**
     * Find all projects by priority
     */
    List<Project> findByPriorityOrderByCreatedAtDesc(Project.Priority priority);
    
    /**
     * Find all projects by template
     */
    List<Project> findByTemplateOrderByCreatedAtDesc(Project.ProjectTemplate template);
    
    /**
     * Find projects by user and status
     */
    List<Project> findByCreatedByAndStatusOrderByCreatedAtDesc(User createdBy, Project.ProjectStatus status);
    
    /**
     * Find projects by user and priority
     */
    List<Project> findByCreatedByAndPriorityOrderByCreatedAtDesc(User createdBy, Project.Priority priority);
    
    /**
     * Search projects by name containing the given keyword
     */
    @Query("SELECT p FROM Project p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Project> searchProjects(@Param("keyword") String keyword);
    
    /**
     * Search projects by name containing the given keyword for a specific user
     */
    @Query("SELECT p FROM Project p WHERE p.createdBy = :user AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    List<Project> searchProjectsByUser(@Param("user") User user, @Param("keyword") String keyword);
    
    /**
     * Count projects by status for a specific user
     */
    long countByCreatedByAndStatus(User createdBy, Project.ProjectStatus status);
    
    /**
     * Count projects by priority for a specific user
     */
    long countByCreatedByAndPriority(User createdBy, Project.Priority priority);
    
    /**
     * Count total projects for a specific user
     */
    long countByCreatedBy(User createdBy);
} 