<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-2 bg-white border-end vh-100 p-0 position-relative">
        <div class="d-flex flex-column h-100">
          <div class="p-3 border-bottom">
            <router-link to="/mail/compose">
              <button class="btn btn-danger w-100">📨 Compose</button>
            </router-link>
          </div>
          <ul class="nav flex-column p-3 flex-grow-1">
            <li class="nav-item">
              <router-link class="nav-link" to="/mail/inbox" active-class="active">📥 Inbox</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/mail/sent" active-class="active">📤 Sent</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/mail/drafts" active-class="active">📝 Drafts</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/mail/trash" active-class="active">🗑 Trash</router-link>
            </li>
          </ul>
          
          <!-- Spacer để tránh content bị che bởi UserProfile -->
          <div class="user-profile-spacer"></div>
        </div>
        
        <!-- User Profile Section - Fixed at bottom -->
        <div class="user-profile-container">
          <UserProfile />
        </div>
      </div>

      <!-- Main content -->
      <div class="col-md-10 px-4 pt-3">
        <!-- 🟡 THÊM router-view ở đây -->
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import UserProfile from './UserProfile.vue'
</script>

<style scoped>
.nav-link.active {
  font-weight: bold;
  color: #d93025 !important;
}

.user-profile-container {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 16.666667%; /* col-md-2 width */
  background: white;
  border-top: 1px solid #dee2e6;
  z-index: 1000;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
}

.user-profile-spacer {
  height: 120px; /* Chiều cao của UserProfile + padding */
}

/* Đảm bảo sidebar có scroll nếu cần */
.col-md-2 {
  overflow-y: auto;
  overflow-x: hidden;
}

/* Ẩn scrollbar nhưng vẫn scroll được */
.col-md-2::-webkit-scrollbar {
  width: 6px;
}

.col-md-2::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.col-md-2::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.col-md-2::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
