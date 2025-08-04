import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../utils/axiosConfig';
import ProjectCreation from './ProjectCreation';
import ProjectView from './ProjectView';
import ProjectEdit from './ProjectEdit';
import ProgressTracking from './ProgressTracking';
import TeamCollaboration from './TeamCollaboration';
import TimeTracking from './TimeTracking';
import TaskManagement from './TaskManagement';
import UserCreation from './UserCreation';
import './Dashboard.css';

const Dashboard = () => {
  const { user, logout, loading } = useAuth();
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState('dashboard');
  const [allProjects, setAllProjects] = useState([]);
  const [projectStats, setProjectStats] = useState({
    activeProjects: 0,
    pendingTasks: 0,
    teamMembers: 0
  });
  const [viewingProject, setViewingProject] = useState(null);
  const [editingProject, setEditingProject] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [deleteModal, setDeleteModal] = useState({ show: false, project: null, hasTasks: false });

  // Show loading while authentication is being checked
  if (loading) {
    return <div>Loading...</div>;
  }

  // Redirect if not authenticated
  if (!user) {
    navigate('/');
    return null;
  }

  // Fetch user's projects and statistics on component mount
  useEffect(() => {
    if (user) {
      fetchUserProjects();
      fetchProjectStatistics();
    }
  }, [user]);

  const fetchUserProjects = async () => {
    try {
      const response = await api.get('/projects');
      setAllProjects(response.data); // Store all projects
    } catch (error) {
      console.error('Error fetching projects:', error);
    }
  };

  const fetchProjectStatistics = async () => {
    try {
      const response = await api.get('/projects/statistics');
      const stats = response.data;
      setProjectStats({
        activeProjects: stats.activeProjects,
        pendingTasks: stats.pendingTasks || 0, // Use real data from API
        teamMembers: 8 // Mock data for now
      });
    } catch (error) {
      console.error('Error fetching statistics:', error);
    }
  };

  const handleProjectCreated = (newProject) => {
    setAllProjects(prevProjects => [newProject, ...prevProjects]); // Add new project to the beginning
    fetchProjectStatistics(); // Refresh statistics
  };

  const handleViewProject = (project) => {
    setViewingProject(project);
  };

  const handleCloseView = () => {
    setViewingProject(null);
  };

  const handleProjectUpdated = (updatedProject) => {
    setAllProjects(prevProjects => 
      prevProjects.map(project => 
        project.id === updatedProject.id ? updatedProject : project
      )
    );
    setEditingProject(null);
    fetchProjectStatistics(); // Refresh statistics
  };

  const handleEditProject = (project) => {
    setEditingProject(project);
  };

  const handleCancelEdit = () => {
    setEditingProject(null);
  };

  const handleDeleteProject = async (projectId) => {
    setLoading(true);
    try {
      const response = await api.delete(`/projects/${projectId}`);
      setAllProjects(prevProjects => 
        prevProjects.filter(project => project.id !== projectId)
      );
      fetchProjectStatistics(); // Refresh statistics
    } catch (error) {
      console.error('Error deleting project:', error);
      if (error.response?.status === 409) {
        // Project has associated tasks
        console.error('Project has associated tasks:', error.response.data);
      } else {
        alert('Failed to delete project. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const checkProjectTasks = async (projectId) => {
    try {
      const response = await api.get(`/tasks?projectId=${projectId}`);
      return response.data.length > 0;
    } catch (error) {
      console.error('Error checking project tasks:', error);
      return false;
    }
  };

  const showDeleteModal = async (project) => {
    const hasTasks = await checkProjectTasks(project.id);
    setDeleteModal({ show: true, project, hasTasks });
  };

  const hideDeleteModal = () => {
    setDeleteModal({ show: false, project: null, hasTasks: false });
  };

  const confirmDelete = async () => {
    if (deleteModal.project) {
      await handleDeleteProject(deleteModal.project.id);
      hideDeleteModal();
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const handleSectionChange = (section) => {
    setActiveSection(section);
  };

  const renderContent = () => {
    switch (activeSection) {
      case 'project-creation':
        return <ProjectCreation onProjectCreated={handleProjectCreated} />;
      case 'user-creation':
        return <UserCreation />;
      case 'task-management':
        return <TaskManagement />;
      case 'progress-tracking':
        return <ProgressTracking />;
      case 'team-collaboration':
        return <TeamCollaboration />;
      case 'time-tracking':
        return <TimeTracking />;
      default:
        return (
          <div className="content-section">
            <h2>Welcome to Your Dashboard</h2>
            <p>Hello, {user.firstName || user.username}! Here's an overview of your project management workspace.</p>
            
            <div className="welcome-stats">
              <div className="stat-card">
                <h3>Active Projects</h3>
                <p className="stat-number">{projectStats.activeProjects}</p>
              </div>
              <div className="stat-card">
                <h3>Pending Tasks</h3>
                <p className="stat-number">{projectStats.pendingTasks}</p>
              </div>
              <div className="stat-card">
                <h3>Team Members</h3>
                <p className="stat-number">{projectStats.teamMembers}</p>
              </div>
            </div>


            {allProjects.length > 0 && (
              <div className="projects-section">
                <h3>Your Projects ({allProjects.length})</h3>
                <div className="projects-grid">
                  {allProjects.map(project => (
                    <div key={project.id} className="project-card">
                      <div className="project-header">
                        <h4>{project.name}</h4>
                        <span className={`priority-badge priority-${project.priority.toLowerCase()}`}>
                          {project.priority}
                        </span>
                      </div>
                      <p className="project-description">
                        {project.description || 'No description available'}
                      </p>
                      <div className="project-meta">
                        <span className="project-template">
                          Template: {project.template.replace('_', ' ')}
                        </span>
                        <span className="project-status">
                          Status: {project.status}
                        </span>
                      </div>
                      <div className="project-dates">
                        {project.startDate && (
                          <span>Start: {new Date(project.startDate).toLocaleDateString()}</span>
                        )}
                        {project.endDate && (
                          <span>End: {new Date(project.endDate).toLocaleDateString()}</span>
                        )}
                      </div>
                      <div className="project-actions">
                        <button 
                          className="action-btn-view"
                          onClick={() => handleViewProject(project)}
                          disabled={loading}
                        >
                          👁️ View
                        </button>
                        <button 
                          className="action-btn-update"
                          onClick={() => handleEditProject(project)}
                          disabled={loading}
                        >
                          🔄 Update
                        </button>
                        <button 
                          className="action-btn-delete"
                          onClick={() => showDeleteModal(project)}
                          disabled={loading}
                        >
                          🗑️ Delete
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {allProjects.length === 0 && (
              <div className="no-projects-section">
                <h3>No Projects Yet</h3>
                <p>Get started by creating your first project!</p>
                <button 
                  className="action-btn"
                  onClick={() => handleSectionChange('project-creation')}
                >
                  Create Your First Project
                </button>
              </div>
            )}
          </div>
        );
    }
  };

  return (
    <div className="dashboard-container">
      {/* Top Bar */}
      <div className="top-bar">
        <div className="top-bar-left">
          <div className="dashboard-logo">
            <span className="logo-icon">📊</span>
            <span className="logo-text">Project Management</span>
          </div>
        </div>
        <div className="top-bar-right">
          <div className="user-info">
            <span className="user-name">{user.firstName || user.username}</span>
            <span className="user-email">{user.email}</span>
          </div>
          <button className="logout-btn" onClick={handleLogout}>
            <span className="logout-icon">🚪</span>
            <span className="logout-text">Logout</span>
          </button>
        </div>
      </div>

      <div className="dashboard-content">
        {/* Sidebar Navigation */}
        <div className="sidebar">
          <nav className="sidebar-nav">
            <ul className="nav-list">
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'dashboard' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('dashboard')}
                >
                  <span className="nav-icon">🏠</span>
                  <span className="nav-text">Dashboard</span>
                </button>
              </li>
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'user-creation' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('user-creation')}
                >
                  <span className="nav-icon">👤</span>
                  <span className="nav-text">User Creation</span>
                </button>
              </li>
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'project-creation' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('project-creation')}
                >
                  <span className="nav-icon">📋</span>
                  <span className="nav-text">Project Creation</span>
                </button>
              </li>
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'task-management' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('task-management')}
                >
                  <span className="nav-icon">✅</span>
                  <span className="nav-text">Task Management</span>
                </button>
              </li>
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'progress-tracking' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('progress-tracking')}
                >
                  <span className="nav-icon">📈</span>
                  <span className="nav-text">Progress Tracking</span>
                </button>
              </li>
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'team-collaboration' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('team-collaboration')}
                >
                  <span className="nav-icon">👥</span>
                  <span className="nav-text">Team Collaboration</span>
                </button>
              </li>
              <li className="nav-item">
                <button 
                  className={`nav-link ${activeSection === 'time-tracking' ? 'active' : ''}`}
                  onClick={() => handleSectionChange('time-tracking')}
                >
                  <span className="nav-icon">⏱️</span>
                  <span className="nav-text">Time Tracking</span>
                </button>
              </li>
            </ul>
          </nav>
        </div>

        {/* Main Content Area */}
        <div className="main-content">
          {renderContent()}
        </div>
      </div>

      {/* Project View Modal */}
      {viewingProject && (
        <ProjectView
          project={viewingProject}
          onClose={handleCloseView}
        />
      )}

      {/* Project Edit Modal */}
      {editingProject && (
        <ProjectEdit
          project={editingProject}
          onProjectUpdated={handleProjectUpdated}
          onCancel={handleCancelEdit}
        />
      )}

      {/* Delete Confirmation Modal */}
      {deleteModal.show && deleteModal.project && (
        <div className="modal-overlay">
          <div className="delete-modal">
            <div className="delete-modal-header">
              <h3>Delete Project</h3>
              <button 
                className="modal-close-btn"
                onClick={hideDeleteModal}
                disabled={loading}
              >
                ✕
              </button>
            </div>
            <div className="delete-modal-content">
              <div className="warning-icon">⚠️</div>
              <div className="modal-text">
                {deleteModal.hasTasks ? (
                  <>
                    <p>
                      The project <strong>"{deleteModal.project.name}"</strong> has associated tasks.
                    </p>
                    <p className="delete-warning">
                      <strong>Warning:</strong> Deleting this project will also remove all associated tasks, comments, and attachments. This action cannot be undone.
                    </p>
                    <p>
                      Are you sure you want to proceed with the deletion?
                    </p>
                  </>
                ) : (
                  <>
                    <p>
                      Are you sure you want to delete the project <strong>"{deleteModal.project.name}"</strong>?
                    </p>
                    <p className="delete-warning">
                      This action cannot be undone. All project data will be permanently removed.
                    </p>
                  </>
                )}
              </div>
            </div>
            <div className="delete-modal-actions">
              <button 
                className="delete-modal-btn cancel-btn"
                onClick={hideDeleteModal}
                disabled={loading}
              >
                Cancel
              </button>
              <button 
                className={`delete-modal-btn confirm-btn ${deleteModal.hasTasks ? 'danger' : ''}`}
                onClick={confirmDelete}
                disabled={loading}
              >
                {loading ? 'Deleting...' : (deleteModal.hasTasks ? 'Delete Project & Tasks' : 'Delete Project')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard; 