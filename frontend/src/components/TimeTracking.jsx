import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import DatePicker from 'react-datepicker';
import { toast, ToastContainer } from 'react-toastify';
import 'react-datepicker/dist/react-datepicker.css';
import 'react-toastify/dist/ReactToastify.css';
import './TimeTracking.css';

const TimeTracking = () => {
  const [activeTab, setActiveTab] = useState('timer');
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [timerTime, setTimerTime] = useState(0);
  const [selectedProject, setSelectedProject] = useState('');
  const [selectedTask, setSelectedTask] = useState('');
  const [selectedPeriod, setSelectedPeriod] = useState('week');
  const [projects, setProjects] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [timeEntries, setTimeEntries] = useState([]);
  const [filteredTimeEntries, setFilteredTimeEntries] = useState([]);
  const [manualEntry, setManualEntry] = useState({
    taskId: '',
    entryDate: new Date(),
    startTime: '',
    endTime: '',
    durationHours: 0,
    durationMinutes: 0,
    description: '',
    isBillable: false
  });
  const [showManualForm, setShowManualForm] = useState(false);
  const [showTodaySummary, setShowTodaySummary] = useState(false);
  const [isOnBreak, setIsOnBreak] = useState(false);
  const [breakStartTime, setBreakStartTime] = useState(null);
  const [selectedDateRange, setSelectedDateRange] = useState({
    startDate: new Date(new Date().setDate(new Date().getDate() - 7)),
    endDate: new Date()
  });
  const [projectSummary, setProjectSummary] = useState(null);
  const [selectedProjectForSummary, setSelectedProjectForSummary] = useState('');
  const [selectedEntriesForSubmission, setSelectedEntriesForSubmission] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const timerRef = useRef(null);
  const breakTimerRef = useRef(null);

  // Fetch projects and tasks on component mount
  useEffect(() => {
    fetchProjects();
    fetchTimeEntries();
  }, []);

  // Fetch tasks when project changes
  useEffect(() => {
    if (selectedProject) {
      fetchTasks(selectedProject);
    } else {
      setTasks([]);
    }
  }, [selectedProject]);

  // Filter time entries when period or entries change
  useEffect(() => {
    filterTimeEntries();
  }, [selectedPeriod, timeEntries]);

  // Fetch project summary when project changes
  useEffect(() => {
    if (selectedProjectForSummary) {
      fetchProjectSummary(selectedProjectForSummary);
    }
  }, [selectedProjectForSummary, selectedDateRange]);

  // Cleanup modal states on component unmount
  useEffect(() => {
    return () => {
      setShowTodaySummary(false);
      setShowManualForm(false);
    };
  }, []);

  const fetchProjects = async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem('token');
      const response = await axios.get('http://project-management-1-kkb0.onrender.com/api/projects', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setProjects(response.data);
    } catch (error) {
      console.error('Error fetching projects:', error);
      toast.error('Failed to fetch projects');
      setError('Failed to fetch projects');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchTasks = async (projectId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`http://project-management-1-kkb0.onrender.com/api/tasks/project/${projectId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTasks(response.data);
    } catch (error) {
      console.error('Error fetching tasks:', error);
      toast.error('Failed to fetch tasks');
    }
  };

  const fetchTimeEntries = async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem('token');
      const response = await axios.get('http://project-management-1-kkb0.onrender.com/api/time-tracking/entries', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTimeEntries(response.data);
    } catch (error) {
      console.error('Error fetching time entries:', error);
      toast.error('Failed to fetch time entries');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchProjectSummary = async (projectId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(
        `http://project-management-1-kkb0.onrender.com/api/time-tracking/projects/${projectId}/summary?startDate=${selectedDateRange.startDate.toISOString().split('T')[0]}&endDate=${selectedDateRange.endDate.toISOString().split('T')[0]}`,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      setProjectSummary(response.data);
    } catch (error) {
      console.error('Error fetching project summary:', error);
      toast.error('Failed to fetch project summary');
    }
  };

  const filterTimeEntries = () => {
    const today = new Date().toISOString().split('T')[0];
    const startOfWeek = new Date();
    startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay());
    const startOfMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
    const startOfQuarter = new Date(new Date().getFullYear(), Math.floor(new Date().getMonth() / 3) * 3, 1);

    let filtered = [...timeEntries];

    switch (selectedPeriod) {
      case 'today':
        filtered = timeEntries.filter(entry => entry.entryDate === today);
        break;
      case 'week':
        filtered = timeEntries.filter(entry => {
          const entryDate = new Date(entry.entryDate);
          return entryDate >= startOfWeek;
        });
        break;
      case 'month':
        filtered = timeEntries.filter(entry => {
          const entryDate = new Date(entry.entryDate);
          return entryDate >= startOfMonth;
        });
        break;
      case 'quarter':
        filtered = timeEntries.filter(entry => {
          const entryDate = new Date(entry.entryDate);
          return entryDate >= startOfQuarter;
        });
        break;
      default:
        filtered = timeEntries;
    }

    setFilteredTimeEntries(filtered);
  };

  const formatTime = (seconds) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const formatDuration = (minutes) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  };

  const startTimer = () => {
    if (selectedProject && selectedTask) {
      setIsTimerRunning(true);
      timerRef.current = setInterval(() => {
        setTimerTime(prev => prev + 1);
      }, 1000);
    } else {
      toast.error('Please select a project and task before starting the timer');
    }
  };

  const stopTimer = () => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
    setIsTimerRunning(false);
    
    // Auto-save the time entry
    if (timerTime > 0) {
      saveTimerEntry();
    }
    
    setTimerTime(0);
  };

  const pauseTimer = () => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
    setIsTimerRunning(false);
  };

  const saveTimerEntry = async () => {
    try {
      const token = localStorage.getItem('token');
      const durationMinutes = Math.floor(timerTime / 60);
      
      const timeEntryData = {
        taskId: selectedTask,
        entryDate: new Date().toISOString().split('T')[0],
        durationMinutes: durationMinutes,
        description: `Timer entry - ${formatTime(timerTime)}`,
        isBillable: false
      };

      await axios.post('http://project-management-1-kkb0.onrender.com/api/time-tracking/entries', timeEntryData, {
        headers: { Authorization: `Bearer ${token}` }
      });

      toast.success('Time entry saved successfully!');
      fetchTimeEntries();
    } catch (error) {
      console.error('Error saving time entry:', error);
      toast.error('Failed to save time entry');
    }
  };

  const saveManualEntry = async () => {
    try {
      // Validation
      if (!manualEntry.taskId) {
        toast.error('Please select a task');
        return;
      }
      if (manualEntry.durationHours === 0 && manualEntry.durationMinutes === 0) {
        toast.error('Please enter duration (hours or minutes)');
        return;
      }

      setIsLoading(true);
      const token = localStorage.getItem('token');
      const durationMinutes = (manualEntry.durationHours * 60) + manualEntry.durationMinutes;
      
      const timeEntryData = {
        taskId: manualEntry.taskId,
        entryDate: manualEntry.entryDate.toISOString().split('T')[0],
        startTime: manualEntry.startTime || null,
        endTime: manualEntry.endTime || null,
        durationMinutes: durationMinutes,
        description: manualEntry.description || '',
        isBillable: manualEntry.isBillable
      };

      await axios.post('http://project-management-1-kkb0.onrender.com/api/time-tracking/entries', timeEntryData, {
        headers: { Authorization: `Bearer ${token}` }
      });

      toast.success('Manual time entry saved successfully!');
      setShowManualForm(false);
      setManualEntry({
        taskId: '',
        entryDate: new Date(),
        startTime: '',
        endTime: '',
        durationHours: 0,
        durationMinutes: 0,
        description: '',
        isBillable: false
      });
      fetchTimeEntries();
    } catch (error) {
      console.error('Error saving manual entry:', error);
      toast.error('Failed to save manual entry');
    } finally {
      setIsLoading(false);
    }
  };

  const handleViewTodaySummary = () => {
    try {
      setShowTodaySummary(true);
      setSelectedPeriod('today');
    } catch (error) {
      console.error('Error opening today summary:', error);
      toast.error('Failed to open today summary');
    }
  };

  const handleStartBreak = () => {
    if (isOnBreak) {
      // End break
      setIsOnBreak(false);
      if (breakTimerRef.current) {
        clearInterval(breakTimerRef.current);
        breakTimerRef.current = null;
      }
      setBreakStartTime(null);
      toast.success('Break ended');
    } else {
      // Start break
      setIsOnBreak(true);
      setBreakStartTime(new Date());
      toast.success('Break started');
    }
  };

  const getTodaySummary = () => {
    try {
      const today = new Date().toISOString().split('T')[0];
      const todayEntries = (timeEntries || []).filter(entry => entry && entry.entryDate === today);
      
      const totalMinutes = todayEntries.reduce((sum, entry) => sum + (entry.durationMinutes || 0), 0);
      const billableMinutes = todayEntries.filter(entry => entry && entry.isBillable).reduce((sum, entry) => sum + (entry.durationMinutes || 0), 0);
      const nonBillableMinutes = totalMinutes - billableMinutes;
      
      return {
        totalHours: (totalMinutes / 60).toFixed(1),
        billableHours: (billableMinutes / 60).toFixed(1),
        nonBillableHours: (nonBillableMinutes / 60).toFixed(1),
        totalEntries: todayEntries.length,
        entries: todayEntries
      };
    } catch (error) {
      console.error('Error in getTodaySummary:', error);
      return {
        totalHours: '0.0',
        billableHours: '0.0',
        nonBillableHours: '0.0',
        totalEntries: 0,
        entries: []
      };
    }
  };

  const submitEntriesForApproval = async () => {
    try {
      setIsLoading(true);
      const token = localStorage.getItem('token');
      await axios.post('http://project-management-1-kkb0.onrender.com/api/time-tracking/entries/submit', selectedEntriesForSubmission, {
        headers: { Authorization: `Bearer ${token}` }
      });

      toast.success('Time entries submitted for approval!');
      setSelectedEntriesForSubmission([]);
      fetchTimeEntries();
    } catch (error) {
      console.error('Error submitting entries:', error);
      toast.error('Failed to submit entries');
    } finally {
      setIsLoading(false);
    }
  };

  const deleteTimeEntry = async (entryId) => {
    try {
      const token = localStorage.getItem('token');
      await axios.delete(`http://project-management-1-kkb0.onrender.com/api/time-tracking/entries/${entryId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      toast.success('Time entry deleted successfully!');
      fetchTimeEntries();
    } catch (error) {
      console.error('Error deleting time entry:', error);
      toast.error('Failed to delete time entry');
    }
  };

  const exportToCSV = () => {
    const csvContent = [
      ['Date', 'Project', 'Task', 'Duration', 'Description', 'Billable', 'Status'],
      ...filteredTimeEntries.map(entry => [
        entry.entryDate,
        entry.projectName,
        entry.taskTitle,
        formatDuration(entry.durationMinutes),
        entry.description || '',
        entry.isBillable ? 'Yes' : 'No',
        entry.status
      ])
    ].map(row => row.join(',')).join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `time-entries-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  const getWeekDays = () => {
    const days = [];
    const startOfWeek = new Date();
    startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay());
    
    for (let i = 0; i < 7; i++) {
      const day = new Date(startOfWeek);
      day.setDate(startOfWeek.getDate() + i);
      days.push(day);
    }
    return days;
  };

  const weekDays = getWeekDays();
  const todaySummary = getTodaySummary();

  return (
    <div className="time-tracking-container">
      <ToastContainer position="top-right" />
      
      <div className="time-tracking-header">
        <h2>Time Tracking</h2>
        <p>Track your time, monitor productivity, and generate detailed reports for better project management.</p>
      </div>

      {/* Tab Navigation */}
      <div className="time-tracking-tabs">
        <button 
          className={`tab-button ${activeTab === 'timer' ? 'active' : ''}`}
          onClick={() => setActiveTab('timer')}
        >
          ⏱️ Time Logger
        </button>
        <button 
          className={`tab-button ${activeTab === 'entries' ? 'active' : ''}`}
          onClick={() => setActiveTab('entries')}
        >
          📝 Time Entries
        </button>
        <button 
          className={`tab-button ${activeTab === 'timesheet' ? 'active' : ''}`}
          onClick={() => setActiveTab('timesheet')}
        >
          📅 Timesheet
        </button>
        <button 
          className={`tab-button ${activeTab === 'summary' ? 'active' : ''}`}
          onClick={() => setActiveTab('summary')}
        >
          📊 Project Summary
        </button>
        <button 
          className={`tab-button ${activeTab === 'reports' ? 'active' : ''}`}
          onClick={() => setActiveTab('reports')}
        >
          📈 Reports
        </button>
      </div>

      {/* Timer Tab */}
      {activeTab === 'timer' && (
        <div className="time-tracking-content">
          <div className="timer-section">
            <div className="timer-container">
              <div className="timer-display">
                <div className="timer-time">{formatTime(timerTime)}</div>
                <div className="timer-status">
                  {isTimerRunning ? '⏸️ Running' : '⏸️ Paused'}
                </div>
              </div>

              <div className="timer-controls">
                {!isTimerRunning ? (
                  <button 
                    className="timer-btn start-btn"
                    onClick={startTimer}
                    disabled={isLoading}
                  >
                    ▶️ Start Timer
                  </button>
                ) : (
                  <div className="timer-controls-group">
                    <button 
                      className="timer-btn pause-btn"
                      onClick={pauseTimer}
                    >
                      ⏸️ Pause
                    </button>
                    <button 
                      className="timer-btn stop-btn"
                      onClick={stopTimer}
                    >
                      ⏹️ Stop
                    </button>
                  </div>
                )}
              </div>

              <div className="timer-settings">
                <div className="setting-group">
                  <label>Project:</label>
                  <select 
                    value={selectedProject} 
                    onChange={(e) => setSelectedProject(e.target.value)}
                    className="setting-select"
                    disabled={isLoading}
                  >
                    <option value="">Select Project</option>
                    {projects.map(project => (
                      <option key={project.id} value={project.id}>{project.name}</option>
                    ))}
                  </select>
                </div>

                <div className="setting-group">
                  <label>Task:</label>
                  <select 
                    value={selectedTask} 
                    onChange={(e) => setSelectedTask(e.target.value)}
                    className="setting-select"
                    disabled={isLoading}
                  >
                    <option value="">Select Task</option>
                    {tasks.map(task => (
                      <option key={task.id} value={task.id}>{task.title}</option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            <div className="quick-actions">
              <h3>Quick Actions</h3>
              <div className="quick-buttons">
                <button 
                  className="quick-btn"
                  onClick={() => setShowManualForm(true)}
                >
                  📝 Add Manual Entry
                </button>
                <button 
                  className="quick-btn"
                  onClick={handleViewTodaySummary}
                >
                  📊 View Today's Summary
                </button>
                <button 
                  className={`quick-btn ${isOnBreak ? 'break-active' : ''}`}
                  onClick={handleStartBreak}
                >
                  {isOnBreak ? '⏸️ End Break' : '⚡ Start Break'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Time Entries Tab */}
      {activeTab === 'entries' && (
        <div className="time-tracking-content">
          <div className="entries-section">
            <div className="entries-header">
              <h3>Time Entries</h3>
              <div className="entries-filters">
                <select 
                  value={selectedPeriod} 
                  onChange={(e) => setSelectedPeriod(e.target.value)}
                  className="filter-select"
                >
                  <option value="today">Today</option>
                  <option value="week">This Week</option>
                  <option value="month">This Month</option>
                  <option value="quarter">This Quarter</option>
                </select>
                <button 
                  className="add-entry-btn"
                  onClick={() => setShowManualForm(true)}
                >
                  ➕ Add Entry
                </button>
              </div>
            </div>

            {error && (
              <div className="error-message">
                {error}
              </div>
            )}

            {isLoading ? (
              <div className="loading-container">
                <div className="loading-spinner"></div>
                <p>Loading time entries...</p>
              </div>
            ) : (
              <>
                <div className="entries-table">
                  <div className="table-header">
                    <div className="header-cell">Date</div>
                    <div className="header-cell">Project</div>
                    <div className="header-cell">Task</div>
                    <div className="header-cell">Duration</div>
                    <div className="header-cell">Time</div>
                    <div className="header-cell">Billable</div>
                    <div className="header-cell">Status</div>
                    <div className="header-cell">Actions</div>
                  </div>
                  {filteredTimeEntries.map(entry => (
                    <div key={entry.id} className="table-row">
                      <div className="table-cell">{entry.entryDate}</div>
                      <div className="table-cell">{entry.projectName}</div>
                      <div className="table-cell">{entry.taskTitle}</div>
                      <div className="table-cell">{formatDuration(entry.durationMinutes)}</div>
                      <div className="table-cell">
                        {entry.startTime && entry.endTime ? `${entry.startTime} - ${entry.endTime}` : 'N/A'}
                      </div>
                      <div className="table-cell">{entry.isBillable ? 'Yes' : 'No'}</div>
                      <div className="table-cell">
                        <span className={`status-badge ${entry.status.toLowerCase()}`}>
                          {entry.status}
                        </span>
                      </div>
                      <div className="table-cell">
                        <div className="entry-actions">
                          <button 
                            className="action-btn"
                            onClick={() => deleteTimeEntry(entry.id)}
                          >
                            🗑️
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="entries-summary">
                  <div className="summary-card">
                    <h4>Total Time</h4>
                    <span className="summary-value">
                      {formatDuration(filteredTimeEntries.reduce((sum, entry) => sum + entry.durationMinutes, 0))}
                    </span>
                  </div>
                  <div className="summary-card">
                    <h4>Billable Hours</h4>
                    <span className="summary-value">
                      {formatDuration(filteredTimeEntries.filter(entry => entry.isBillable).reduce((sum, entry) => sum + entry.durationMinutes, 0))}
                    </span>
                  </div>
                  <div className="summary-card">
                    <h4>Non-Billable Hours</h4>
                    <span className="summary-value">
                      {formatDuration(filteredTimeEntries.filter(entry => !entry.isBillable).reduce((sum, entry) => sum + entry.durationMinutes, 0))}
                    </span>
                  </div>
                  <div className="summary-card">
                    <h4>Entries</h4>
                    <span className="summary-value">{filteredTimeEntries.length}</span>
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* Timesheet Tab */}
      {activeTab === 'timesheet' && (
        <div className="time-tracking-content">
          <div className="timesheet-section">
            <div className="timesheet-header">
              <h3>Weekly Timesheet</h3>
              <button 
                className="submit-btn"
                onClick={submitEntriesForApproval}
                disabled={selectedEntriesForSubmission.length === 0 || isLoading}
              >
                {isLoading ? (
                  <>
                    <span className="loading-spinner"></span>
                    Submitting...
                  </>
                ) : (
                  '📤 Submit for Approval'
                )}
              </button>
            </div>

            <div className="timesheet-table">
              <div className="table-header">
                <div className="header-cell">Task</div>
                {weekDays.map(day => (
                  <div key={day.toISOString()} className="header-cell">
                    {day.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' })}
                  </div>
                ))}
                <div className="header-cell">Total</div>
              </div>
              
              {tasks.map(task => (
                <div key={task.id} className="table-row">
                  <div className="table-cell task-name">{task.title}</div>
                  {weekDays.map(day => {
                    const dayEntries = timeEntries.filter(entry => 
                      entry.taskId === task.id && 
                      entry.entryDate === day.toISOString().split('T')[0]
                    );
                    const totalMinutes = dayEntries.reduce((sum, entry) => sum + entry.durationMinutes, 0);
                    
                    return (
                      <div key={day.toISOString()} className="table-cell">
                        <input
                          type="text"
                          value={totalMinutes > 0 ? formatDuration(totalMinutes) : ''}
                          placeholder="0h 0m"
                          className="time-input"
                          readOnly
                        />
                      </div>
                    );
                  })}
                  <div className="table-cell total-cell">
                    {formatDuration(
                      timeEntries
                        .filter(entry => entry.taskId === task.id)
                        .reduce((sum, entry) => sum + entry.durationMinutes, 0)
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Project Summary Tab */}
      {activeTab === 'summary' && (
        <div className="time-tracking-content">
          <div className="summary-section">
            <div className="summary-header">
              <h3>Project Time Summary</h3>
              <div className="summary-filters">
                <select 
                  value={selectedProjectForSummary} 
                  onChange={(e) => setSelectedProjectForSummary(e.target.value)}
                  className="filter-select"
                >
                  <option value="">Select Project</option>
                  {projects.map(project => (
                    <option key={project.id} value={project.id}>{project.name}</option>
                  ))}
                </select>
                <div className="date-range">
                  <DatePicker
                    selected={selectedDateRange.startDate}
                    onChange={(date) => setSelectedDateRange({...selectedDateRange, startDate: date})}
                    className="date-input"
                    placeholderText="Start Date"
                  />
                  <span>to</span>
                  <DatePicker
                    selected={selectedDateRange.endDate}
                    onChange={(date) => setSelectedDateRange({...selectedDateRange, endDate: date})}
                    className="date-input"
                    placeholderText="End Date"
                  />
                </div>
              </div>
            </div>

            {projectSummary && (
              <div className="summary-overview">
                <div className="summary-card">
                  <div className="summary-icon">⏰</div>
                  <div className="summary-content">
                    <h4>Total Hours</h4>
                    <span className="summary-value">{projectSummary.totalHours.toFixed(1)}h</span>
                  </div>
                </div>
                <div className="summary-card">
                  <div className="summary-icon">💰</div>
                  <div className="summary-content">
                    <h4>Billable Hours</h4>
                    <span className="summary-value">{projectSummary.billableHours.toFixed(1)}h</span>
                  </div>
                </div>
                <div className="summary-card">
                  <div className="summary-icon">📊</div>
                  <div className="summary-content">
                    <h4>Non-Billable Hours</h4>
                    <span className="summary-value">{projectSummary.nonBillableHours.toFixed(1)}h</span>
                  </div>
                </div>
                <div className="summary-card">
                  <div className="summary-icon">📝</div>
                  <div className="summary-content">
                    <h4>Total Entries</h4>
                    <span className="summary-value">{projectSummary.timeEntries.length}</span>
                  </div>
                </div>
              </div>
            )}

            {projectSummary && projectSummary.timeEntries.length > 0 && (
              <div className="summary-details">
                <h4>Time Entries</h4>
                <div className="entries-table">
                  <div className="table-header">
                    <div className="header-cell">Date</div>
                    <div className="header-cell">Task</div>
                    <div className="header-cell">User</div>
                    <div className="header-cell">Duration</div>
                    <div className="header-cell">Billable</div>
                    <div className="header-cell">Status</div>
                  </div>
                  {projectSummary.timeEntries.map(entry => (
                    <div key={entry.id} className="table-row">
                      <div className="table-cell">{entry.entryDate}</div>
                      <div className="table-cell">{entry.taskTitle}</div>
                      <div className="table-cell">{entry.userName}</div>
                      <div className="table-cell">{formatDuration(entry.durationMinutes)}</div>
                      <div className="table-cell">{entry.isBillable ? 'Yes' : 'No'}</div>
                      <div className="table-cell">
                        <span className={`status-badge ${entry.status.toLowerCase()}`}>
                          {entry.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Reports Tab */}
      {activeTab === 'reports' && (
        <div className="time-tracking-content">
          <div className="reports-section">
            <div className="reports-header">
              <h3>Time Reports</h3>
              <div className="report-actions">
                <button className="report-btn" onClick={exportToCSV}>
                  📊 Export CSV
                </button>
                <button className="report-btn">
                  📧 Export PDF
                </button>
              </div>
            </div>

            <div className="reports-grid">
              <div className="report-card">
                <div className="report-icon">📅</div>
                <h4>Daily Report</h4>
                <p>Track your daily time allocation and productivity</p>
                <button className="generate-btn">Generate</button>
              </div>
              <div className="report-card">
                <div className="report-icon">📊</div>
                <h4>Weekly Summary</h4>
                <p>Weekly overview of time spent on projects</p>
                <button className="generate-btn">Generate</button>
              </div>
              <div className="report-card">
                <div className="report-icon">📈</div>
                <h4>Monthly Analytics</h4>
                <p>Detailed monthly productivity analysis</p>
                <button className="generate-btn">Generate</button>
              </div>
              <div className="report-card">
                <div className="report-icon">🎯</div>
                <h4>Project Report</h4>
                <p>Time tracking for specific projects</p>
                <button className="generate-btn">Generate</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Today's Summary Modal */}
      {showTodaySummary && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Today's Summary</h3>
              <button 
                className="modal-close-btn"
                onClick={() => {
                  try {
                    setShowTodaySummary(false);
                  } catch (error) {
                    console.error('Error closing modal:', error);
                    // Force close by refreshing the component state
                    window.location.reload();
                  }
                }}
              >
                ✕
              </button>
            </div>
            
            <div className="modal-body">
              <div className="today-summary-stats">
                <div className="summary-stat">
                  <h4>Total Hours</h4>
                  <span className="stat-value">{todaySummary?.totalHours || '0.0'}h</span>
                </div>
                <div className="summary-stat">
                  <h4>Billable Hours</h4>
                  <span className="stat-value">{todaySummary?.billableHours || '0.0'}h</span>
                </div>
                <div className="summary-stat">
                  <h4>Non-Billable Hours</h4>
                  <span className="stat-value">{todaySummary?.nonBillableHours || '0.0'}h</span>
                </div>
                <div className="summary-stat">
                  <h4>Total Entries</h4>
                  <span className="stat-value">{todaySummary?.totalEntries || 0}</span>
                </div>
              </div>

              {todaySummary.entries && todaySummary.entries.length > 0 && (
                <div className="today-entries">
                  <h4>Today's Entries</h4>
                  <div className="entries-list">
                    {todaySummary.entries.map(entry => (
                      <div key={entry.id || Math.random()} className="entry-item">
                        <div className="entry-info">
                          <span className="entry-project">{entry.projectName || 'Unknown Project'}</span>
                          <span className="entry-task">{entry.taskTitle || 'Unknown Task'}</span>
                          <span className="entry-duration">{formatDuration(entry.durationMinutes || 0)}</span>
                        </div>
                        <span className={`entry-billable ${entry.isBillable ? 'billable' : 'non-billable'}`}>
                          {entry.isBillable ? '💰' : '📝'}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div className="modal-actions">
              <button 
                className="btn btn-secondary" 
                onClick={() => {
                  try {
                    setShowTodaySummary(false);
                  } catch (error) {
                    console.error('Error closing modal:', error);
                    // Force close by refreshing the component state
                    window.location.reload();
                  }
                }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Manual Entry Form Modal */}
      {showManualForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Add Manual Time Entry</h3>
              <button 
                className="modal-close-btn"
                onClick={() => setShowManualForm(false)}
              >
                ✕
              </button>
            </div>
            
            <div className="modal-body">
              <div className="form-group">
                <label>Project:</label>
                <select 
                  value={selectedProject} 
                  onChange={(e) => setSelectedProject(e.target.value)}
                  className="form-select"
                >
                  <option value="">Select Project</option>
                  {projects.map(project => (
                    <option key={project.id} value={project.id}>{project.name}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Task:</label>
                <select 
                  value={manualEntry.taskId} 
                  onChange={(e) => setManualEntry({...manualEntry, taskId: e.target.value})}
                  className="form-select"
                >
                  <option value="">Select Task</option>
                  {tasks.map(task => (
                    <option key={task.id} value={task.id}>{task.title}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Date:</label>
                <DatePicker
                  selected={manualEntry.entryDate}
                  onChange={(date) => setManualEntry({...manualEntry, entryDate: date})}
                  className="form-input"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Start Time:</label>
                  <input
                    type="time"
                    value={manualEntry.startTime}
                    onChange={(e) => setManualEntry({...manualEntry, startTime: e.target.value})}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label>End Time:</label>
                  <input
                    type="time"
                    value={manualEntry.endTime}
                    onChange={(e) => setManualEntry({...manualEntry, endTime: e.target.value})}
                    className="form-input"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Hours:</label>
                  <input
                    type="number"
                    min="0"
                    value={manualEntry.durationHours}
                    onChange={(e) => setManualEntry({...manualEntry, durationHours: parseInt(e.target.value) || 0})}
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label>Minutes:</label>
                  <input
                    type="number"
                    min="0"
                    max="59"
                    value={manualEntry.durationMinutes}
                    onChange={(e) => setManualEntry({...manualEntry, durationMinutes: parseInt(e.target.value) || 0})}
                    className="form-input"
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Description:</label>
                <textarea
                  value={manualEntry.description}
                  onChange={(e) => setManualEntry({...manualEntry, description: e.target.value})}
                  className="form-textarea"
                  rows="3"
                />
              </div>

              <div className="form-group">
                <label className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={manualEntry.isBillable}
                    onChange={(e) => setManualEntry({...manualEntry, isBillable: e.target.checked})}
                  />
                  Billable
                </label>
              </div>
            </div>

            <div className="modal-actions">
              <button 
                className="btn btn-primary" 
                onClick={saveManualEntry}
                disabled={isLoading}
              >
                {isLoading ? (
                  <>
                    <span className="loading-spinner"></span>
                    Saving...
                  </>
                ) : (
                  'Save Entry'
                )}
              </button>
              <button 
                className="btn btn-secondary" 
                onClick={() => setShowManualForm(false)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TimeTracking; 