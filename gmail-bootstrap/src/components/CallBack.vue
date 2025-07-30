<template>
  <div>Loading...</div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search)
  const authorizationCode = urlParams.get('authorization_code')

  if (authorizationCode) {
    try {
      const response = await axios.get(`http://localhost:8080/mail-project/api/auth/laoid/callback?authorization_code=${authorizationCode}`)
      const { token, message } = response.data
      if (token) {
        localStorage.setItem('token', token)
        console.log('Đăng nhập LaoID thành công:', message)
        router.push('/mail')
      } else {
        throw new Error(message || 'No token received')
      }
    } catch (error) {
      console.error('Lỗi đăng nhập LaoID:', error.response?.data?.message || error.message)
      alert('Đăng nhập bằng LaoID thất bại.')
      router.push('/login')
    }
  } else {
    alert('Không tìm thấy authorization_code.')
    router.push('/login')
  }
})
</script>