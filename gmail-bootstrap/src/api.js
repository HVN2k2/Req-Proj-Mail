// src/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/mail-project/api', // base URL của backend bạn
});

// Gắn token (nếu có) vào request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
