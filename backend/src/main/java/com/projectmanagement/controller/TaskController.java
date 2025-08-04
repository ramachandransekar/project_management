package com.projectmanagement.controller;

import com.projectmanagement.dto.*;
import com.projectmanagement.service.TaskService;
import com.projectmanagement.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request,
                                                  Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TaskResponse task = taskService.createTask(request, userDetails.getId());
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId,
                                                   Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TaskResponse task = taskService.getTaskById(taskId, userDetails.getId());
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<TaskResponse> tasks = taskService.getAllTasksByUser(userDetails.getId());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId,
                                                               Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<TaskResponse> tasks = taskService.getTasksByProject(projectId, userDetails.getId());
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId,
                                                  @Valid @RequestBody TaskUpdateRequest request,
                                                  Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TaskResponse task = taskService.updateTask(taskId, request, userDetails.getId());
        return ResponseEntity.ok(task);
    }
    
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId,
                                          Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        taskService.deleteTask(taskId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
    
    // Comment endpoints
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<TaskCommentResponse> addComment(@PathVariable Long taskId,
                                                         @Valid @RequestBody TaskCommentRequest request,
                                                         Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TaskCommentResponse comment = taskService.addComment(taskId, request, userDetails.getId());
        return ResponseEntity.ok(comment);
    }
    
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<TaskCommentResponse>> getTaskComments(@PathVariable Long taskId,
                                                                    Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<TaskCommentResponse> comments = taskService.getTaskComments(taskId, userDetails.getId());
        return ResponseEntity.ok(comments);
    }
    
    // File attachment endpoints
    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<TaskAttachmentResponse> uploadAttachment(@PathVariable Long taskId,
                                                                  @RequestParam("file") MultipartFile file,
                                                                  Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        TaskAttachmentResponse attachment = taskService.uploadAttachment(taskId, file, userDetails.getId());
        return ResponseEntity.ok(attachment);
    }
    
    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<TaskAttachmentResponse>> getTaskAttachments(@PathVariable Long taskId,
                                                                          Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<TaskAttachmentResponse> attachments = taskService.getTaskAttachments(taskId, userDetails.getId());
        return ResponseEntity.ok(attachments);
    }
    
        @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId,
                                              Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        taskService.deleteAttachment(attachmentId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get task statistics for the authenticated user
     */
    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getTaskStatistics(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            TaskService.TaskStatistics statistics = taskService.getTaskStatistics(userDetails.getId());
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 