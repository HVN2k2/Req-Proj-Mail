<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>📥 Inbox</h2>
      <div v-if="loading" class="spinner-border spinner-border-sm" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    
    <!-- Search bar -->
    <div class="mb-3">
      <div class="input-group">
        <input 
          v-model="searchKeyword" 
          type="text" 
          class="form-control" 
          placeholder="Tìm kiếm mail..."
          @input="handleSearch"
        >
        <button class="btn btn-outline-secondary" type="button" @click="handleSearch">
          <i class="bi bi-search"></i>
        </button>
      </div>
    </div>

    <!-- Mail list -->
    <div v-if="!loading && mails.length > 0" class="list-group">
      <div 
        v-for="mail in mails" 
        :key="mail.id" 
        class="list-group-item list-group-item-action"
        @click="selectMail(mail)"
        :class="{ 'active': selectedMail?.id === mail.id }"
      >
        <div class="d-flex justify-content-between align-items-start">
          <div class="flex-grow-1">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <strong class="text-truncate me-2">{{ mail.sender }}</strong>
              <small class="text-muted">{{ formatDate(mail.createdAt) }}</small>
            </div>
            <p class="mb-1 fw-semibold">{{ mail.subject }}</p>
            <small class="text-muted text-truncate d-block">{{ mail.message }}</small>
          </div>
          <span class="badge bg-primary ms-2">{{ mail.status }}</span>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else-if="!loading && mails.length === 0" class="text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h5 class="mt-3 text-muted">Hộp thư trống</h5>
      <p class="text-muted">Chưa có mail nào trong inbox</p>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2 text-muted">Đang tải mail...</p>
    </div>

    <!-- Pagination -->
    <nav v-if="totalPages > 1" class="mt-3">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">Trước</a>
        </li>
        <li 
          v-for="page in visiblePages" 
          :key="page" 
          class="page-item"
          :class="{ active: page === currentPage }"
        >
          <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page + 1 }}</a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">Sau</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { mailAPI } from '@/api.js'

const mails = ref([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)
const searchKeyword = ref('')
const selectedMail = ref(null)

// Computed properties
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, currentPage.value - 2)
  const end = Math.min(totalPages.value - 1, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// Methods
async function loadMails() {
  // Kiểm tra token trước
  const token = localStorage.getItem('token')
  if (!token) {
    console.error('❌ Không có token, chuyển về trang login')
    window.location.href = '/login'
    return
  }
  
  loading.value = true
  try {
    // Lấy email từ localStorage hoặc user info
    const userEmail = localStorage.getItem('userEmail') || 'test@example.com'
    console.log('📧 Loading mails for email:', userEmail)
    
    const response = await mailAPI.getInbox(userEmail, currentPage.value, pageSize.value)
    console.log('📧 Mail response:', response.data)
    
    if (response.data && response.data.content) {
      mails.value = response.data.content
      totalPages.value = response.data.totalPages
      totalElements.value = response.data.totalElements
    } else {
      mails.value = []
      totalPages.value = 0
      totalElements.value = 0
    }
  } catch (error) {
    console.error('❌ Lỗi khi tải mail:', error)
    
    // Nếu lỗi 401/403, có thể token hết hạn
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.error('🔒 Token không hợp lệ hoặc hết hạn')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return
    }
    
    mails.value = []
    // Fallback data cho demo
    mails.value = [
      { 
        id: 1, 
        sender: "google@gmail.com", 
        subject: "Welcome to Gmail", 
        message: "Chào mừng bạn đến với Gmail!", 
        status: "RECEIVED",
        createdAt: new Date().toISOString()
      },
      { 
        id: 2, 
        sender: "youtube@gmail.com", 
        subject: "Video approved", 
        message: "Video của bạn đã được duyệt!", 
        status: "RECEIVED",
        createdAt: new Date().toISOString()
      }
    ]
  } finally {
    loading.value = false
  }
}

function changePage(page) {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadMails()
  }
}

function handleSearch() {
  currentPage.value = 0
  loadMails()
}

function selectMail(mail) {
  selectedMail.value = mail
  // Emit event để parent component có thể xử lý
  // this.$emit('mail-selected', mail)
}

function formatDate(dateString) {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now - date)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 1) {
    return 'Hôm nay'
  } else if (diffDays === 2) {
    return 'Hôm qua'
  } else if (diffDays <= 7) {
    return `${diffDays - 1} ngày trước`
  } else {
    return date.toLocaleDateString('vi-VN')
  }
}

// Lifecycle
onMounted(() => {
  loadMails()
})
</script>

<style scoped>
.list-group-item {
  cursor: pointer;
  transition: background-color 0.2s;
}

.list-group-item:hover {
  background-color: #f8f9fa;
}

.list-group-item.active {
  background-color: #e3f2fd;
  border-color: #2196f3;
}

.text-truncate {
  max-width: 200px;
}
</style>
