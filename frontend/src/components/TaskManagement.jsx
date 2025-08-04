import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './TaskManagement.css';

const TaskManagement = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [newTask, setNewTask] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
    status: 'TODO',
    dueDate: '',
    projectId: '',
    assignee: ''
  });
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [filterPriority, setFilterPriority] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [showTaskDetail, setShowTaskDetail] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [commentLoading, setCommentLoading] = useState(false);
  const [showCommentForm, setShowCommentForm] = useState(false);
  
  // Drag and drop state
  const [draggedTask, setDraggedTask] = useState(null);
  const [dragOverStatus, setDragOverStatus] = useState(null);

  // Task statuses for Kanban board
  const statuses = ['TODO', 'IN_PROGRESS', 'REVIEW', 'DONE'];
  const priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  useEffect(() => {
    fetchProjects();
    fetchUsers();
    fetchTasks();
  }, []);

  const fetchProjects = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await fetch('http://localhost:8080/api/projects', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const projectsData = await response.json();
        setProjects(projectsData);
      }
    } catch (error) {
      console.error('Error fetching projects:', error);
    }
  };

  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await fetch('http://localhost:8080/api/auth/users/list', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const usersData = await response.json();
        setUsers(usersData);
      }
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const fetchTasks = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await fetch('http://localhost:8080/api/tasks', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const tasksData = await response.json();
        setTasks(tasksData);
      }
    } catch (error) {
      console.error('Error fetching tasks:', error);
    }
  };

  // New function to fetch task with fresh comments and attachments
  const fetchTaskWithDetails = async (taskId) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return null;

      const response = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const taskData = await response.json();
        return taskData;
      }
    } catch (error) {
      console.error('Error fetching task details:', error);
    }
    return null;
  };

  const handleCreateTask = async (e) => {
    e.preventDefault();
    
    if (!newTask.title.trim()) {
      alert('Task title is required');
      return;
    }

    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const isUpdate = newTask.id; // Check if we're updating an existing task
      const url = isUpdate 
        ? `http://localhost:8080/api/tasks/${newTask.id}`
        : 'http://localhost:8080/api/tasks';
      
      const method = isUpdate ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          title: newTask.title,
          description: newTask.description,
          status: newTask.status,
          priority: newTask.priority,
          dueDate: newTask.dueDate,
          projectId: newTask.projectId || null,
          assigneeId: newTask.assigneeId || null
        })
      });

      if (response.ok) {
        const taskData = await response.json();
        
        if (isUpdate) {
          // Update existing task in the list
          setTasks(prevTasks => 
            prevTasks.map(t => t.id === newTask.id ? taskData : t)
          );
        } else {
          // Add new task to the list
          setTasks(prevTasks => [...prevTasks, taskData]);
        }
        
        // Reset form
        setNewTask({
          title: '',
          description: '',
          priority: 'MEDIUM',
          status: 'TODO',
          dueDate: '',
          projectId: '',
          assigneeId: null
        });
        setShowCreateForm(false);
      } else {
        alert(`Failed to ${isUpdate ? 'update' : 'create'} task. Please try again.`);
      }
    } catch (error) {
      console.error(`Error ${isUpdate ? 'updating' : 'creating'} task:`, error);
      alert(`Failed to ${isUpdate ? 'update' : 'create'} task. Please try again.`);
    }
  };

  const handleUpdateTaskStatus = async (taskId, newStatus) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const task = tasks.find(t => t.id === taskId);
      if (!task) return;

      const response = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          title: task.title,
          description: task.description,
          status: newStatus,
          priority: task.priority,
          dueDate: task.dueDate,
          assigneeId: task.assigneeId || null
        })
      });

      if (response.ok) {
        const updatedTask = await response.json();
        setTasks(prevTasks => 
          prevTasks.map(t => t.id === taskId ? updatedTask : t)
        );
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const handleDeleteTask = async (taskId) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        const token = localStorage.getItem('token');
        if (!token) return;

        const response = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (response.ok) {
          setTasks(prevTasks => prevTasks.filter(task => task.id !== taskId));
        } else {
          alert('Failed to delete task. Please try again.');
        }
      } catch (error) {
        console.error('Error deleting task:', error);
        alert('Failed to delete task. Please try again.');
      }
    }
  };

  const handleEditTask = (task) => {
    setNewTask({
      ...task,
      assigneeId: task.assigneeId || null
    });
    setShowCreateForm(true);
  };

  const handleTaskClick = async (task) => {
    // Fetch fresh task data with comments and attachments
    const freshTaskData = await fetchTaskWithDetails(task.id);
    if (freshTaskData) {
      setSelectedTask(freshTaskData);
      setShowTaskDetail(true);
    } else {
      // Fallback to existing task data if fetch fails
      setSelectedTask(task);
      setShowTaskDetail(true);
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim() || !selectedTask) return;

    setCommentLoading(true);
    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await fetch(`http://localhost:8080/api/team/tasks/${selectedTask.id}/comment`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          content: newComment
        })
      });

      if (response.ok) {
        const comment = await response.json();
        setSelectedTask(prev => ({
          ...prev,
          comments: [comment, ...(prev.comments || [])]
        }));
        setNewComment('');
        setShowCommentForm(false); // Hide the form after successful comment
      } else {
        alert('Failed to add comment. Please try again.');
      }
    } catch (error) {
      console.error('Error adding comment:', error);
      alert('Failed to add comment. Please try again.');
    } finally {
      setCommentLoading(false);
    }
  };

  const handleCancelComment = () => {
    setNewComment('');
    setShowCommentForm(false);
  };

  const handleAddCommentClick = () => {
    if (newComment.trim()) {
      handleAddComment();
    } else {
      setShowCommentForm(true);
    }
  };

  const handleFileUpload = async () => {
    if (!selectedFile || !selectedTask) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const formData = new FormData();
      formData.append('file', selectedFile);

      const response = await fetch(`http://localhost:8080/api/tasks/${selectedTask.id}/attachments`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (response.ok) {
        const attachment = await response.json();
        setSelectedTask(prev => ({
          ...prev,
          attachments: [attachment, ...(prev.attachments || [])]
        }));
        setSelectedFile(null);
      }
    } catch (error) {
      console.error('Error uploading file:', error);
    }
  };

  const handleStatusChange = async (newStatus) => {
    if (!selectedTask) return;

    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await fetch(`http://localhost:8080/api/tasks/${selectedTask.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          title: selectedTask.title,
          description: selectedTask.description,
          status: newStatus,
          priority: selectedTask.priority,
          dueDate: selectedTask.dueDate,
          assigneeId: selectedTask.assigneeId || null
        })
      });

      if (response.ok) {
        const updatedTask = await response.json();
        setSelectedTask(updatedTask);
        setTasks(prevTasks => 
          prevTasks.map(t => t.id === selectedTask.id ? updatedTask : t)
        );
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const filteredTasks = tasks.filter(task => {
    const matchesStatus = filterStatus === 'ALL' || task.status === filterStatus;
    const matchesPriority = filterPriority === 'ALL' || task.priority === filterPriority;
    const matchesSearch = task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         task.description.toLowerCase().includes(searchTerm.toLowerCase());
    
    return matchesStatus && matchesPriority && matchesSearch;
  });

  const getTasksByStatus = (status) => {
    return filteredTasks.filter(task => task.status === status);
  };

  const getPriorityColor = (priority) => {
    const colors = {
      'LOW': '#28a745',
      'MEDIUM': '#ffc107',
      'HIGH': '#fd7e14',
      'URGENT': '#dc3545'
    };
    return colors[priority] || '#6c757d';
  };

  const getStatusColor = (status) => {
    const colors = {
      'TODO': '#6c757d',
      'IN_PROGRESS': '#007bff',
      'REVIEW': '#ffc107',
      'DONE': '#28a745'
    };
    return colors[status] || '#6c757d';
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString();
  };

  const isOverdue = (dueDate) => {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  };

  // Drag and Drop Handlers
  const handleDragStart = (e, task) => {
    setDraggedTask(task);
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/html', e.target.outerHTML);
  };

  const handleDragOver = (e, status) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setDragOverStatus(status);
  };

  const handleDragEnter = (e, status) => {
    e.preventDefault();
    setDragOverStatus(status);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setDragOverStatus(null);
  };

  const handleDrop = async (e, targetStatus) => {
    e.preventDefault();
    setDragOverStatus(null);
    
    if (!draggedTask || draggedTask.status === targetStatus) {
      setDraggedTask(null);
      return;
    }

    try {
      const token = localStorage.getItem('token');
      if (!token) return;

      const response = await fetch(`http://localhost:8080/api/tasks/${draggedTask.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          title: draggedTask.title,
          description: draggedTask.description,
          status: targetStatus,
          priority: draggedTask.priority,
          dueDate: draggedTask.dueDate,
          assigneeId: draggedTask.assigneeId || null
        })
      });

      if (response.ok) {
        const updatedTask = await response.json();
        setTasks(prevTasks => 
          prevTasks.map(t => t.id === draggedTask.id ? updatedTask : t)
        );
      } else {
        console.error('Failed to update task status');
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    } finally {
      setDraggedTask(null);
    }
  };

  const handleDragEnd = () => {
    setDraggedTask(null);
    setDragOverStatus(null);
  };

  const handleRefresh = async () => {
    // Clear all filters
    setFilterStatus('ALL');
    setFilterPriority('ALL');
    setSearchTerm('');
    
    // Reload the task list
    await fetchTasks();
  };

  return (
    <div className="task-management-container">
      <div className="task-management-header">
        <div className="header-top-row">
          <h2>Task Management</h2>
          <div className="task-controls">
            <button 
              className="create-task-btn"
              onClick={() => setShowCreateForm(true)}
            >
              ➕ Create New Task
            </button>
            <button 
              className="refresh-btn"
              onClick={handleRefresh}
              title="Refresh and clear filters"
            >
              🔄 Refresh
            </button>
          </div>
        </div>
        <p>Organize and track tasks across all projects</p>
      </div>

      {/* Filters and Search */}
      <div className="task-filters">
        <div className="filter-group">
          <label>Status:</label>
          <select 
            value={filterStatus} 
            onChange={(e) => setFilterStatus(e.target.value)}
            className="filter-select"
          >
            <option value="ALL">All Statuses</option>
            {statuses.map(status => (
              <option key={status} value={status}>
                {status.replace('_', ' ')}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label>Priority:</label>
          <select 
            value={filterPriority} 
            onChange={(e) => setFilterPriority(e.target.value)}
            className="filter-select"
          >
            <option value="ALL">All Priorities</option>
            {priorities.map(priority => (
              <option key={priority} value={priority}>
                {priority}
              </option>
            ))}
          </select>
        </div>

        <div className="search-group">
          <input
            type="text"
            placeholder="Search tasks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
      </div>

      {/* Task Creation Form */}
      {showCreateForm && (
        <div className="task-form-overlay">
          <div className="task-form-modal">
            <div className="task-form-header">
              <h3>{newTask.id ? 'Edit Task' : 'Create New Task'}</h3>
              <button 
                className="close-btn"
                onClick={() => {
                  setShowCreateForm(false);
                  setNewTask({
                    title: '',
                    description: '',
                    priority: 'MEDIUM',
                    status: 'TODO',
                    dueDate: '',
                    projectId: '',
                    assigneeId: null
                  });
                }}
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreateTask} className="task-form">
              <div className="form-group">
                <label>Task Title *</label>
                <input
                  type="text"
                  value={newTask.title}
                  onChange={(e) => setNewTask({...newTask, title: e.target.value})}
                  placeholder="Enter task title"
                  required
                />
              </div>

              <div className="form-group">
                <label>Description</label>
                <textarea
                  value={newTask.description}
                  onChange={(e) => setNewTask({...newTask, description: e.target.value})}
                  placeholder="Enter task description"
                  rows="3"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Priority</label>
                  <select
                    value={newTask.priority}
                    onChange={(e) => setNewTask({...newTask, priority: e.target.value})}
                  >
                    {priorities.map(priority => (
                      <option key={priority} value={priority}>
                        {priority}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Status</label>
                  <select
                    value={newTask.status}
                    onChange={(e) => setNewTask({...newTask, status: e.target.value})}
                  >
                    {statuses.map(status => (
                      <option key={status} value={status}>
                        {status.replace('_', ' ')}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Due Date</label>
                  <input
                    type="date"
                    value={newTask.dueDate}
                    onChange={(e) => setNewTask({...newTask, dueDate: e.target.value})}
                  />
                </div>

                <div className="form-group">
                  <label>Project</label>
                  <select
                    value={newTask.projectId}
                    onChange={(e) => setNewTask({...newTask, projectId: e.target.value})}
                  >
                    <option value="">Select Project</option>
                    {projects.map(project => (
                      <option key={project.id} value={project.id}>
                        {project.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Assignee</label>
                  <select
                    value={newTask.assigneeId || ''}
                    onChange={(e) => setNewTask({...newTask, assigneeId: e.target.value ? parseInt(e.target.value) : null})}
                  >
                    <option value="">Select Assignee</option>
                    {users.map(user => (
                      <option key={user.id} value={user.id}>
                        {user.firstName && user.lastName ? `${user.firstName} ${user.lastName}` : user.username}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Assigner</label>
                  <input
                    type="text"
                    value={user.firstName && user.lastName ? `${user.firstName} ${user.lastName}` : user.username}
                    disabled
                    className="disabled-input"
                  />
                </div>
              </div>

              <div className="form-actions">
                <button type="submit" className="submit-btn">
                  {newTask.id ? 'Update Task' : 'Create Task'}
                </button>
                <button 
                  type="button" 
                  className="cancel-btn"
                  onClick={() => {
                    setShowCreateForm(false);
                    setNewTask({
                      title: '',
                      description: '',
                      priority: 'MEDIUM',
                      status: 'TODO',
                      dueDate: '',
                      projectId: '',
                      assignee: ''
                    });
                  }}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Kanban Board */}
      <div className="kanban-board">
        {statuses.map(status => (
          <div 
            key={status} 
            className={`kanban-column ${dragOverStatus === status ? 'drag-over' : ''}`}
            onDragOver={(e) => handleDragOver(e, status)}
            onDragEnter={(e) => handleDragEnter(e, status)}
            onDragLeave={handleDragLeave}
            onDrop={(e) => handleDrop(e, status)}
          >
            <div className="column-header">
              <h3>{status.replace('_', ' ')}</h3>
              <span className="task-count">{getTasksByStatus(status).length}</span>
            </div>
            
            <div className="column-content">
              {getTasksByStatus(status).map(task => (
                <div 
                  key={task.id} 
                  className={`task-card ${draggedTask?.id === task.id ? 'dragging' : ''}`}
                  draggable={true}
                  onDragStart={(e) => handleDragStart(e, task)}
                  onDragEnd={handleDragEnd}
                  onClick={() => handleTaskClick(task)}
                >
                  <div className="task-header">
                    <h4 className="task-title">{task.title}</h4>
                    <div className="task-actions" onClick={(e) => e.stopPropagation()}>
                      <button 
                        className="action-btn edit-btn"
                        onClick={() => handleEditTask(task)}
                        title="Edit Task"
                      >
                        ✏️
                      </button>
                      <button 
                        className="action-btn delete-btn"
                        onClick={() => handleDeleteTask(task.id)}
                        title="Delete Task"
                      >
                        🗑️
                      </button>
                    </div>
                  </div>
                  
                  {task.description && (
                    <p className="task-description">{task.description}</p>
                  )}
                  
                  <div className="task-meta">
                    <span 
                      className="priority-badge"
                      style={{ backgroundColor: getPriorityColor(task.priority) }}
                    >
                      {task.priority}
                    </span>
                    
                    {task.dueDate && (
                      <span className={`due-date ${isOverdue(task.dueDate) ? 'overdue' : ''}`}>
                        📅 {formatDate(task.dueDate)}
                        {isOverdue(task.dueDate) && <span className="overdue-indicator">!</span>}
                      </span>
                    )}
                  </div>
                  
                  {task.assigneeName && (
                    <div className="task-assignee">
                      👤 {task.assigneeName}
                    </div>
                  )}
                  
                  {task.projectName && (
                    <div className="task-project">
                      📁 {task.projectName}
                    </div>
                  )}
                  
                  <div className="task-footer">
                    <small className="task-date">
                      Created: {formatDate(task.createdAt)}
                    </small>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>


      {/* Task Detail Popup */}
      {showTaskDetail && selectedTask && (
        <div className="task-detail-overlay">
          <div className="task-detail-modal">
            <div className="task-detail-header">
              <h3>{selectedTask.title}</h3>
              <button 
                className="close-btn"
                onClick={() => {
                  setShowTaskDetail(false);
                  setSelectedTask(null);
                  setNewComment('');
                  setSelectedFile(null);
                  setShowCommentForm(false);
                }}
              >
                ✕
              </button>
            </div>

            <div className="task-detail-content">
              {/* Task Information */}
              <div className="task-info-section">
                <div className="task-info-row">
                  <div className="task-info-item">
                    <label>Status:</label>
                    <select 
                      value={selectedTask.status}
                      onChange={(e) => handleStatusChange(e.target.value)}
                      className="status-select"
                    >
                      {statuses.map(status => (
                        <option key={status} value={status}>
                          {status.replace('_', ' ')}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="task-info-item">
                    <label>Priority:</label>
                    <span 
                      className="priority-badge"
                      style={{ backgroundColor: getPriorityColor(selectedTask.priority) }}
                    >
                      {selectedTask.priority}
                    </span>
                  </div>
                </div>

                <div className="task-info-row">
                  <div className="task-info-item">
                    <label>Due Date:</label>
                    <span>{selectedTask.dueDate ? formatDate(selectedTask.dueDate) : 'No due date'}</span>
                  </div>
                  <div className="task-info-item">
                    <label>Created:</label>
                    <span>{formatDate(selectedTask.createdAt)}</span>
                  </div>
                </div>

                {selectedTask.description && (
                  <div className="task-description-section">
                    <label>Description:</label>
                    <p>{selectedTask.description}</p>
                  </div>
                )}

                {selectedTask.projectName && (
                  <div className="task-info-item">
                    <label>Project:</label>
                    <span>{selectedTask.projectName}</span>
                  </div>
                )}

                {selectedTask.assigneeName && (
                  <div className="task-info-item">
                    <label>Assignee:</label>
                    <span>{selectedTask.assigneeName}</span>
                  </div>
                )}
              </div>

              {/* Comments Section */}
              <div className="comments-section">
                <h4>Comments</h4>
                
                <div className="comments-list">
                  {selectedTask.comments && selectedTask.comments.length > 0 ? (
                    selectedTask.comments.map(comment => (
                      <div key={comment.id} className="comment-item">
                        <div className="comment-header">
                          <span className="comment-author">{comment.createdByName}</span>
                          <span className="comment-date">{formatDate(comment.createdAt)}</span>
                        </div>
                        <p className="comment-content">{comment.content}</p>
                      </div>
                    ))
                  ) : (
                    <p className="no-comments">No comments yet</p>
                  )}
                </div>

                <div className="add-comment">
                  <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Add a comment..."
                    rows="3"
                    disabled={commentLoading}
                  />
                  <div className="comment-input-actions">
                    <button 
                      onClick={handleAddCommentClick}
                      className="add-comment-toggle-btn"
                      disabled={commentLoading}
                    >
                      {commentLoading ? 'Adding...' : '💬 Add Comment'}
                    </button>
                  </div>
                </div>
              </div>

              {/* Attachments Section */}
              <div className="attachments-section">
                <h4>Attachments</h4>
                <div className="add-attachment">
                  <input
                    type="file"
                    onChange={(e) => setSelectedFile(e.target.files[0])}
                    className="file-input"
                  />
                  <button 
                    onClick={handleFileUpload}
                    disabled={!selectedFile}
                    className="upload-btn"
                  >
                    Upload File
                  </button>
                </div>

                <div className="attachments-list">
                  {selectedTask.attachments && selectedTask.attachments.length > 0 ? (
                    selectedTask.attachments.map(attachment => (
                      <div key={attachment.id} className="attachment-item">
                        <div className="attachment-info">
                          <span className="attachment-name">{attachment.originalFileName}</span>
                          <span className="attachment-size">({Math.round(attachment.fileSize / 1024)} KB)</span>
                        </div>
                        <div className="attachment-meta">
                          <span>Uploaded by {attachment.uploadedByName}</span>
                          <span>{formatDate(attachment.uploadedAt)}</span>
                        </div>
                      </div>
                    ))
                  ) : (
                    <p className="no-attachments">No attachments yet</p>
                  )}
                </div>
              </div>
            </div>

            {/* Dialog Footer with Done and Cancel Buttons */}
            <div className="task-detail-footer">
              <div className="dialog-actions">
                {showCommentForm && (
                  <>
                    <button 
                      onClick={() => {
                        setShowTaskDetail(false);
                        setSelectedTask(null);
                        setNewComment('');
                        setSelectedFile(null);
                        setShowCommentForm(false);
                      }}
                      className="dialog-btn done-btn"
                    >
                      Done
                    </button>
                    <button 
                      onClick={() => {
                        setShowCommentForm(false);
                        setNewComment('');
                      }}
                      disabled={commentLoading}
                      className="dialog-btn cancel-btn"
                    >
                      Cancel
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskManagement; 