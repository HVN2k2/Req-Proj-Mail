<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>📝 Thư nháp</h2>
      <div v-if="loading" class="spinner-border spinner-border-sm" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <!-- Mail list -->
    <div v-if="!loading && drafts.length > 0" class="list-group">
      <div 
        v-for="draft in drafts" 
        :key="draft.id" 
        class="list-group-item list-group-item-action"
        @click="selectDraft(draft)"
        :class="{ 'active': selectedDraft?.id === draft.id }"
      >
        <div class="d-flex justify-content-between align-items-start">
          <div class="flex-grow-1">
            <div class="d-flex justify-content-between align-items-center mb-1">
              <strong class="text-truncate me-2">To: {{ draft.receiver }}</strong>
              <small class="text-muted">{{ formatDate(draft.createdAt) }}</small>
            </div>
            <p class="mb-1 fw-semibold">{{ draft.subject }}</p>
            <small class="text-muted text-truncate d-block">{{ draft.message }}</small>
          </div>
          <span class="badge bg-warning ms-2">{{ draft.status }}</span>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else-if="!loading && drafts.length === 0" class="text-center py-5">
      <i class="bi bi-file-earmark-text display-1 text-muted"></i>
      <h5 class="mt-3 text-muted">Không có thư nháp</h5>
      <p class="text-muted">Các mail gửi thất bại sẽ xuất hiện ở đây</p>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2 text-muted">Đang tải thư nháp...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { mailAPI } from '@/api.js'

const drafts = ref([])
const loading = ref(false)
const selectedDraft = ref(null)

// Methods
async function loadDrafts() {
  // Kiểm tra token trước
  const token = localStorage.getItem('token')
  if (!token) {
    console.error('❌ Không có token, chuyển về trang login')
    window.location.href = '/login'
    return
  }
  
  loading.value = true
  try {
    // Load mail có status FAIL từ backend
    const response = await mailAPI.getFailedMails()
    console.log('📝 Failed mails response:', response.data)
    
    if (response.data && Array.isArray(response.data)) {
      drafts.value = response.data
    } else {
      drafts.value = []
    }
  } catch (error) {
    console.error('❌ Lỗi khi tải thư nháp:', error)
    
    // Nếu lỗi 401/403, có thể token hết hạn
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.error('🔒 Token không hợp lệ hoặc hết hạn')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return
    }
    
    drafts.value = []
    // Fallback data cho demo
    drafts.value = [
      { 
        id: 1, 
        receiver: "test@example.com", 
        subject: "Mail gửi thất bại 1", 
        message: "Nội dung mail bị lỗi khi gửi", 
        status: "FAILED",
        createdAt: new Date().toISOString()
      },
      { 
        id: 2, 
        receiver: "demo@example.com", 
        subject: "Mail gửi thất bại 2", 
        message: "Mail không thể gửi do lỗi server", 
        status: "FAILED",
        createdAt: new Date().toISOString()
      }
    ]
  } finally {
    loading.value = false
  }
}

function selectDraft(draft) {
  selectedDraft.value = draft
  // Emit event để parent component có thể xử lý
  // this.$emit('draft-selected', draft)
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
  loadDrafts()
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
