<template>
  <div class="register-wrapper d-flex justify-content-center align-items-center min-vh-100 bg-body-tertiary">
    <div class="register-card card shadow-sm p-4 w-100" style="max-width: 450px;">
      <div class="text-center mb-4">
        <img src="https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x_r2.png" alt="Gmail Logo" height="36" />
        <h5 class="fw-semibold mt-3">Tạo tài khoản Gmail Clone</h5>
        <p class="text-muted small">Đăng ký để sử dụng hệ thống email cá nhân</p>
      </div>

      <form @submit.prevent="handleRegister">
        <div class="mb-3">
          <label for="name" class="form-label">Họ và tên</label>
          <input
            v-model="name"
            type="text"
            class="form-control"
            id="name"
            placeholder="Nguyễn Văn A"
            required
          />
        </div>

        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
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
            Đăng ký
          </button>
        </div>
      </form>

      <div class="text-center mt-3">
        <router-link to="/login" class="text-decoration-none small">
          Đã có tài khoản? <strong>Đăng nhập</strong>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const name = ref('')
const email = ref('')
const password = ref('')
const router = useRouter()

async function handleRegister() {
  try {
    const response = await axios.post('http://localhost:8080/mail-project/api/auth/register', {
      name: name.value,
      email: email.value,
      password: password.value
    });

    const token = response.data.token;
    localStorage.setItem('token', token); // Lưu token nếu cần

    console.log('Đăng ký thành công, token:', token);
    router.push('/login'); // Điều hướng nếu cần

  } catch (error) {
    console.error('Lỗi đăng ký:', error.response?.data || error.message);
    alert('Đăng ký thất bại. Kiểm tra lại thông tin.');
  }
}
</script>

<style scoped>
.register-wrapper {
  background-color: #f5f7f9;
}

.register-card {
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
