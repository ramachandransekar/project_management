package com.projectmanagement.service;

import com.projectmanagement.dto.*;
import com.projectmanagement.model.*;
import com.projectmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskCommentRepository taskCommentRepository;
    
    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final String UPLOAD_DIR = "uploads/tasks/";
    
    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        User createdBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        }
        
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
        }
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
        task.setPriority(Task.Priority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());
        task.setProject(project);
        task.setAssignee(assignee);
        task.setCreatedBy(createdBy);
        
        Task savedTask = taskRepository.save(task);
        return convertToTaskResponse(savedTask);
    }
    
    public TaskResponse getTaskById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Check if user has access to the task
        if (!task.getCreatedBy().getId().equals(userId) && 
            (task.getAssignee() == null || !task.getAssignee().getId().equals(userId))) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToTaskResponse(task);
    }
    
    public List<TaskResponse> getAllTasksByUser(Long userId) {
        List<Task> tasks = taskRepository.findByCreatedById(userId);
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }
    
    public List<TaskResponse> getTasksByProject(Long projectId, Long userId) {
        // Verify user has access to the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }
    
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Check if user has permission to update the task
        if (!task.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(Task.TaskStatus.valueOf(request.getStatus()));
        task.setPriority(Task.Priority.valueOf(request.getPriority()));
        task.setDueDate(request.getDueDate());
        
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToTaskResponse(updatedTask);
    }
    
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Check if user has permission to delete the task
        if (!task.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        taskRepository.delete(task);
    }
    
    public TaskCommentResponse addComment(Long taskId, TaskCommentRequest request, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        TaskComment comment = new TaskComment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setCreatedBy(user);
        
        TaskComment savedComment = taskCommentRepository.save(comment);
        return convertToTaskCommentResponse(savedComment);
    }
    
    public List<TaskCommentResponse> getTaskComments(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Check if user has access to the task
        if (!task.getCreatedBy().getId().equals(userId) && 
            (task.getAssignee() == null || !task.getAssignee().getId().equals(userId))) {
            throw new RuntimeException("Access denied");
        }
        
        List<TaskComment> comments = taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return comments.stream()
                .map(this::convertToTaskCommentResponse)
                .collect(Collectors.toList());
    }
    
    public TaskAttachmentResponse uploadAttachment(Long taskId, MultipartFile file, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = UPLOAD_DIR + fileName;
            
            // Save file
            Path filePathObj = Paths.get(filePath);
            Files.copy(file.getInputStream(), filePathObj);
            
            // Save attachment record
            TaskAttachment attachment = new TaskAttachment();
            attachment.setFileName(fileName);
            attachment.setOriginalFileName(originalFileName);
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setFilePath(filePath);
            attachment.setTask(task);
            attachment.setUploadedBy(user);
            
            TaskAttachment savedAttachment = taskAttachmentRepository.save(attachment);
            return convertToTaskAttachmentResponse(savedAttachment);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    public List<TaskAttachmentResponse> getTaskAttachments(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // Check if user has access to the task
        if (!task.getCreatedBy().getId().equals(userId) && 
            (task.getAssignee() == null || !task.getAssignee().getId().equals(userId))) {
            throw new RuntimeException("Access denied");
        }
        
        List<TaskAttachment> attachments = taskAttachmentRepository.findByTaskIdOrderByUploadedAtDesc(taskId);
        return attachments.stream()
                .map(this::convertToTaskAttachmentResponse)
                .collect(Collectors.toList());
    }
    
    public void deleteAttachment(Long attachmentId, Long userId) {
        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        
        // Check if user has permission to delete the attachment
        if (!attachment.getUploadedBy().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        try {
            // Delete file from filesystem
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
            
            // Delete from database
            taskAttachmentRepository.delete(attachment);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
    
    /**
     * Get task statistics for a user
     */
    public TaskStatistics getTaskStatistics(Long userId) {
        long totalTasks = taskRepository.countByUser(userId);
        long pendingTasks = taskRepository.countPendingTasksByUser(userId);
        long todoTasks = taskRepository.countByUserAndStatus(userId, Task.TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByUserAndStatus(userId, Task.TaskStatus.IN_PROGRESS);
        long reviewTasks = taskRepository.countByUserAndStatus(userId, Task.TaskStatus.REVIEW);
        long doneTasks = taskRepository.countByUserAndStatus(userId, Task.TaskStatus.DONE);
        
        return new TaskStatistics(totalTasks, pendingTasks, todoTasks, inProgressTasks, reviewTasks, doneTasks);
    }
    
    // Inner class for task statistics
    public static class TaskStatistics {
        private long totalTasks;
        private long pendingTasks;
        private long todoTasks;
        private long inProgressTasks;
        private long reviewTasks;
        private long doneTasks;
        
        public TaskStatistics(long totalTasks, long pendingTasks, long todoTasks, long inProgressTasks, long reviewTasks, long doneTasks) {
            this.totalTasks = totalTasks;
            this.pendingTasks = pendingTasks;
            this.todoTasks = todoTasks;
            this.inProgressTasks = inProgressTasks;
            this.reviewTasks = reviewTasks;
            this.doneTasks = doneTasks;
        }
        
        // Getters
        public long getTotalTasks() { return totalTasks; }
        public long getPendingTasks() { return pendingTasks; }
        public long getTodoTasks() { return todoTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public long getReviewTasks() { return reviewTasks; }
        public long getDoneTasks() { return doneTasks; }
    }
    
    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().name());
        response.setPriority(task.getPriority().name());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getProject() != null) {
            response.setProjectId(task.getProject().getId());
            response.setProjectName(task.getProject().getName());
        }
        
        if (task.getAssignee() != null) {
            response.setAssigneeId(task.getAssignee().getId());
            response.setAssigneeName(task.getAssignee().getFullName());
        }
        
        response.setCreatedById(task.getCreatedBy().getId());
        response.setCreatedByName(task.getCreatedBy().getFullName());
        
        // Convert comments
        List<TaskCommentResponse> comments = task.getComments().stream()
                .map(this::convertToTaskCommentResponse)
                .collect(Collectors.toList());
        response.setComments(comments);
        
        // Convert attachments
        List<TaskAttachmentResponse> attachments = task.getAttachments().stream()
                .map(this::convertToTaskAttachmentResponse)
                .collect(Collectors.toList());
        response.setAttachments(attachments);
        
        return response;
    }
    
    private TaskCommentResponse convertToTaskCommentResponse(TaskComment comment) {
        TaskCommentResponse response = new TaskCommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setTaskId(comment.getTask().getId());
        response.setCreatedById(comment.getCreatedBy().getId());
        response.setCreatedByName(comment.getCreatedBy().getFullName());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
    
    private TaskAttachmentResponse convertToTaskAttachmentResponse(TaskAttachment attachment) {
        TaskAttachmentResponse response = new TaskAttachmentResponse();
        response.setId(attachment.getId());
        response.setFileName(attachment.getFileName());
        response.setOriginalFileName(attachment.getOriginalFileName());
        response.setFileType(attachment.getFileType());
        response.setFileSize(attachment.getFileSize());
        response.setFilePath(attachment.getFilePath());
        response.setTaskId(attachment.getTask().getId());
        response.setUploadedById(attachment.getUploadedBy().getId());
        response.setUploadedByName(attachment.getUploadedBy().getFullName());
        response.setUploadedAt(attachment.getUploadedAt());
        return response;
    }
} 