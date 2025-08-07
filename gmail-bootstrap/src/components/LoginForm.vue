<template>
  <div class="login-wrapper d-flex justify-content-center align-items-center min-vh-100 bg-body-tertiary">
    <div class="login-card card shadow-sm p-4 w-100" style="max-width: 400px;">
      <div class="text-center mb-4">
        <img src="https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x_r2.png" alt="Gmail Logo" height="36" />
        <h5 class="fw-semibold mt-3">Đăng nhập vào Gmail Clone</h5>
        <p class="text-muted small">Quản lý email dễ dàng và an toàn</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label for="email" class="form-label">Địa chỉ Email</label>
          <input
            v-model="email"
            type="email"
            class="form-control"
            id="email"
            placeholder="example@gmail.com"
            required
          />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">Mật khẩu</label>
          <input
            v-model="password"
            type="password"
            class="form-control"
            id="password"
            placeholder="••••••••"
            required
          />
        </div>

        <div class="d-grid">
          <button class="btn btn-primary fw-bold" type="submit">
            Đăng nhập
          </button>
        </div>
      </form>
      <div class="d-grid mt-3">
        <button id="laoid-signin" class="btn btn-outline-secondary fw-bold" @click="handleLaoIdClick">
          Đăng nhập bằng LaoID
        </button>
      </div>
      <div class="text-center mt-3">
        <router-link to="/register" class="text-decoration-none small">
          Chưa có tài khoản? <strong>Đăng ký</strong>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/api.js'

const email = ref('')
const password = ref('')
const router = useRouter()
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  try {
    // Xóa dữ liệu cũ trước khi đăng nhập
    localStorage.removeItem('token')
    localStorage.removeItem('userEmail')
    console.log('🧹 Đã xóa dữ liệu đăng nhập cũ')
    
    const response = await authAPI.login({
      email: email.value,
      password: password.value
    })
    const { token, message } = response.data
    
    // Lưu token mới
    localStorage.setItem('token', token)
    console.log('✅ Đăng nhập thành công:', message)
    router.push('/mail/inbox')
  } catch (error) {
    console.error('❌ Lỗi đăng nhập:', error.response?.data?.message || error.message)
    alert('Đăng nhập thất bại. Vui lòng kiểm tra email/mật khẩu.')
  } finally {
    loading.value = false
  }
}

const clientId = '660dfa27-5a95-4c88-8a55-abe1310bf579' // Thay bằng ClientID thực
const redirectUri = 'http://localhost/laoid/auth/callback' // Cập nhật để dùng port 80
const useCallbackUri = true

onMounted(async () => {
  // Đợi Vue render xong
  await nextTick()
  
  console.log("LoginForm mounted, LaoID ready")
})

async function handleLaoIdLogin(authorizationCode) {
  loading.value = true
  try {
    // Xóa dữ liệu cũ trước khi đăng nhập
    localStorage.removeItem('token')
    localStorage.removeItem('userEmail')
    console.log('🧹 Đã xóa dữ liệu đăng nhập cũ (LaoID)')
    
    // Sử dụng authAPI thay vì axios trực tiếp
    const response = await authAPI.laoidCallback(authorizationCode)
    const { token, message } = response.data
    if (token) {
      localStorage.setItem('token', token)
      console.log('✅ Đăng nhập LaoID thành công:', message)
      router.push('/mail/inbox')
    } else {
      throw new Error(message || 'No token received')
    }
  } catch (error) {
    console.error('❌ Lỗi đăng nhập LaoID:', error.response?.data?.message || error.message)
    alert('Đăng nhập bằng LaoID thất bại.')
  } finally {
    loading.value = false
  }
}

function handleLaoIdClick() {
  console.log("Opening LaoID popup...")
  
  // Tự implement popup LaoID
  const popupWidth = 455
  const popupHeight = 810
  const windowWidth = window.innerWidth || document.documentElement.clientWidth || screen.width
  const windowHeight = window.innerHeight || document.documentElement.clientHeight || screen.height
  const left = windowWidth / 2 - popupWidth / 2 + window.screenLeft
  const top = windowHeight / 2 - popupHeight / 2 + window.screenTop

  window.open(
    `https://demo-sso.tinasoft.io/login?client_id=${clientId}&redirect_uri=${redirectUri}&use_callback_uri=${useCallbackUri}`,
    "LaoID",
    `height=${popupHeight},width=${popupWidth},top=${top},left=${left},resizable=no,location=no,menubar=no`
  )
}
</script>

<style scoped>
.login-wrapper {
  background-color: #f5f7f9;
}

.login-card {
  border-radius: 12px;
}

.btn-primary {
  background-color: #1a73e8;
  border-color: #1a73e8;
}

.btn-primary:hover {
  background-color: #1669c1;
}
</style>