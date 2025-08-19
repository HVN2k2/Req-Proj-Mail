// src/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/mail-project/api', // base URL của backend bạn
});

// Gắn token (nếu có) vào request
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  console.log('🔑 Token check:', token ? 'Token exists' : 'No token found');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    console.log('🔐 Authorization header set:', `Bearer ${token.substring(0, 20)}...`);
  } else {
    console.log('⚠️ No token available for request');
  }
  console.log('🚀 API Request:', config.method?.toUpperCase(), config.url, config.data || config.params);
  return config;
});

// Log response
api.interceptors.response.use(
  response => {
    console.log('✅ API Response:', response.status, response.config.url, response.data);
    return response;
  },
  error => {
    console.error('❌ API Error:', error.response?.status, error.config?.url, error.response?.data);
    return Promise.reject(error);
  }
);

// Authentication APIs
export const authAPI = {
  // Đăng ký
  register: (userData) => api.post('/auth/register', userData),
  
  // Đăng nhập
  login: (credentials) => api.post('/auth/login', credentials),
  
  // LaoID callback
  laoidCallback: (authorizationCode) => api.get(`/auth/laoid/callback?authorization_code=${authorizationCode}`),
  
  // LaoID login (POST method)
  laoidLogin: (code) => api.post('/auth/laoid', { code }),
  
  // Đăng xuất
  logout: () => api.post('/auth/logout'),
};

// User APIs
export const userAPI = {
  // Lấy thông tin user
  getProfile: () => api.get('/user/profile'),
  
  // Cập nhật thông tin user
  updateProfile: (userData) => api.put('/user/profile', userData),
  
  // Đổi mật khẩu
  changePassword: (passwordData) => api.put('/user/password', passwordData),
};

// Mail APIs
export const mailAPI = {
  // Gửi mail
  sendMail: (mailData) => api.post('/mail/send', mailData),
  
  // Lấy inbox (tất cả mail)
  getInbox: (toEmail, page = 0, size = 10) => 
    api.get(`/mail/getAllMail?toEmail=${toEmail}&page=${page}&size=${size}`),
  
  // Lấy chi tiết mail theo ID
  getMailDetail: (mailId) => api.get(`/mail/detail/${mailId}`),
  
  // Lấy mail đã gửi
  getSentMails: () => api.get('/mail/sent'),
  
  // Lấy mail đang chờ
  getPendingMails: () => api.get('/mail/pending'),
  
  // Lấy mail đã nhận
  getReceivedMails: () => api.get('/mail/recived'),
  
  // Lấy mail gửi thất bại (drafts)
  getFailedMails: () => api.get('/mail/failed'),
  
  // Xóa mail
  deleteMail: (id) => api.delete(`/mail/${id}`),
};

export default api;
