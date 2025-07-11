import axios from 'axios';

const API = 'http://localhost:8080/mail-project/api/mail'; // Thay đổi port nếu backend chạy ở port khác

export default {
  getInbox() {
    return axios.get(`${API}/recived`);
  },
  getSent() {
    return axios.get(`${API}/sent`);
  },
  sendMail(mail) {
    return axios.post(`${API}/send`, mail);
  },
  deleteMail(id) {
    return axios.delete(`${API}/${id}`);
  },
  getAll() {
    return axios.get(`${API}/getAllMail`);
  }
};
