<template>
  <div>Loading...</div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '@/api.js'

const router = useRouter()

onMounted(async () => {
  console.log('CallBack component mounted')
  const urlParams = new URLSearchParams(window.location.search)
  const authorizationCode = urlParams.get('authorization_code')
  console.log('Authorization code:', authorizationCode)

  if (authorizationCode) {
    try {
      console.log('Calling backend API...')
      console.log('API URL:', `http://localhost:8080/mail-project/api/auth/laoid/callback?authorization_code=${authorizationCode}`)
      
      const response = await authAPI.laoidCallback(authorizationCode)
      console.log('Backend response:', response.data)
      
      const { token, message } = response.data
      if (token) {
        localStorage.setItem('token', token)
        console.log('Đăng nhập LaoID thành công:', message)
        console.log('Redirecting to /mail/inbox...')
        router.push('/mail/inbox')
      } else {
        throw new Error(message || 'No token received')
      }
    } catch (error) {
      console.error('Lỗi đăng nhập LaoID:', error.response?.data?.message || error.message)
      console.error('Full error:', error)
      console.error('Error status:', error.response?.status)
      console.error('Error headers:', error.response?.headers)
      alert('Đăng nhập bằng LaoID thất bại.')
      router.push('/login')
    }
  } else {
    console.error('No authorization_code found')
    alert('Không tìm thấy authorization_code.')
    router.push('/login')
  }
})
</script>