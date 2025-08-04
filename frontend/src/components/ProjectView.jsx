import React, { useState } from 'react';
import TeamCollaboration from './TeamCollaboration';
import './ProjectView.css';

const ProjectView = ({ project, onClose }) => {
  const [activeTab, setActiveTab] = useState('details');
  const formatDate = (dateString) => {
    if (!dateString) return 'Not set';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatPriority = (priority) => {
    const priorityMap = {
      'LOW': 'Low',
      'MEDIUM': 'Medium',
      'HIGH': 'High',
      'URGENT': 'Urgent'
    };
    return priorityMap[priority] || priority;
  };

  const formatStatus = (status) => {
    const statusMap = {
      'ACTIVE': 'Active',
      'COMPLETED': 'Completed',
      'ON_HOLD': 'On Hold',
      'CANCELLED': 'Cancelled'
    };
    return statusMap[status] || status;
  };

  const formatTemplate = (template) => {
    return template.replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase());
  };

  const getPriorityColor = (priority) => {
    const colorMap = {
      'LOW': '#28a745',
      'MEDIUM': '#ffc107',
      'HIGH': '#fd7e14',
      'URGENT': '#dc3545'
    };
    return colorMap[priority] || '#6c757d';
  };

  const getStatusColor = (status) => {
    const colorMap = {
      'ACTIVE': '#007bff',
      'COMPLETED': '#28a745',
      'ON_HOLD': '#ffc107',
      'CANCELLED': '#dc3545'
    };
    return colorMap[status] || '#6c757d';
  };

  return (
    <div className="project-view-overlay">
      <div className="project-view-modal">
        <div className="modal-header">
          <h2>Project Details</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="project-tabs">
          <button 
            className={`tab-button ${activeTab === 'details' ? 'active' : ''}`}
            onClick={() => setActiveTab('details')}
          >
            Project Details
          </button>
          <button 
            className={`tab-button ${activeTab === 'team' ? 'active' : ''}`}
            onClick={() => setActiveTab('team')}
          >
            Team Collaboration
          </button>
        </div>

        <div className="project-view-content">
          {activeTab === 'details' && (
            <>
              <div className="project-header-section">
                <h3 className="project-name">{project.name}</h3>
                <div className="project-badges">
                  <span 
                    className="priority-badge"
                    style={{ backgroundColor: getPriorityColor(project.priority) }}
                  >
                    {formatPriority(project.priority)}
                  </span>
                  <span 
                    className="status-badge"
                    style={{ backgroundColor: getStatusColor(project.status) }}
                  >
                    {formatStatus(project.status)}
                  </span>
                </div>
              </div>

              <div className="project-description-section">
                <h4>Description</h4>
                <p className="project-description">
                  {project.description || 'No description available'}
                </p>
              </div>

              <div className="project-details-grid">
                <div className="detail-item">
                  <label>Template</label>
                  <span>{formatTemplate(project.template)}</span>
                </div>

                <div className="detail-item">
                  <label>Start Date</label>
                  <span>{formatDate(project.startDate)}</span>
                </div>

                <div className="detail-item">
                  <label>End Date</label>
                  <span>{formatDate(project.endDate)}</span>
                </div>

                <div className="detail-item">
                  <label>Created</label>
                  <span>{formatDate(project.createdAt)}</span>
                </div>

                <div className="detail-item">
                  <label>Last Updated</label>
                  <span>{formatDate(project.updatedAt)}</span>
                </div>

                <div className="detail-item">
                  <label>Project ID</label>
                  <span className="project-id">{project.id}</span>
                </div>
              </div>

              <div className="project-meta-section">
                <h4>Project Information</h4>
                <div className="meta-grid">
                  <div className="meta-item">
                    <span className="meta-label">Priority:</span>
                    <span className="meta-value">{formatPriority(project.priority)}</span>
                  </div>
                  <div className="meta-item">
                    <span className="meta-label">Status:</span>
                    <span className="meta-value">{formatStatus(project.status)}</span>
                  </div>
                  <div className="meta-item">
                    <span className="meta-label">Template:</span>
                    <span className="meta-value">{formatTemplate(project.template)}</span>
                  </div>
                </div>
              </div>
            </>
          )}

          {activeTab === 'team' && (
            <TeamCollaboration projectId={project.id} />
          )}
        </div>

        <div className="modal-actions">
          <button
            type="button"
            onClick={onClose}
            className="btn-close"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProjectView; 