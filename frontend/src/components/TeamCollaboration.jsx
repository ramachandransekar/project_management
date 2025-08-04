import React, { useState, useEffect } from 'react';
import './TeamCollaboration.css';

const TeamCollaboration = ({ projectId }) => {
  const [activeTab, setActiveTab] = useState('members');
  const [members, setMembers] = useState([]);
  const [activities, setActivities] = useState([]);
  const [projectNote, setProjectNote] = useState('');
  const [newMember, setNewMember] = useState({ userId: '', role: 'MEMBER' });
  const [availableUsers, setAvailableUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const token = localStorage.getItem('token');

  useEffect(() => {
    if (projectId) {
      loadProjectMembers();
      loadProjectActivity();
      loadProjectNote();
      loadAvailableUsers();
    }
  }, [projectId]);

  const loadProjectMembers = async () => {
    try {
      setLoading(true);
      console.log('Loading project members for project:', projectId);
      const response = await fetch(`http://project-management-1-kkb0.onrender.com/api/team/project/${projectId}/members`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Response status:', response.status);
      
      if (response.ok) {
        const data = await response.json();
        console.log('Project members data:', data);
        setMembers(data);
      } else {
        const errorText = await response.text();
        console.error('Failed to load project members:', errorText);
        setError('Failed to load project members');
      }
    } catch (error) {
      console.error('Error loading project members:', error);
      setError('Error loading project members');
    } finally {
      setLoading(false);
    }
  };

  const loadProjectActivity = async () => {
    try {
      const response = await fetch(`http://project-management-1-kkb0.onrender.com/api/team/project/${projectId}/activity/recent?limit=20`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setActivities(data);
      }
    } catch (error) {
      console.error('Error loading project activity:', error);
    }
  };

  const loadProjectNote = async () => {
    try {
      const response = await fetch(`http://project-management-1-kkb0.onrender.com/api/team/project/${projectId}/note`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setProjectNote(data.content || '');
      }
    } catch (error) {
      console.error('Error loading project note:', error);
    }
  };

  const loadAvailableUsers = async () => {
    try {
      console.log('Loading available users');
      const response = await fetch('http://project-management-1-kkb0.onrender.com/api/auth/users', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Available users response status:', response.status);
      
      if (response.ok) {
        const data = await response.json();
        console.log('Available users data:', data);
        setAvailableUsers(data);
      } else {
        const errorText = await response.text();
        console.error('Failed to load available users:', errorText);
      }
    } catch (error) {
      console.error('Error loading available users:', error);
    }
  };

  const addMemberToProject = async () => {
    if (!newMember.userId) {
      setError('Please select a user');
      return;
    }

    try {
      setLoading(true);
      const response = await fetch(`http://project-management-1-kkb0.onrender.com/api/team/project/${projectId}/add-member`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newMember)
      });
      
      if (response.ok) {
        setNewMember({ userId: '', role: 'MEMBER' });
        loadProjectMembers();
        loadProjectActivity();
        setError('');
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Failed to add member');
      }
    } catch (error) {
      setError('Error adding member to project');
    } finally {
      setLoading(false);
    }
  };

  const removeMemberFromProject = async (userId) => {
    if (!window.confirm('Are you sure you want to remove this member from the project?')) {
      return;
    }

    try {
      const response = await fetch(`http://project-management-1-kkb0.onrender.com/api/team/project/${projectId}/remove-member/${userId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (response.ok) {
        loadProjectMembers();
        loadProjectActivity();
      } else {
        setError('Failed to remove member');
      }
    } catch (error) {
      setError('Error removing member from project');
    }
  };

  const saveProjectNote = async () => {
    try {
      const response = await fetch(`http://project-management-1-kkb0.onrender.com/api/team/project/${projectId}/note`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ content: projectNote })
      });
      
      if (response.ok) {
        loadProjectActivity();
      } else {
        setError('Failed to save project note');
      }
    } catch (error) {
      setError('Error saving project note');
    }
  };

  const formatDateTime = (dateTimeString) => {
    return new Date(dateTimeString).toLocaleString();
  };

  const getActivityIcon = (activityType) => {
    switch (activityType) {
      case 'MEMBER_ADDED':
        return '👥';
      case 'MEMBER_REMOVED':
        return '🚪';
      case 'TASK_CREATED':
        return '📝';
      case 'TASK_UPDATED':
        return '✏️';
      case 'TASK_COMPLETED':
        return '✅';
      case 'COMMENT_ADDED':
        return '💬';
      case 'NOTE_UPDATED':
        return '📝';
      default:
        return '📋';
    }
  };

  return (
    <div className="team-collaboration">
      <div className="team-collaboration-header">
        <h2>Team Collaboration</h2>
        <div className="tab-navigation">
          <button 
            className={`tab-button ${activeTab === 'members' ? 'active' : ''}`}
            onClick={() => setActiveTab('members')}
          >
            Team Members
          </button>
          <button 
            className={`tab-button ${activeTab === 'activity' ? 'active' : ''}`}
            onClick={() => setActiveTab('activity')}
          >
            Recent Activity
          </button>
          <button 
            className={`tab-button ${activeTab === 'notes' ? 'active' : ''}`}
            onClick={() => setActiveTab('notes')}
          >
            Project Notes
          </button>
        </div>
      </div>

      {error && (
        <div className="error-message">
          {error}
          <button onClick={() => setError('')}>✕</button>
        </div>
      )}

      <div className="tab-content">
        {/* Team Members Tab */}
        {activeTab === 'members' && (
          <div className="members-section">
            <div className="add-member-section">
              <h3>Add Team Member</h3>
              <div className="add-member-form">
                <select
                  value={newMember.userId}
                  onChange={(e) => setNewMember({ ...newMember, userId: e.target.value })}
                  className="member-select"
                >
                  <option value="">Select a user...</option>
                  {availableUsers.map(user => (
                    <option key={user.id} value={user.id}>
                      {user.fullName || user.username} ({user.email})
                    </option>
                  ))}
                </select>
                <select
                  value={newMember.role}
                  onChange={(e) => setNewMember({ ...newMember, role: e.target.value })}
                  className="role-select"
                >
                  <option value="MEMBER">Member</option>
                  <option value="ADMIN">Admin</option>
                  <option value="VIEWER">Viewer</option>
                </select>
                <button 
                  onClick={addMemberToProject}
                  disabled={loading || !newMember.userId}
                  className="add-member-btn"
                >
                  {loading ? 'Adding...' : 'Add Member'}
                </button>
              </div>
            </div>

            <div className="members-list">
              <h3>Current Team Members</h3>
              {loading ? (
                <div className="loading">Loading members...</div>
              ) : members.length === 0 ? (
                <div className="no-members">
                  No team members found
                  <div style={{fontSize: '12px', color: '#666', marginTop: '10px'}}>
                    Debug: members array length = {members.length}
                  </div>
                </div>
              ) : (
                <div className="members-grid">
                  {members.map(member => {
                    console.log('Rendering member:', member);
                    return (
                      <div key={member.id} className="member-card">
                        <div className="member-info">
                          <div className="member-name">{member.fullName || member.username}</div>
                          <div className="member-email">{member.email}</div>
                          <div className="member-role">{member.role}</div>
                          <div className="member-joined">
                            Joined: {formatDateTime(member.joinedAt)}
                          </div>
                        </div>
                        <button
                          onClick={() => removeMemberFromProject(member.userId)}
                          className="remove-member-btn"
                          title="Remove member"
                        >
                          ✕
                        </button>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        )}

        {/* Activity Tab */}
        {activeTab === 'activity' && (
          <div className="activity-section">
            <h3>Recent Activity</h3>
            {activities.length === 0 ? (
              <div className="no-activity">No recent activity</div>
            ) : (
              <div className="activity-list">
                {activities.map(activity => (
                  <div key={activity.id} className="activity-item">
                    <div className="activity-icon">
                      {getActivityIcon(activity.activityType)}
                    </div>
                    <div className="activity-content">
                      <div className="activity-description">{activity.description}</div>
                      <div className="activity-meta">
                        <span className="activity-user">{activity.fullName || activity.username}</span>
                        <span className="activity-time">{formatDateTime(activity.createdAt)}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Notes Tab */}
        {activeTab === 'notes' && (
          <div className="notes-section">
            <h3>Project Notes</h3>
            <div className="notes-editor">
              <textarea
                value={projectNote}
                onChange={(e) => setProjectNote(e.target.value)}
                placeholder="Add shared notes for the project team..."
                className="notes-textarea"
                rows="15"
              />
              <div className="notes-actions">
                <button 
                  onClick={saveProjectNote}
                  className="save-notes-btn"
                  disabled={loading}
                >
                  {loading ? 'Saving...' : 'Save Notes'}
                </button>
              </div>
            </div>
            <div className="notes-info">
              <p>💡 This is a shared notepad for the entire project team. All team members can view and edit these notes.</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TeamCollaboration; 