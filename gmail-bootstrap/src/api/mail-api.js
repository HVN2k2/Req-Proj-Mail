// src/api/mailApi.js
import axios from 'axios'

const baseUrl = 'http://localhost:8080/mail-project/api/mail'

const getAuthHeader = () => ({
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`
  }
})

export const getInbox = () => axios.get(`${baseUrl}/getAllMail`, getAuthHeader())
export const getSent = () => axios.get(`${baseUrl}/sent`, getAuthHeader())
export const sendMail = (data) => axios.post(`${baseUrl}/send`, data, getAuthHeader())
export const getMailDetail = (id) => axios.get(`${baseUrl}/${id}`, getAuthHeader())
export const deleteMail = (id) => axios.delete(`${baseUrl}/delete/${id}`, getAuthHeader())
