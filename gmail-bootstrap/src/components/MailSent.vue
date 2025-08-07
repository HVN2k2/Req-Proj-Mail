<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>📤 Sent Mail</h2>
      <div v-if="loading" class="spinner-border spinner-border-sm" role="status">
        <span class="visually-hidden">Loading...</span>
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
              <strong class="text-truncate me-2">To: {{ mail.receiver }}</strong>
              <small class="text-muted">{{ formatDate(mail.createdAt) }}</small>
            </div>
            <p class="mb-1 fw-semibold">{{ mail.subject }}</p>
            <small class="text-muted text-truncate d-block">{{ mail.message }}</small>
          </div>
          <span class="badge bg-success ms-2">{{ mail.status }}</span>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else-if="!loading && mails.length === 0" class="text-center py-5">
      <i class="bi bi-send display-1 text-muted"></i>
      <h5 class="mt-3 text-muted">Chưa có mail nào đã gửi</h5>
      <p class="text-muted">Các mail bạn gửi sẽ xuất hiện ở đây</p>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2 text-muted">Đang tải mail đã gửi...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { mailAPI } from '@/api.js'

const mails = ref([])
const loading = ref(false)
const selectedMail = ref(null)

// Methods
async function loadSentMails() {
  loading.value = true
  try {
    const response = await mailAPI.getSentMails()
    console.log('📤 Sent mails response:', response.data)
    
    if (response.data && Array.isArray(response.data)) {
      mails.value = response.data
    } else {
      mails.value = []
    }
  } catch (error) {
    console.error('❌ Lỗi khi tải mail đã gửi:', error)
    mails.value = []
    // Fallback data cho demo
    mails.value = [
      { 
        id: 1, 
        receiver: "john@example.com", 
        subject: "Meeting notes", 
        message: "Gửi tài liệu cuộc họp tuần này", 
        status: "SENT",
        createdAt: new Date().toISOString()
      },
      { 
        id: 2, 
        receiver: "team@example.com", 
        subject: "Sprint report", 
        message: "Báo cáo sprint tháng 12", 
        status: "SENT",
        createdAt: new Date().toISOString()
      }
    ]
  } finally {
    loading.value = false
  }
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
  loadSentMails()
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
