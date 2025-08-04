package com.projectmanagement.service;

import com.projectmanagement.dto.*;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.Task;
import com.projectmanagement.model.User;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get comprehensive progress data for a project
     */
    public ProgressResponse getProjectProgress(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Check if user has access to this project
        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        
        // Calculate basic progress
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
                .count();
        double completionPercentage = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        // Determine project health
        String projectHealth = calculateProjectHealth(project, tasks, completionPercentage);
        
        ProgressResponse response = new ProgressResponse(
                projectId, project.getName(), totalTasks, completedTasks, 
                completionPercentage, projectHealth, project.getStartDate(), project.getEndDate()
        );
        
        // Add task status breakdown
        response.setTaskStatusBreakdown(getTaskStatusBreakdown(tasks));
        
        // Add milestones (simulated for now)
        response.setMilestones(getProjectMilestones(project));
        
        // Add task progress
        response.setTaskProgress(getTaskProgress(tasks));
        
        // Add team progress
        response.setTeamProgress(getTeamMemberProgress(project));
        
        // Add burndown data
        response.setBurndownData(getBurndownData(project, tasks));
        
        // Add activity feed
        response.setActivityFeed(getActivityFeed(project));
        
        return response;
    }
    
    /**
     * Get progress for all user projects
     */
    public List<ProgressResponse> getAllProjectsProgress(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Project> projects = projectRepository.findByCreatedByOrderByCreatedAtDesc(user);
        
        return projects.stream()
                .map(project -> {
                    List<Task> tasks = taskRepository.findByProjectId(project.getId());
                    int totalTasks = tasks.size();
                    int completedTasks = (int) tasks.stream()
                            .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
                            .count();
                    double completionPercentage = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
                    String projectHealth = calculateProjectHealth(project, tasks, completionPercentage);
                    
                    return new ProgressResponse(
                            project.getId(), project.getName(), totalTasks, completedTasks,
                            completionPercentage, projectHealth, project.getStartDate(), project.getEndDate()
                    );
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get team member progress leaderboard
     */
    public List<TeamMemberProgressResponse> getTeamLeaderboard(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        
        // Group tasks by assignee
        Map<User, List<Task>> tasksByUser = tasks.stream()
                .filter(task -> task.getAssignee() != null)
                .collect(Collectors.groupingBy(Task::getAssignee));
        
        List<TeamMemberProgressResponse> leaderboard = new ArrayList<>();
        int rank = 1;
        
        for (Map.Entry<User, List<Task>> entry : tasksByUser.entrySet()) {
            User assignee = entry.getKey();
            List<Task> userTasks = entry.getValue();
            
            int totalTasks = userTasks.size();
            int completedTasks = (int) userTasks.stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
                    .count();
            double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
            
            leaderboard.add(new TeamMemberProgressResponse(
                    assignee.getId(), assignee.getUsername(), assignee.getFullName(),
                    totalTasks, completedTasks, completionRate, rank++
            ));
        }
        
        // Sort by completion rate descending
        leaderboard.sort((a, b) -> Double.compare(b.getCompletionRate(), a.getCompletionRate()));
        
        // Update ranks
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }
        
        return leaderboard;
    }
    
    /**
     * Calculate project health status
     */
    private String calculateProjectHealth(Project project, List<Task> tasks, double completionPercentage) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = project.getEndDate();
        
        if (endDate == null) {
            return "🟡 At Risk"; // No end date set
        }
        
        long daysUntilDeadline = ChronoUnit.DAYS.between(today, endDate);
        
        if (daysUntilDeadline < 0) {
            return completionPercentage < 100 ? "🔴 Behind Schedule" : "🟢 On Track";
        }
        
        // Calculate expected progress based on time elapsed
        long totalDays = ChronoUnit.DAYS.between(project.getStartDate(), endDate);
        long daysElapsed = ChronoUnit.DAYS.between(project.getStartDate(), today);
        double expectedProgress = totalDays > 0 ? (double) daysElapsed / totalDays * 100 : 0;
        
        if (completionPercentage >= expectedProgress + 10) {
            return "🟢 On Track";
        } else if (completionPercentage >= expectedProgress - 10) {
            return "🟡 At Risk";
        } else {
            return "🔴 Behind Schedule";
        }
    }
    
    /**
     * Get task status breakdown
     */
    private Map<String, Integer> getTaskStatusBreakdown(List<Task> tasks) {
        Map<String, Integer> breakdown = new HashMap<>();
        breakdown.put("TODO", 0);
        breakdown.put("IN_PROGRESS", 0);
        breakdown.put("REVIEW", 0);
        breakdown.put("DONE", 0);
        
        for (Task task : tasks) {
            String status = task.getStatus().name();
            breakdown.put(status, breakdown.get(status) + 1);
        }
        
        return breakdown;
    }
    
    /**
     * Get project milestones (simulated)
     */
    private List<MilestoneResponse> getProjectMilestones(Project project) {
        List<MilestoneResponse> milestones = new ArrayList<>();
        
        // Create some sample milestones based on project dates
        if (project.getStartDate() != null && project.getEndDate() != null) {
            long totalDays = ChronoUnit.DAYS.between(project.getStartDate(), project.getEndDate());
            
            milestones.add(new MilestoneResponse(
                    1L, "Project Kickoff", "Project initiation and planning",
                    project.getStartDate(), true, "COMPLETED"
            ));
            
            milestones.add(new MilestoneResponse(
                    2L, "Development Phase", "Core development work",
                    project.getStartDate().plusDays(totalDays / 3), false, "IN_PROGRESS"
            ));
            
            milestones.add(new MilestoneResponse(
                    3L, "Testing Phase", "Quality assurance and testing",
                    project.getStartDate().plusDays(2 * totalDays / 3), false, "PENDING"
            ));
            
            milestones.add(new MilestoneResponse(
                    4L, "Project Completion", "Final delivery and handover",
                    project.getEndDate(), false, "PENDING"
            ));
        }
        
        return milestones;
    }
    
    /**
     * Get task progress details
     */
    private List<TaskProgressResponse> getTaskProgress(List<Task> tasks) {
        return tasks.stream()
                .map(task -> {
                    double progressPercentage = calculateTaskProgress(task);
                    String assigneeName = task.getAssignee() != null ? 
                            task.getAssignee().getFullName() : "Unassigned";
                    
                    TaskProgressResponse response = new TaskProgressResponse(
                            task.getId(), task.getTitle(), task.getStatus().name(),
                            progressPercentage, task.getDueDate(), assigneeName
                    );
                    
                    // Add simulated subtasks
                    response.setSubtasks(getTaskSubtasks(task));
                    
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate task progress percentage
     */
    private double calculateTaskProgress(Task task) {
        switch (task.getStatus()) {
            case TODO: return 0.0;
            case IN_PROGRESS: return 50.0;
            case REVIEW: return 80.0;
            case DONE: return 100.0;
            default: return 0.0;
        }
    }
    
    /**
     * Get task subtasks (simulated)
     */
    private List<SubtaskResponse> getTaskSubtasks(Task task) {
        List<SubtaskResponse> subtasks = new ArrayList<>();
        
        // Add some sample subtasks based on task status
        subtasks.add(new SubtaskResponse(1L, "Define requirements", true));
        subtasks.add(new SubtaskResponse(2L, "Create design", task.getStatus() != Task.TaskStatus.TODO));
        subtasks.add(new SubtaskResponse(3L, "Implement functionality", 
                task.getStatus() == Task.TaskStatus.IN_PROGRESS || 
                task.getStatus() == Task.TaskStatus.REVIEW || 
                task.getStatus() == Task.TaskStatus.DONE));
        subtasks.add(new SubtaskResponse(4L, "Testing", 
                task.getStatus() == Task.TaskStatus.REVIEW || 
                task.getStatus() == Task.TaskStatus.DONE));
        subtasks.add(new SubtaskResponse(5L, "Documentation", task.getStatus() == Task.TaskStatus.DONE));
        
        return subtasks;
    }
    
    /**
     * Get team member progress
     */
    private List<TeamMemberProgressResponse> getTeamMemberProgress(Project project) {
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        
        Map<User, List<Task>> tasksByUser = tasks.stream()
                .filter(task -> task.getAssignee() != null)
                .collect(Collectors.groupingBy(Task::getAssignee));
        
        List<TeamMemberProgressResponse> teamProgress = new ArrayList<>();
        int rank = 1;
        
        for (Map.Entry<User, List<Task>> entry : tasksByUser.entrySet()) {
            User assignee = entry.getKey();
            List<Task> userTasks = entry.getValue();
            
            int totalTasks = userTasks.size();
            int completedTasks = (int) userTasks.stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
                    .count();
            double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
            
            teamProgress.add(new TeamMemberProgressResponse(
                    assignee.getId(), assignee.getUsername(), assignee.getFullName(),
                    totalTasks, completedTasks, completionRate, rank++
            ));
        }
        
        teamProgress.sort((a, b) -> Double.compare(b.getCompletionRate(), a.getCompletionRate()));
        
        for (int i = 0; i < teamProgress.size(); i++) {
            teamProgress.get(i).setRank(i + 1);
        }
        
        return teamProgress;
    }
    
    /**
     * Get burndown chart data
     */
    private List<BurndownDataResponse> getBurndownData(Project project, List<Task> tasks) {
        List<BurndownDataResponse> burndownData = new ArrayList<>();
        
        if (project.getStartDate() == null || project.getEndDate() == null) {
            return burndownData;
        }
        
        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        LocalDate today = LocalDate.now();
        
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream()
                .filter(task -> task.getStatus() == Task.TaskStatus.DONE)
                .count();
        
        // Generate data for each day from start to end
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int remainingTasks = totalTasks - completedTasks;
            int idealRemainingTasks = calculateIdealRemainingTasks(totalTasks, startDate, endDate, currentDate);
            
            burndownData.add(new BurndownDataResponse(currentDate, remainingTasks, idealRemainingTasks));
            currentDate = currentDate.plusDays(1);
        }
        
        return burndownData;
    }
    
    /**
     * Calculate ideal remaining tasks for burndown chart
     */
    private int calculateIdealRemainingTasks(int totalTasks, LocalDate startDate, LocalDate endDate, LocalDate currentDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long daysElapsed = ChronoUnit.DAYS.between(startDate, currentDate);
        
        if (totalDays <= 0) return 0;
        
        double progressRatio = (double) daysElapsed / totalDays;
        return Math.max(0, totalTasks - (int) (totalTasks * progressRatio));
    }
    
    /**
     * Get activity feed
     */
    private List<ActivityFeedResponse> getActivityFeed(Project project) {
        List<ActivityFeedResponse> activityFeed = new ArrayList<>();
        List<Task> tasks = taskRepository.findByProjectId(project.getId());
        
        // Generate activity feed based on task updates
        for (Task task : tasks) {
            if (task.getStatus() == Task.TaskStatus.DONE) {
                activityFeed.add(new ActivityFeedResponse(
                        1L, "TASK_COMPLETED", "Task completed",
                        task.getAssignee() != null ? task.getAssignee().getFullName() : "Unknown",
                        task.getUpdatedAt() != null ? task.getUpdatedAt() : LocalDateTime.now(),
                        project.getName(), task.getTitle()
                ));
            }
        }
        
        // Sort by timestamp descending
        activityFeed.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        return activityFeed;
    }
} 