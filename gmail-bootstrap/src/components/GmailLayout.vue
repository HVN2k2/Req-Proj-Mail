<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-2 bg-white border-end vh-100 p-0 position-relative">
        <div class="d-flex flex-column h-100">
          <div class="p-3 border-bottom">
            <router-link to="/mail/compose">
              <button class="btn btn-danger w-100 mb-2">📨 Compose</button>
            </router-link>
            <button 
              @click="fetchMails" 
              :disabled="isFetching"
              class="btn btn-outline-primary w-100"
              :title="isFetching ? 'Fetching...' : 'Fetch new mails'"
            >
              <span v-if="isFetching" class="spinner-border spinner-border-sm me-1"></span>
              {{ isFetching ? '🔄 Fetching...' : '📥 Fetch Mail' }}
            </button>
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
        <!-- Mail fetch status indicator -->
        <div v-if="isFetching" class="alert alert-info alert-dismissible fade show mb-3" role="alert">
          <span class="spinner-border spinner-border-sm me-2" role="status"></span>
          🔄 Fetching new mails...
          <button type="button" class="btn-close" @click="isFetching = false"></button>
        </div>
        
        <!-- Fetch result indicator -->
        <div v-if="fetchResult && !isFetching" class="alert alert-success alert-dismissible fade show mb-3" role="alert">
          <span v-if="fetchResult.success">✅</span>
          <span v-else>❌</span>
          {{ fetchResult.message || 'Mail fetch completed' }}
          <button type="button" class="btn-close" @click="fetchResult = null"></button>
        </div>
        
        <!-- Last fetch time indicator -->
        <div v-if="lastFetchTimeFormatted" class="text-muted small mb-2">
          📧 Last updated: {{ lastFetchTimeFormatted }}
        </div>
        
        <!-- 🟡 THÊM router-view ở đây -->
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref, computed } from 'vue'
import UserProfile from './UserProfile.vue'
import mailService from '../services/mailService.js'

// Reactive state
const isFetching = ref(false)
const lastFetchTime = ref(null)
const fetchResult = ref(null)

// Computed properties
const lastFetchTimeFormatted = computed(() => {
  if (!lastFetchTime.value) return null
  return new Date(lastFetchTime.value).toLocaleTimeString()
})

// Function để fetch mail
const fetchMails = async () => {
  if (isFetching.value) return
  
  try {
    isFetching.value = true
    fetchResult.value = null
    
    const result = await mailService.fetchMails()
    fetchResult.value = result
    
    if (result.success) {
      lastFetchTime.value = mailService.getLastFetchTime()
      console.log('📧 Mails fetched successfully')
    }
  } catch (error) {
    console.error('❌ Error in fetchMails:', error)
    fetchResult.value = { success: false, message: error.message }
  } finally {
    isFetching.value = false
  }
}

// Function để fetch mail nếu cần
const fetchIfNeeded = async () => {
  const result = await mailService.fetchIfNeeded()
  if (result.success) {
    lastFetchTime.value = mailService.getLastFetchTime()
  }
  return result
}

// Fetch mail khi component được mount
onMounted(async () => {
  console.log('🚀 GmailLayout mounted - fetching mails...')
  
  // Fetch mail ngay lập tức
  await fetchMails()
  
  // Fetch mail định kỳ mỗi 2 phút
  const interval = setInterval(async () => {
    await fetchIfNeeded()
  }, 2 * 60 * 1000) // 2 phút
  
  // Cleanup interval khi component unmount
  onUnmounted(() => {
    clearInterval(interval)
  })
})
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
