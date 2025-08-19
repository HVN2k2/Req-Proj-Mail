<template>
  <div class="mail-detail-container">
    <!-- Header với nút back -->
    <div class="mail-detail-header d-flex align-items-center mb-4">
      <button @click="goBack" class="btn btn-outline-secondary me-3">
        <i class="fas fa-arrow-left"></i> Back
      </button>
      <h4 class="mb-0">📧 Mail Detail</h4>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">Loading mail details...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="alert alert-danger">
      <i class="fas fa-exclamation-triangle me-2"></i>
      {{ error }}
    </div>

    <!-- Mail content -->
    <div v-else-if="mail" class="mail-detail-content">
      <!-- Mail header -->
      <div class="card mb-4">
        <div class="card-header bg-light">
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <h5 class="card-title mb-1">{{ mail.subject || 'No Subject' }}</h5>
              <div class="mail-meta">
                <span class="badge bg-primary me-2">{{ getStatusBadge(mail.status) }}</span>
                <small class="text-muted">
                  <i class="fas fa-clock me-1"></i>
                  {{ formatDate(mail.createdAt) }}
                </small>
              </div>
            </div>
            <div class="mail-actions">
              <button @click="replyMail" class="btn btn-sm btn-outline-primary me-2">
                <i class="fas fa-reply"></i> Reply
              </button>
              <button @click="forwardMail" class="btn btn-sm btn-outline-secondary me-2">
                <i class="fas fa-share"></i> Forward
              </button>
              <button @click="deleteMail" class="btn btn-sm btn-outline-danger">
                <i class="fas fa-trash"></i> Delete
              </button>
            </div>
          </div>
        </div>
        
        <div class="card-body">
          <!-- From -->
          <div class="mail-field mb-3">
            <label class="fw-bold text-muted">From:</label>
            <div class="mail-value">
              <i class="fas fa-user me-2"></i>
              {{ mail.sender || 'Unknown' }}
            </div>
          </div>

          <!-- To -->
          <div class="mail-field mb-3">
            <label class="fw-bold text-muted">To:</label>
            <div class="mail-value">
              <i class="fas fa-envelope me-2"></i>
              {{ mail.receiver || mail.to || 'Unknown' }}
            </div>
          </div>

          <!-- CC (nếu có) -->
          <div v-if="mail.cc" class="mail-field mb-3">
            <label class="fw-bold text-muted">CC:</label>
            <div class="mail-value">
              <i class="fas fa-copy me-2"></i>
              {{ mail.cc }}
            </div>
          </div>

          <!-- BCC (nếu có) -->
          <div v-if="mail.bcc" class="mail-field mb-3">
            <label class="fw-bold text-muted">BCC:</label>
            <div class="mail-value">
              <i class="fas fa-eye-slash me-2"></i>
              {{ mail.bcc }}
            </div>
          </div>

          <!-- Date -->
          <div class="mail-field mb-3">
            <label class="fw-bold text-muted">Date:</label>
            <div class="mail-value">
              <i class="fas fa-calendar me-2"></i>
              {{ formatDate(mail.createdAt) }}
            </div>
          </div>
        </div>
      </div>

      <!-- Mail body -->
      <div class="card">
        <div class="card-body">
          <div class="mail-body">
            <div v-if="mail.message" v-html="formatMessage(mail.message)"></div>
            <div v-else-if="mail.body" v-html="formatMessage(mail.body)"></div>
            <div v-else class="text-muted">
              <i class="fas fa-info-circle me-2"></i>
              No message content available
            </div>
          </div>
        </div>
      </div>

      <!-- Attachments (nếu có) -->
      <div v-if="mail.attachments && mail.attachments.length > 0" class="card mt-4">
        <div class="card-header">
          <h6 class="mb-0">
            <i class="fas fa-paperclip me-2"></i>
            Attachments ({{ mail.attachments.length }})
          </h6>
        </div>
        <div class="card-body">
          <div class="row">
            <div v-for="attachment in mail.attachments" :key="attachment.id" class="col-md-6 mb-2">
              <div class="attachment-item d-flex align-items-center p-2 border rounded">
                <i class="fas fa-file me-2"></i>
                <div class="flex-grow-1">
                  <div class="fw-bold">{{ attachment.name }}</div>
                  <small class="text-muted">{{ formatFileSize(attachment.size) }}</small>
                </div>
                <button @click="downloadAttachment(attachment)" class="btn btn-sm btn-outline-primary">
                  <i class="fas fa-download"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- No mail found -->
    <div v-else class="text-center py-5">
      <i class="fas fa-envelope-open fa-3x text-muted mb-3"></i>
      <h5>Mail not found</h5>
      <p class="text-muted">The requested mail could not be found.</p>
      <button @click="goBack" class="btn btn-primary">
        <i class="fas fa-arrow-left me-2"></i>
        Go Back
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { mailAPI } from '../api.js'

