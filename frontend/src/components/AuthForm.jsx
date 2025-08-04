import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const AuthForm = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const { login, signup, user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      let result;
      
      if (isLogin) {
        result = await login(formData.username, formData.password);
      } else {
        result = await signup(formData);
      }

      if (result.success) {
        if (isLogin) {
          navigate('/dashboard');
        } else {
          setSuccess(result.message + ' Redirecting to login form...');
          setTimeout(() => {
            setIsLogin(true);
            setFormData({
              username: result.username || formData.username,
              email: '',
              password: '',
              firstName: '',
              lastName: ''
            });
            setSuccess('');
          }, 2500);
        }
      } else {
        setError(result.error);
      }
    } catch (err) {
      setError('An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = (isLoginMode) => {
    setIsLogin(isLoginMode);
    setFormData({
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: ''
    });
    setError('');
    setSuccess('');
  };

  return (
    <div className="container">
      {/* Background Elements */}
      <div className="background-elements">
        <div className="bg-element"></div>
        <div className="bg-element"></div>
        <div className="bg-element"></div>
        <div className="bg-element"></div>
        <div className="bg-element"></div>
        <div className="bg-element"></div>
      </div>
      
      <div className="auth-container">
        {/* Toggle Button at the top */}
        <div className="toggle-switch-container">
          <div className="toggle-switch">
            <button
              type="button"
              className={`toggle-option ${isLogin ? 'active' : ''}`}
              onClick={() => handleToggle(true)}
            >
              Log In
            </button>
            <button
              type="button"
              className={`toggle-option ${!isLogin ? 'active' : ''}`}
              onClick={() => handleToggle(false)}
            >
              Sign Up
            </button>
            <div className={`toggle-slider ${isLogin ? 'left' : 'right'}`}></div>
          </div>
        </div>

        <div className="auth-header">
          <h1 className="auth-title">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h1>
          <p className="auth-subtitle">
            {isLogin ? 'Log in to your account' : 'Join us today'}
          </p>
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        <form onSubmit={handleSubmit}>
          {!isLogin && (
            <>
              <div className="form-group">
                <label className="form-label">First Name</label>
                <input
                  type="text"
                  name="firstName"
                  className="form-input"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  required={!isLogin}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Last Name</label>
                <input
                  type="text"
                  name="lastName"
                  className="form-input"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  required={!isLogin}
                />
              </div>
            </>
          )}

          <div className="form-group">
            <label className="form-label">
              {isLogin ? 'Username or Email' : 'Username'}
            </label>
            <input
              type="text"
              name="username"
              className="form-input"
              value={formData.username}
              onChange={handleInputChange}
              required
            />
          </div>

          {!isLogin && (
            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                type="email"
                name="email"
                className="form-input"
                value={formData.email}
                onChange={handleInputChange}
                required={!isLogin}
              />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              name="password"
              className="form-input"
              value={formData.password}
              onChange={handleInputChange}
              required
              minLength={6}
            />
          </div>

          <button
            type="submit"
            className="form-button"
            disabled={loading}
          >
            {loading ? (
              <span className="loading"></span>
            ) : (
              isLogin ? 'Log In' : 'Create Account'
            )}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AuthForm; 