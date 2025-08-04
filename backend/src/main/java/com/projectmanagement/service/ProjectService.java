package com.projectmanagement.service;

import com.projectmanagement.dto.ProjectCreateRequest;
import com.projectmanagement.dto.ProjectUpdateRequest;
import com.projectmanagement.dto.ProjectResponse;
import com.projectmanagement.dto.ProjectTemplateResponse;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.User;
import com.projectmanagement.model.ProjectMember;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.repository.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    /**
     * Create a new project
     */
    public ProjectResponse createProject(ProjectCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Convert string values to enums
        Project.Priority priority = convertToPriority(request.getPriority());
        Project.ProjectTemplate template = convertToTemplate(request.getTemplate());
        
        // Validate dates
        validateDates(request.getStartDate(), request.getEndDate());
        
        Project project = new Project(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                priority,
                template,
                user
        );
        
        Project savedProject = projectRepository.save(project);
        
        // Automatically add the creator as an OWNER member
        ProjectMember ownerMember = new ProjectMember(savedProject, user, ProjectMember.Role.OWNER);
        projectMemberRepository.save(ownerMember);
        
        return new ProjectResponse(savedProject);
    }
    
    /**
     * Get all projects for a user
     */
    public List<ProjectResponse> getUserProjects(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get projects created by the user
        List<Project> createdProjects = projectRepository.findByCreatedByOrderByCreatedAtDesc(user);
        
        // Get projects where user is a member
        List<ProjectMember> memberProjects = projectMemberRepository.findByUserId(user.getId());
        List<Project> memberProjectList = memberProjects.stream()
                .map(ProjectMember::getProject)
                .filter(project -> !project.getCreatedBy().getId().equals(user.getId())) // Exclude projects user created
                .collect(Collectors.toList());
        
        // Combine and sort by creation date
        List<Project> allProjects = new java.util.ArrayList<>();
        allProjects.addAll(createdProjects);
        allProjects.addAll(memberProjectList);
        
        allProjects.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        
        return allProjects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get project by ID
     */
    public ProjectResponse getProjectById(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Check if user has access to this project (either creator or member)
        boolean isCreator = project.getCreatedBy().getId().equals(user.getId());
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId());
        
        if (!isCreator && !isMember) {
            throw new RuntimeException("Access denied");
        }
        
        return new ProjectResponse(project);
    }
    
    /**
     * Get available project templates
     */
    public List<ProjectTemplateResponse> getProjectTemplates() {
        return Arrays.asList(
                new ProjectTemplateResponse("none", "No Template", "Start from scratch", "📝"),
                new ProjectTemplateResponse("web-dev", "Web Development", "Full-stack web application", "🌐"),
                new ProjectTemplateResponse("mobile-app", "Mobile App", "Cross-platform mobile application", "📱"),
                new ProjectTemplateResponse("marketing", "Marketing Campaign", "Digital marketing project", "📢"),
                new ProjectTemplateResponse("research", "Research Project", "Academic or business research", "🔬")
        );
    }
    
    /**
     * Search projects by keyword
     */
    public List<ProjectResponse> searchProjects(String keyword, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Project> projects = projectRepository.searchProjectsByUser(user, keyword);
        return projects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get projects by status
     */
    public List<ProjectResponse> getProjectsByStatus(String status, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project.ProjectStatus projectStatus = convertToProjectStatus(status);
        List<Project> projects = projectRepository.findByCreatedByAndStatusOrderByCreatedAtDesc(user, projectStatus);
        
        return projects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get projects by priority
     */
    public List<ProjectResponse> getProjectsByPriority(String priority, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project.Priority projectPriority = convertToPriority(priority);
        List<Project> projects = projectRepository.findByCreatedByAndPriorityOrderByCreatedAtDesc(user, projectPriority);
        
        return projects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Update an existing project
     */
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Check if user has access to this project
        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        // Convert string values to enums
        Project.Priority priority = convertToPriority(request.getPriority());
        Project.ProjectTemplate template = convertToTemplate(request.getTemplate());
        Project.ProjectStatus status = convertToProjectStatus(request.getStatus());
        
        // Validate dates
        validateDates(request.getStartDate(), request.getEndDate());
        
        // Update project fields
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setPriority(priority);
        project.setTemplate(template);
        project.setStatus(status);
        
        Project updatedProject = projectRepository.save(project);
        return new ProjectResponse(updatedProject);
    }
    
    /**
     * Delete a project
     */
    public void deleteProject(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Check if user has access to this project
        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        projectRepository.delete(project);
    }
    
    /**
     * Get project statistics for a user
     */
    public ProjectStatistics getProjectStatistics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        long totalProjects = projectRepository.countByCreatedBy(user);
        long activeProjects = projectRepository.countByCreatedByAndStatus(user, Project.ProjectStatus.ACTIVE);
        long completedProjects = projectRepository.countByCreatedByAndStatus(user, Project.ProjectStatus.COMPLETED);
        long urgentProjects = projectRepository.countByCreatedByAndPriority(user, Project.Priority.URGENT);
        
        // Get task statistics
        TaskService.TaskStatistics taskStats = taskService.getTaskStatistics(user.getId());
        
        return new ProjectStatistics(totalProjects, activeProjects, completedProjects, urgentProjects, taskStats.getPendingTasks());
    }
    
    // Helper methods
    private Project.Priority convertToPriority(String priority) {
        try {
            return Project.Priority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid priority: " + priority);
        }
    }
    
    private Project.ProjectTemplate convertToTemplate(String template) {
        try {
            return Project.ProjectTemplate.valueOf(template.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid template: " + template);
        }
    }
    
    private Project.ProjectStatus convertToProjectStatus(String status) {
        try {
            return Project.ProjectStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }
    
    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date cannot be after end date");
        }
    }
    
    // Inner class for statistics
    public static class ProjectStatistics {
        private long totalProjects;
        private long activeProjects;
        private long completedProjects;
        private long urgentProjects;
        private long pendingTasks;
        
        public ProjectStatistics(long totalProjects, long activeProjects, long completedProjects, long urgentProjects, long pendingTasks) {
            this.totalProjects = totalProjects;
            this.activeProjects = activeProjects;
            this.completedProjects = completedProjects;
            this.urgentProjects = urgentProjects;
            this.pendingTasks = pendingTasks;
        }
        
        // Getters
        public long getTotalProjects() { return totalProjects; }
        public long getActiveProjects() { return activeProjects; }
        public long getCompletedProjects() { return completedProjects; }
        public long getUrgentProjects() { return urgentProjects; }
        public long getPendingTasks() { return pendingTasks; }
    }
} 