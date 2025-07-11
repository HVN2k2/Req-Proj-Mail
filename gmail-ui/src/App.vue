<template>
  <div class="app-wrapper">
    <!-- Sidebar -->
    <aside class="sidebar">
      <h2 class="logo">📧 Gmail Clone</h2>
      <button class="compose-btn" @click="showCompose = true">✉️ Soạn thư</button>
      <ul class="nav">
        <li :class="navClass('inbox')" @click="currentView = 'inbox'">Inbox</li>
        <li :class="navClass('sendbox')" @click="currentView = 'sendbox'">Sent</li>
      </ul>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <div class="header-bar">
        <h1>{{ currentView.toUpperCase() }}</h1>
        <input v-model="searchQuery" placeholder="Search mail..." />
      </div>

      <!-- Danh sách thư -->
      <div v-if="!selectedMail && currentView === 'inbox'">
        <div v-for="mail in filteredInbox" :key="mail.id" class="mail-item" @click="viewMail(mail)">
          <strong>{{ mail.subject }}</strong><br />
          <small>From: {{ mail.from }}</small>
        </div>
      </div>

      <div v-if="!selectedMail && currentView === 'sendbox'">
        <div v-for="mail in filteredSendbox" :key="mail.id" class="mail-item" @click="viewMail(mail)">
          <strong>{{ mail.subject }}</strong><br />
          <small>To: {{ mail.to }}</small>
        </div>
      </div>

      <!-- Chi tiết thư -->
      <div v-if="selectedMail" class="mail-detail">
        <button @click="goBack" class="back-btn">← Quay lại</button>
        <h2>{{ selectedMail.subject }}</h2>
        <p><strong>Từ:</strong> {{ selectedMail.from || 'Tôi' }}</p>
        <p><strong>Đến:</strong> {{ selectedMail.to || 'Tôi' }}</p>
        <p><strong>Ngày:</strong> {{ selectedMail.date || '-' }}</p>
        <hr />
        <p class="body">{{ selectedMail.message }}</p>
        <button @click="deleteMail(selectedMail.id)">🗑 Xoá thư</button>
      </div>
    </main>

    <!-- Soạn thư -->
    <div v-if="showCompose" class="compose-modal">
      <div class="compose-box">
        <h2>Soạn thư</h2>
        <input v-model="newMail.to" placeholder="Người nhận" />
        <input v-model="newMail.subject" placeholder="Tiêu đề" />
        <textarea v-model="newMail.message" placeholder="Nội dung..."></textarea>
        <div class="actions">
          <button @click="sendMail">Gửi</button>
          <button @click="showCompose = false">Huỷ</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import mailService from './services/mailService';

export default {
  data() {
    return {
      inbox: [],
      sendbox: [],
      selectedMail: null,
      currentView: 'inbox',
      showCompose: false,
      searchQuery: '',
      newMail: {
        from: 'me@example.com', // tạm hardcode nếu chưa có login
        to: '',
        subject: '',
        message: ''
      }
    };
  },
  mounted() {
    this.loadInbox();
    this.loadSendbox();
  },
  computed: {
    filteredInbox() {
      return this.inbox.filter(mail =>
        mail.subject.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    },
    filteredSendbox() {
      return this.sendbox.filter(mail =>
        mail.subject.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
    }
  },
  methods: {
    navClass(view) {
      return this.currentView === view ? 'active' : '';
    },
    loadInbox() {
      mailService.getInbox().then(res => this.inbox = res.data);
    },
    loadSendbox() {
      mailService.getSent().then(res => this.sendbox = res.data);
    },
    sendMail() {
      const { to, subject, message } = this.newMail;
      if (!to || !subject || !message) {
        alert('Vui lòng nhập đầy đủ thông tin');
        return;
      }

      mailService.sendMail(this.newMail).then(() => {
        this.showCompose = false;
        this.newMail = {
          from: 'me@example.com',
          to: '',
          subject: '',
          message: ''
        };
        this.loadSendbox();
        this.currentView = 'sendbox';
      });
    }, // ✅ đã thêm dấu phẩy ở đây

    viewMail(mail) {
      this.selectedMail = mail;
    },
    goBack() {
      this.selectedMail = null;
    },
    deleteMail(id) {
      mailService.deleteMail(id).then(() => {
        this.goBack();
        this.loadInbox();
        this.loadSendbox();
      });
    }
  }
};
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  font-family: Arial, sans-serif;
  background: #f0f2f5;
}
.sidebar {
  width: 240px;
  background: #ffffff;
  padding: 20px;
  border-right: 1px solid #ddd;
}
.logo {
  font-size: 20px;
  margin-bottom: 20px;
  font-weight: bold;
}
.compose-btn {
  background-color: #007bff;
  color: white;
  padding: 10px;
  width: 100%;
  border: none;
  border-radius: 4px;
  margin-bottom: 20px;
  cursor: pointer;
}
.nav li {
  padding: 10px 0;
  cursor: pointer;
}
.nav li.active {
  font-weight: bold;
  color: #007bff;
}
.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
.mail-item {
  background: white;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 4px;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.mail-detail {
  background: white;
  padding: 20px;
  border-radius: 8px;
}
.back-btn {
  margin-bottom: 10px;
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
}
.compose-modal {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex; justify-content: center; align-items: center;
}
.compose-box {
  background: white;
  width: 400px;
  padding: 20px;
  border-radius: 8px;
}
.compose-box input, .compose-box textarea {
  width: 100%;
  margin-bottom: 12px;
  padding: 8px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
