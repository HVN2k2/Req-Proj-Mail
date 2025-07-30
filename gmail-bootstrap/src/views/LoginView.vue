<template>
  <LoginForm @loginSuccess="handleSuccess" @switchToRegister="goRegister" />
</template>

<script setup>
import { useRouter } from 'vue-router'
import LoginForm from '../components/LoginForm.vue'

const router = useRouter()

function handleSuccess() {
  router.push('/mail')
}

function goRegister() {
  router.push('/register')
}
</script>
