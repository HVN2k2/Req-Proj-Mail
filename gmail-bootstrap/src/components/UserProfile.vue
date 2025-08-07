<template>
  <div class="user-profile">
    <div class="d-flex align-items-center justify-content-between p-3 border-bottom">
      <div class="d-flex align-items-center">
        <div class="user-avatar me-3">
          <div class="avatar-circle">
            {{ userInitials }}
          </div>
        </div>
        <div class="user-info">
          <div class="user-name fw-semibold">{{ displayName }}</div>
          <div class="user-email text-muted small">{{ displayEmail }}</div>
        </div>
      </div>
      <div class="dropdown">
        <button 
          class="btn btn-outline-secondary btn-sm dropdown-toggle" 
          type="button" 
          @click="toggleDropdown"
          ref="dropdownButton"
        >
          <i class="bi bi-gear"></i>
        </button>
        <ul 
          class="dropdown-menu dropdown-menu-end" 
          :class="{ 'show': showDropdown }"
          ref="dropdownMenu"
          style="top: auto; bottom: 100%; margin-bottom: 5px;"
        >
          <li><a class="dropdown-item" href="#" @click="showProfileModal">Thông tin cá nhân</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item text-danger" href="#" @click="handleLogout">Đăng xuất</a></li>
        </ul>
      </div>
    </div>

    <!-- Profile Modal -->
    <div 
      class="modal fade" 
      :class="{ 'show d-block': showModal }" 
      tabindex="-1" 
      @click.self="closeModal"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Thông tin cá nhân</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Họ và tên</label>
              <input v-model="userInfo.name" type="text" class="form-control" />
            </div>
            <div class="mb-3">
              <label class="form-label">Email</label>
              <input v-model="userInfo.email" type="email" class="form-control" disabled />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">Hủy</button>
            <button type="button" class="btn btn-primary" @click="updateProfile">Cập nhật</button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Modal backdrop -->
    <div 
      class="modal-backdrop fade" 
      :class="{ 'show': showModal }" 
      v-if="showModal"
    ></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { userAPI, authAPI } from '@/api.js'

const router = useRouter()
const userInfo = ref({
  name: '',
  email: ''
})

const showDropdown = ref(false)
const showModal = ref(false)
const dropdownButton = ref(null)
const dropdownMenu = ref(null)
const isLoading = ref(true)

const userInitials = computed(() => {
  if (userInfo.value.name) {
    return userInfo.value.name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
  }
  return userInfo.value.email ? userInfo.value.email[0].toUpperCase() : 'U'
})

const displayName = computed(() => {
  return userInfo.value.name || userInfo.value.email || 'Người dùng'
})

const displayEmail = computed(() => {
  return userInfo.value.email || 'user@example.com'
})

onMounted(async () => {
  await loadUserProfile()
  // Đóng dropdown khi click bên ngoài
  document.addEventListener('click', handleClickOutside)
  
  // Lắng nghe sự kiện storage change để reload khi token thay đổi
  window.addEventListener('storage', handleStorageChange)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  window.removeEventListener('storage', handleStorageChange)
})

function handleStorageChange(event) {
  if (event.key === 'token' || event.key === 'userEmail') {
    console.log('🔄 Token hoặc userEmail đã thay đổi, reload thông tin user')
    loadUserProfile()
  }
}

function handleClickOutside(event) {
  if (dropdownButton.value && !dropdownButton.value.contains(event.target) &&
      dropdownMenu.value && !dropdownMenu.value.contains(event.target)) {
    showDropdown.value = false
  }
}

function toggleDropdown() {
  showDropdown.value = !showDropdown.value
}

async function loadUserProfile() {
  isLoading.value = true
  try {
    console.log('🔄 Đang tải thông tin người dùng...')
    const response = await userAPI.getProfile()
    console.log('✅ Thông tin người dùng:', response.data)
    
    // Cập nhật thông tin người dùng
    userInfo.value = {
      name: response.data.name || '',
      email: response.data.email || ''
    }
    
    // Lưu email vào localStorage để sử dụng cho mail API
    if (response.data.email) {
      localStorage.setItem('userEmail', response.data.email)
      console.log('📧 Đã lưu email user:', response.data.email)
    }
    
    console.log('📝 userInfo sau khi cập nhật:', userInfo.value)
  } catch (error) {
    console.error('❌ Lỗi khi tải thông tin người dùng:', error)
    
    // Fallback to email from localStorage if available
    const token = localStorage.getItem('token')
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        console.log('🔍 Token payload:', payload)
        userInfo.value = {
          name: payload.name || 'Người dùng',
          email: payload.email || 'user@example.com'
        }
        
        // Lưu email vào localStorage
        if (payload.email) {
          localStorage.setItem('userEmail', payload.email)
          console.log('📧 Đã lưu email từ token:', payload.email)
        }
      } catch (e) {
        console.error('❌ Lỗi decode token:', e)
        userInfo.value = {
          name: 'Người dùng',
          email: 'user@example.com'
        }
        localStorage.setItem('userEmail', 'user@example.com')
      }
    } else {
      userInfo.value = {
        name: 'Người dùng',
        email: 'user@example.com'
      }
      localStorage.setItem('userEmail', 'user@example.com')
    }
  } finally {
    isLoading.value = false
  }
}

function showProfileModal() {
  showModal.value = true
  showDropdown.value = false
}

function closeModal() {
  showModal.value = false
}

async function updateProfile() {
  try {
    await userAPI.updateProfile({
      name: userInfo.value.name,
      email: userInfo.value.email
    })
    alert('Cập nhật thông tin thành công!')
    closeModal()
  } catch (error) {
    console.error('Lỗi khi cập nhật thông tin:', error)
    alert('Cập nhật thông tin thất bại!')
  }
}

async function handleLogout() {
  if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
    try {
      // Gọi API logout để thông báo cho server
      await authAPI.logout()
    } catch (error) {
      console.error('Lỗi khi gọi API logout:', error)
      // Vẫn tiếp tục logout ở client side ngay cả khi API fail
    }
    
    // Xóa token và chuyển về trang login
    localStorage.removeItem('token')
    router.push('/login')
  }
}
</script>

<style scoped>
.user-profile {
  background: white;
  position: relative;
  z-index: 1001;
  padding: 1rem;
  border-top: 1px solid #dee2e6;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.user-name {
  font-size: 14px;
  line-height: 1.2;
}

.user-email {
  font-size: 12px;
  line-height: 1.2;
}

.dropdown-menu {
  min-width: 200px;
}

/* Đảm bảo dropdown hiển thị đúng */
.dropdown-menu.show {
  display: block !important;
}

/* Modal styles */
.modal.show {
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-backdrop.show {
  opacity: 0.5;
}
</style> 