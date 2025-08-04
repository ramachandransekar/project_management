import React, { useState, useEffect } from 'react';
import { PieChart, Pie, Cell, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { CheckCircle, Clock, AlertCircle, TrendingUp, Users, Calendar, Activity } from 'lucide-react';
import './ProgressTracking.css';

const ProgressTracking = () => {
  const [projects, setProjects] = useState([]);
  const [selectedProject, setSelectedProject] = useState(null);
  const [projectProgress, setProjectProgress] = useState(null);
  const [teamLeaderboard, setTeamLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');
  const [sortBy, setSortBy] = useState('completion');

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042'];

  useEffect(() => {
    fetchProjects();
  }, []);

  useEffect(() => {
    if (selectedProject) {
      fetchProjectProgress(selectedProject);
      fetchTeamLeaderboard(selectedProject);
    }
  }, [selectedProject]);

  const fetchProjects = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/progress/projects', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setProjects(data);
        if (data.length > 0) {
          setSelectedProject(data[0].projectId);
        }
      }
    } catch (error) {
      console.error('Error fetching projects:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchProjectProgress = async (projectId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/progress/project/${projectId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setProjectProgress(data);
      }
    } catch (error) {
      console.error('Error fetching project progress:', error);
    }
  };

  const fetchTeamLeaderboard = async (projectId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/progress/project/${projectId}/leaderboard`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setTeamLeaderboard(data);
      }
    } catch (error) {
      console.error('Error fetching team leaderboard:', error);
    }
  };

  const getHealthStatusIcon = (health) => {
    if (health.includes('🟢')) return <CheckCircle className="text-green-500" />;
    if (health.includes('🟡')) return <AlertCircle className="text-yellow-500" />;
    if (health.includes('🔴')) return <AlertCircle className="text-red-500" />;
    return <Clock className="text-gray-500" />;
  };

  const getHealthStatusColor = (health) => {
    if (health.includes('🟢')) return 'bg-green-100 text-green-800';
    if (health.includes('🟡')) return 'bg-yellow-100 text-yellow-800';
    if (health.includes('🔴')) return 'bg-red-100 text-red-800';
    return 'bg-gray-100 text-gray-800';
  };

  const prepareTaskStatusData = (breakdown) => {
    if (!breakdown) return [];
    return Object.entries(breakdown).map(([status, count]) => ({
      name: status.replace('_', ' '),
      value: count
    }));
  };

  const prepareBurndownData = (burndownData) => {
    if (!burndownData) return [];
    return burndownData.map(item => ({
      date: new Date(item.date).toLocaleDateString(),
      remaining: item.remainingTasks,
      ideal: item.idealRemainingTasks
    }));
  };



  const filteredProjects = projects.filter(project => {
    if (filter === 'all') return true;
    if (filter === 'on-track') return project.projectHealth.includes('🟢');
    if (filter === 'at-risk') return project.projectHealth.includes('🟡');
    if (filter === 'behind') return project.projectHealth.includes('🔴');
    return true;
  });

  const sortedProjects = [...filteredProjects].sort((a, b) => {
    if (sortBy === 'completion') return b.completionPercentage - a.completionPercentage;
    if (sortBy === 'deadline') {
      if (!a.endDate || !b.endDate) return 0;
      return new Date(a.endDate) - new Date(b.endDate);
    }
    return 0;
  });

  if (loading) {
    return (
      <div className="progress-tracking">
        <div className="loading">Loading progress data...</div>
      </div>
    );
  }

  return (
    <div className="progress-tracking">
      <div className="progress-header">
        <h1>Progress Tracking</h1>
        <div className="progress-controls">
          <select 
            value={filter} 
            onChange={(e) => setFilter(e.target.value)}
            className="filter-select"
          >
            <option value="all">All Projects</option>
            <option value="on-track">On Track</option>
            <option value="at-risk">At Risk</option>
            <option value="behind">Behind Schedule</option>
          </select>
          <select 
            value={sortBy} 
            onChange={(e) => setSortBy(e.target.value)}
            className="sort-select"
          >
            <option value="completion">Sort by Completion</option>
            <option value="deadline">Sort by Deadline</option>
          </select>
        </div>
      </div>

      {/* Project-Level Progress Bars */}
      <div className="projects-section">
        <h2>Project Progress Overview</h2>
        <div className="projects-grid">
          {sortedProjects.map(project => (
            <div 
              key={project.projectId} 
              className={`project-card ${selectedProject === project.projectId ? 'selected' : ''}`}
              onClick={() => setSelectedProject(project.projectId)}
            >
              <div className="project-header">
                <h3>{project.projectName}</h3>
                <span className={`health-status ${getHealthStatusColor(project.projectHealth)}`}>
                  {getHealthStatusIcon(project.projectHealth)}
                  {project.projectHealth.replace(/[🟢🟡🔴]/g, '').trim()}
                </span>
              </div>
              
              <div className="progress-bar-container">
                <div className="progress-bar">
                  <div 
                    className="progress-fill"
                    style={{ width: `${project.completionPercentage}%` }}
                  ></div>
                </div>
                <div className="progress-text">
                  {project.completedTasks} of {project.totalTasks} tasks completed
                </div>
                <div className="progress-percentage">
                  {project.completionPercentage.toFixed(1)}%
                </div>
              </div>
              
              <div className="project-dates">
                <span>Start: {new Date(project.startDate).toLocaleDateString()}</span>
                <span>End: {new Date(project.endDate).toLocaleDateString()}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {selectedProject && projectProgress && (
        <div className="project-details">
          <h2>{projectProgress.projectName} - Detailed Progress</h2>
          
          <div className="details-grid">
            {/* Task Status Breakdown */}
            <div className="chart-card">
              <h3>Task Status Breakdown</h3>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={prepareTaskStatusData(projectProgress.taskStatusBreakdown)}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {prepareTaskStatusData(projectProgress.taskStatusBreakdown).map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>

            {/* Burndown Chart */}
            <div className="chart-card">
              <h3>Burndown Chart</h3>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={prepareBurndownData(projectProgress.burndownData)}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="remaining" stroke="#8884d8" name="Actual Remaining" />
                  <Line type="monotone" dataKey="ideal" stroke="#82ca9d" name="Ideal Remaining" />
                </LineChart>
              </ResponsiveContainer>
            </div>

            {/* Team Progress Leaderboard */}
            <div className="leaderboard-card">
              <h3>Team Progress Leaderboard</h3>
              <div className="leaderboard">
                {teamLeaderboard.map((member, index) => (
                  <div key={member.userId} className="leaderboard-item">
                    <div className="rank">#{member.rank}</div>
                    <div className="member-info">
                      <div className="member-name">{member.fullName}</div>
                      <div className="member-stats">
                        {member.completedTasks}/{member.totalTasks} tasks ({member.completionRate.toFixed(1)}%)
                      </div>
                    </div>
                    <div className="member-progress">
                      <div className="progress-bar">
                        <div 
                          className="progress-fill"
                          style={{ width: `${member.completionRate}%` }}
                        ></div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

                         {/* Milestone Tracker */}
             <div className="milestones-card">
               <h3>Project Milestones</h3>
               <div className="milestones">
                 {projectProgress.milestones?.map(milestone => (
                   <div key={milestone.id} className={`milestone ${milestone.completed ? 'completed' : ''}`}>
                     <div className="milestone-header">
                       <h4>{milestone.name}</h4>
                       <span className={`milestone-status ${milestone.completed ? 'completed' : 'pending'}`}>
                         {milestone.completed ? '✓' : '○'}
                       </span>
                     </div>
                     <p>{milestone.description}</p>
                     <div className="milestone-date">
                       Due: {new Date(milestone.dueDate).toLocaleDateString()}
                     </div>
                   </div>
                 ))}
               </div>
             </div>

             {/* Timeline View */}
             <div className="timeline-card">
               <h3>Project Timeline</h3>
               <div className="timeline">
                 {projectProgress.taskProgress?.map((task, index) => (
                   <div key={task.id} className="timeline-item">
                     <div className="timeline-marker">
                       <div className={`timeline-dot ${task.status.toLowerCase()}`}></div>
                       {index < projectProgress.taskProgress.length - 1 && <div className="timeline-line"></div>}
                     </div>
                     <div className="timeline-content">
                       <h4>{task.title}</h4>
                       <div className="timeline-details">
                         <span className="task-status-badge">{task.status.replace('_', ' ')}</span>
                         <span className="task-assignee">{task.assigneeName}</span>
                         {task.dueDate && (
                           <span className="task-due-date">
                             Due: {new Date(task.dueDate).toLocaleDateString()}
                           </span>
                         )}
                       </div>
                       <div className="timeline-progress">
                         <div className="progress-bar">
                           <div 
                             className="progress-fill"
                             style={{ width: `${task.progressPercentage}%` }}
                           ></div>
                         </div>
                         <span className="progress-text">{task.progressPercentage.toFixed(0)}%</span>
                       </div>
                     </div>
                   </div>
                 ))}
               </div>
             </div>
          </div>

          {/* Individual Task Progress */}
          <div className="tasks-section">
            <h3>Individual Task Progress</h3>
            <div className="tasks-grid">
              {projectProgress.taskProgress?.map(task => (
                <div key={task.id} className="task-card">
                  <div className="task-header">
                    <h4>{task.title}</h4>
                    <span className={`task-status ${task.status.toLowerCase()}`}>
                      {task.status.replace('_', ' ')}
                    </span>
                  </div>
                  
                  <div className="task-progress">
                    <div className="progress-bar">
                      <div 
                        className="progress-fill"
                        style={{ width: `${task.progressPercentage}%` }}
                      ></div>
                    </div>
                    <span className="progress-text">{task.progressPercentage.toFixed(0)}%</span>
                  </div>
                  
                  <div className="task-details">
                    <span>Assignee: {task.assigneeName}</span>
                    {task.dueDate && (
                      <span>Due: {new Date(task.dueDate).toLocaleDateString()}</span>
                    )}
                  </div>
                  
                  {task.subtasks && task.subtasks.length > 0 && (
                    <div className="subtasks">
                      <h5>Subtasks:</h5>
                      {task.subtasks.map(subtask => (
                        <div key={subtask.id} className="subtask">
                          <input 
                            type="checkbox" 
                            checked={subtask.completed}
                            readOnly
                          />
                          <span className={subtask.completed ? 'completed' : ''}>
                            {subtask.title}
                          </span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Activity Feed */}
          <div className="activity-section">
            <h3>Activity Feed</h3>
            <div className="activity-feed">
              {projectProgress.activityFeed?.map(activity => (
                <div key={activity.id} className="activity-item">
                  <div className="activity-icon">
                    <Activity size={16} />
                  </div>
                  <div className="activity-content">
                    <div className="activity-description">{activity.description}</div>
                    <div className="activity-meta">
                      by {activity.userName} • {new Date(activity.timestamp).toLocaleString()}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProgressTracking; 