const route = useRoute()
const router = useRouter()

const mail = ref(null)
const loading = ref(true)
const error = ref(null)

// Fetch mail details
const fetchMailDetail = async () => {
  try {
    loading.value = true
    error.value = null
    
    const mailId = route.params.id
    console.log('📧 Fetching mail detail for ID:', mailId)
    
    // Gọi API để lấy chi tiết mail
    const response = await mailAPI.getMailDetail(mailId)
    mail.value = response.data.mail || response.data
    
    console.log('✅ Mail detail loaded:', mail.value)
  } catch (err) {
    console.error('❌ Error fetching mail detail:', err)
    error.value = err.response?.data?.message || 'Failed to load mail details'
  } finally {
    loading.value = false
  }
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return 'Unknown date'
  
  try {
    const date = new Date(dateString)
    return date.toLocaleString('vi-VN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}

// Format message content
const formatMessage = (message) => {
  if (!message) return ''
  
  // Convert line breaks to <br> tags
  return message.replace(/\n/g, '<br>')
}

// Get status badge
const getStatusBadge = (status) => {
  const statusMap = {
    'SENT': 'Sent',
    'RECEIVED': 'Received',
    'PENDING': 'Pending',
    'FAILED': 'Failed',
    'DRAFT': 'Draft'
  }
  return statusMap[status] || status || 'Unknown'
}

// Navigation
const goBack = () => {
  router.back()
}

// Actions
const replyMail = () => {
  if (mail.value) {
    router.push({
      name: 'compose',
      query: {
        replyTo: mail.value.sender,
        subject: `Re: ${mail.value.subject}`,
        body: `\n\n--- Original Message ---\nFrom: ${mail.value.sender}\nDate: ${formatDate(mail.value.createdAt)}\nSubject: ${mail.value.subject}\n\n${mail.value.message || mail.value.body}`
      }
    })
  }
}

const forwardMail = () => {
  if (mail.value) {
    router.push({
      name: 'compose',
      query: {
        subject: `Fwd: ${mail.value.subject}`,
        body: `\n\n--- Forwarded Message ---\nFrom: ${mail.value.sender}\nDate: ${formatDate(mail.value.createdAt)}\nSubject: ${mail.value.subject}\n\n${mail.value.message || mail.value.body}`
      }
    })
  }
}

const deleteMail = async () => {
  if (!mail.value) return
  
  if (confirm('Are you sure you want to delete this mail?')) {
    try {
      await mailAPI.deleteMail(mail.value.id)
      console.log('✅ Mail deleted successfully')
      goBack()
    } catch (err) {
      console.error('❌ Error deleting mail:', err)
      alert('Failed to delete mail')
    }
  }
}

const downloadAttachment = (attachment) => {
  // Implement attachment download logic
  console.log('📎 Downloading attachment:', attachment)
  alert(`Downloading ${attachment.name}`)
}

const formatFileSize = (bytes) => {
  if (!bytes) return 'Unknown size'
  
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

onMounted(() => {
  fetchMailDetail()
})
</script>

<style scoped>
.mail-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.mail-detail-header {
  border-bottom: 1px solid #e9ecef;
  padding-bottom: 15px;
}

.mail-field {
  display: flex;
  align-items: flex-start;
}

.mail-field label {
  min-width: 80px;
  margin-right: 15px;
}

.mail-value {
  flex: 1;
  word-break: break-word;
}

.mail-body {
  line-height: 1.6;
  font-size: 14px;
}

.mail-actions {
  flex-shrink: 0;
}

.attachment-item {
  background-color: #f8f9fa;
  transition: background-color 0.2s;
}

.attachment-item:hover {
  background-color: #e9ecef;
}

@media (max-width: 768px) {
  .mail-detail-container {
    padding: 10px;
  }
  
  .mail-field {
    flex-direction: column;
  }
  
  .mail-field label {
    min-width: auto;
    margin-bottom: 5px;
  }
  
  .mail-actions {
    margin-top: 10px;
  }
}
</style>
