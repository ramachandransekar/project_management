package com.projectmanagement.service;

import com.projectmanagement.dto.*;
import com.projectmanagement.model.*;
import com.projectmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamCollaborationService {
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    @Autowired
    private ProjectNoteRepository projectNoteRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    // Project Member Management
    @Transactional
    public ProjectMemberResponse addMemberToProject(Long projectId, AddMemberRequest request, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User userToAdd = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is already a member
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new RuntimeException("User is already a member of this project");
        }
        
        ProjectMember projectMember = new ProjectMember(project, userToAdd, request.getRole());
        projectMember = projectMemberRepository.save(projectMember);
        
        // Log activity
        ActivityLog activityLog = new ActivityLog(project, currentUser, 
                ActivityLog.ActivityType.MEMBER_ADDED, 
                currentUser.getFullName() + " added " + userToAdd.getFullName() + " to the project");
        activityLogRepository.save(activityLog);
        
        return new ProjectMemberResponse(projectMember);
    }
    
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);
        return members.stream()
                .map(ProjectMemberResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void removeMemberFromProject(Long projectId, Long userId, User currentUser) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project member not found"));
        
        User userToRemove = projectMember.getUser();
        projectMemberRepository.delete(projectMember);
        
        // Log activity
        ActivityLog activityLog = new ActivityLog(projectMember.getProject(), currentUser, 
                ActivityLog.ActivityType.MEMBER_REMOVED, 
                currentUser.getFullName() + " removed " + userToRemove.getFullName() + " from the project");
        activityLogRepository.save(activityLog);
    }
    
    // Activity Log Management
    public List<ActivityLogResponse> getProjectActivity(Long projectId) {
        List<ActivityLog> activities = activityLogRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        return activities.stream()
                .map(ActivityLogResponse::new)
                .collect(Collectors.toList());
    }
    
    public List<ActivityLogResponse> getRecentProjectActivity(Long projectId, int limit) {
        List<ActivityLog> activities = activityLogRepository.findRecentByProjectId(projectId, limit);
        return activities.stream()
                .map(ActivityLogResponse::new)
                .collect(Collectors.toList());
    }
    
    // Project Notes Management
    @Transactional
    public ProjectNoteResponse createOrUpdateProjectNote(Long projectId, ProjectNoteRequest request, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        Optional<ProjectNote> existingNote = projectNoteRepository.findByProjectId(projectId);
        ProjectNote projectNote;
        
        if (existingNote.isPresent()) {
            projectNote = existingNote.get();
            projectNote.setContent(request.getContent());
            projectNote.setLastUpdatedBy(currentUser);
        } else {
            projectNote = new ProjectNote(project, request.getContent(), currentUser);
        }
        
        projectNote = projectNoteRepository.save(projectNote);
        
        // Log activity
        ActivityLog activityLog = new ActivityLog(project, currentUser, 
                ActivityLog.ActivityType.NOTE_UPDATED, 
                currentUser.getFullName() + " updated the project notes");
        activityLogRepository.save(activityLog);
        
        return new ProjectNoteResponse(projectNote);
    }
    
    public ProjectNoteResponse getProjectNote(Long projectId) {
        ProjectNote projectNote = projectNoteRepository.findByProjectId(projectId)
                .orElse(null);
        
        if (projectNote == null) {
            return new ProjectNoteResponse(); // Return empty response
        }
        
        return new ProjectNoteResponse(projectNote);
    }
    
    // Task Comments (using existing TaskComment model)
    @Transactional
    public TaskCommentResponse addTaskComment(Long taskId, TaskCommentRequest request, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        TaskComment comment = new TaskComment(request.getContent(), task, currentUser);
        
        // Save comment (assuming TaskCommentRepository exists)
        // This would need to be implemented if not already present
        
        // Log activity
        ActivityLog activityLog = new ActivityLog(task.getProject(), currentUser, 
                ActivityLog.ActivityType.COMMENT_ADDED, 
                currentUser.getFullName() + " commented on task: " + task.getTitle());
        activityLog.setEntityType("TASK");
        activityLog.setEntityId(taskId);
        activityLogRepository.save(activityLog);
        
        return new TaskCommentResponse(comment.getId(), comment.getContent(), comment.getTask().getId(),
                comment.getCreatedBy().getId(), comment.getCreatedBy().getFullName(), comment.getCreatedAt());
    }
    
    // Utility method to log task activities
    public void logTaskActivity(Task task, User user, ActivityLog.ActivityType activityType, String description) {
        ActivityLog activityLog = new ActivityLog(task.getProject(), user, activityType, description);
        activityLog.setEntityType("TASK");
        activityLog.setEntityId(task.getId());
        activityLogRepository.save(activityLog);
    }
} 