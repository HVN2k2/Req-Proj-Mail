<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2>✉️ Compose Mail</h2>
      <div v-if="loading" class="spinner-border spinner-border-sm" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>

    <form @submit.prevent="sendMail" class="compose-form">
      <div class="mb-3">
        <label for="to" class="form-label">To</label>
        <input 
          id="to"
          type="email" 
          v-model="form.to" 
          class="form-control" 
          placeholder="recipient@example.com"
          required
          :disabled="loading"
        >
      </div>
      
      <div class="mb-3">
        <label for="subject" class="form-label">Subject</label>
        <input 
          id="subject"
          type="text" 
          v-model="form.subject" 
          class="form-control" 
          placeholder="Enter subject..."
          required
          :disabled="loading"
        >
      </div>
      
      <div class="mb-3">
        <label for="message" class="form-label">Message</label>
        <textarea 
          id="message"
          v-model="form.message" 
          class="form-control" 
          rows="8" 
          placeholder="Enter your message..."
          required
          :disabled="loading"
        ></textarea>
      </div>
      
      <div class="d-flex gap-2">
        <button 
          type="submit" 
          class="btn btn-primary"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
          {{ loading ? 'Sending...' : 'Send Mail' }}
        </button>
        
        <button 
          type="button" 
          class="btn btn-secondary"
          @click="resetForm"
          :disabled="loading"
        >
          Reset
        </button>
        
        <button 
          type="button" 
          class="btn btn-outline-secondary"
          @click="goBack"
          :disabled="loading"
        >
          Cancel
        </button>
      </div>
    </form>

    <!-- Success Alert -->
    <div v-if="showSuccess" class="alert alert-success alert-dismissible fade show mt-3" role="alert">
      <i class="bi bi-check-circle me-2"></i>
      Mail sent successfully!
      <button type="button" class="btn-close" @click="showSuccess = false"></button>
    </div>

    <!-- Error Alert -->
    <div v-if="showError" class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
      <i class="bi bi-exclamation-triangle me-2"></i>
      {{ errorMessage }}
      <button type="button" class="btn-close" @click="showError = false"></button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { mailAPI } from '@/api.js'

const router = useRouter()
const loading = ref(false)
const showSuccess = ref(false)
const showError = ref(false)
const errorMessage = ref('')

const form = reactive({ 
  to: '', 
  subject: '', 
  message: '' 
})

async function sendMail() {
  // Kiểm tra token trước
  const token = localStorage.getItem('token')
  if (!token) {
    console.error('❌ Không có token, chuyển về trang login')
    router.push('/login')
    return
  }

  loading.value = true
  showSuccess.value = false
  showError.value = false
  
  try {
    console.log('�� Gửi mail:', form)
    console.log('🔑 Token:', token.substring(0, 20) + '...')
    
    const requestData = {
      to: form.to,
      subject: form.subject,
      message: form.message
    }
    
    console.log('📤 Request data:', requestData)
    
    const response = await mailAPI.sendMail(requestData)
    
    console.log('✅ Mail sent successfully:', response.data)
    showSuccess.value = true
    
    // Reset form sau khi gửi thành công
    resetForm()
    
    // Chuyển về inbox sau 2 giây
    setTimeout(() => {
      router.push('/mail/inbox')
    }, 2000)
    
  } catch (error) {
    console.error('❌ Lỗi khi gửi mail:', error)
    console.error('❌ Error response:', error.response?.data)
    console.error('❌ Error status:', error.response?.status)
    console.error('❌ Error headers:', error.response?.headers)
    
    // Nếu lỗi 401/403, có thể token hết hạn
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.error('🔒 Token không hợp lệ hoặc hết hạn')
      localStorage.removeItem('token')
      router.push('/login')
      return
    }
    
    errorMessage.value = error.response?.data?.message || 'Gửi mail thất bại. Vui lòng thử lại.'
    showError.value = true
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.to = ''
  form.subject = ''
  form.message = ''
  showSuccess.value = false
  showError.value = false
}

function goBack() {
  router.push('/mail/inbox')
}
</script>

<style scoped>
.compose-form {
  max-width: 800px;
}

.form-control:focus {
  border-color: #1a73e8;
  box-shadow: 0 0 0 0.2rem rgba(26, 115, 232, 0.25);
}

.btn-primary {
  background-color: #1a73e8;
  border-color: #1a73e8;
}

.btn-primary:hover {
  background-color: #1669c1;
  border-color: #1669c1;
}

.alert {
  border-radius: 8px;
}
</style>
