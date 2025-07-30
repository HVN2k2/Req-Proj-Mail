<template>
  <RegisterForm @switchToLogin="goLogin" @loginSuccess="handleSuccess" />
</template>

<script setup>
import { useRouter } from 'vue-router'
import RegisterForm from '../components/RegisterForm.vue'

const router = useRouter()

function goLogin() {
  router.push('/login')
}

function handleSuccess() {
  router.push('/mail')
}
</script>
