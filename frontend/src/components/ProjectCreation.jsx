import React, { useState, useEffect } from 'react';
import './ProjectCreation.css';

const ProjectCreation = ({ onProjectCreated }) => {
  const [projectData, setProjectData] = useState({
    name: '',
    description: '',
    startDate: '',
    endDate: '',
    priority: 'medium',
    template: 'none'
  });

  const [isCreating, setIsCreating] = useState(false);
  const [projectTemplates, setProjectTemplates] = useState([]);
  const [error, setError] = useState('');
  const [notification, setNotification] = useState({ show: false, message: '', type: 'success' });

  // Fetch project templates on component mount
  useEffect(() => {
    fetchProjectTemplates();
  }, []);

  const fetchProjectTemplates = async () => {
    try {
      const response = await fetch('https://project-management-1-kkb0.onrender.com/api/projects/templates');
      if (response.ok) {
        const templates = await response.json();
        setProjectTemplates(templates);
      } else {
        console.error('Failed to fetch templates');
      }
    } catch (error) {
      console.error('Error fetching templates:', error);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setProjectData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const showNotification = (message, type = 'success') => {
    setNotification({ show: true, message, type });
    
    // Auto-hide notification after 4 seconds
    setTimeout(() => {
      setNotification({ show: false, message: '', type: 'success' });
    }, 4000);
  };

  const hideNotification = () => {
    setNotification({ show: false, message: '', type: 'success' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsCreating(true);
    setError('');
    
    try {
      // Get JWT token from localStorage
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('Authentication required');
      }

      const response = await fetch('https://project-management-1-kkb0.onrender.com/api/projects', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(projectData)
      });

      if (response.ok) {
        const createdProject = await response.json();
        console.log('Project created:', createdProject);
        
        // Call the callback with the newly created project
        if (onProjectCreated) {
          onProjectCreated(createdProject);
        }
        
        // Reset form
        setProjectData({
          name: '',
          description: '',
          startDate: '',
          endDate: '',
          priority: 'medium',
          template: 'none'
        });
        
        // Show success notification
        showNotification(`Project "${createdProject.name}" created successfully!`);
      } else {
        const errorData = await response.json();
        if (errorData.errors) {
          const errorMessages = Object.values(errorData.errors).join(', ');
          setError(errorMessages);
        } else {
          setError(errorData.message || 'Failed to create project');
        }
      }
    } catch (error) {
      console.error('Error creating project:', error);
      setError(error.message || 'An error occurred while creating the project');
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className="project-creation-container">
      <div className="project-creation-header">
        <h2>Create New Project</h2>
        <p>Set up a new project with all the essential details and configurations.</p>
      </div>

      <div className="project-creation-content">
        <div className="project-form-section">
          {error && (
            <div className="error-message">
              {error}
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="project-form">
            <div className="form-group">
              <label htmlFor="name">Project Name *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={projectData.name}
                onChange={handleInputChange}
                placeholder="Enter project name"
                required
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Project Description</label>
              <textarea
                id="description"
                name="description"
                value={projectData.description}
                onChange={handleInputChange}
                placeholder="Describe your project goals and objectives"
                rows="4"
                className="form-textarea"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="startDate">Start Date</label>
                <input
                  type="date"
                  id="startDate"
                  name="startDate"
                  value={projectData.startDate}
                  onChange={handleInputChange}
                  className="form-input"
                />
              </div>

              <div className="form-group">
                <label htmlFor="endDate">End Date</label>
                <input
                  type="date"
                  id="endDate"
                  name="endDate"
                  value={projectData.endDate}
                  onChange={handleInputChange}
                  className="form-input"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="priority">Priority Level</label>
                <select
                  id="priority"
                  name="priority"
                  value={projectData.priority}
                  onChange={handleInputChange}
                  className="form-select"
                >
                  <option value="low">Low</option>
                  <option value="medium">Medium</option>
                  <option value="high">High</option>
                  <option value="urgent">Urgent</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="template">Project Template</label>
                <select
                  id="template"
                  name="template"
                  value={projectData.template}
                  onChange={handleInputChange}
                  className="form-select"
                >
                  {projectTemplates.map(template => (
                    <option key={template.id} value={template.id}>
                      {template.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-actions">
              <button
                type="submit"
                className="create-project-btn"
                disabled={isCreating || !projectData.name}
              >
                {isCreating ? (
                  <>
                    <span className="loading-spinner"></span>
                    Creating Project...
                  </>
                ) : (
                  'Create Project'
                )}
              </button>
            </div>
          </form>
        </div>

        <div className="project-templates-section">
          <h3>Project Templates</h3>
          <p>Choose from our pre-built templates to get started quickly.</p>
          
          <div className="templates-grid">
            {projectTemplates.slice(1).map(template => (
              <div key={template.id} className="template-card">
                <div className="template-icon">
                  {template.icon}
                </div>
                <h4>{template.name}</h4>
                <p>{template.description}</p>
                <button 
                  className="use-template-btn"
                  onClick={() => setProjectData(prev => ({ ...prev, template: template.id }))}
                >
                  Use Template
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Custom Notification Toast */}
      {notification.show && (
        <div className={`notification-toast ${notification.type}`}>
          <div className="notification-content">
            <div className="notification-icon">
              {notification.type === 'success' ? '✅' : '❌'}
            </div>
            <div className="notification-message">
              {notification.message}
            </div>
            <button 
              className="notification-close"
              onClick={hideNotification}
            >
              ✕
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectCreation; 