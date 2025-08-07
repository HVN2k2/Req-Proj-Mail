<template>
  <div>
    <h4>📧 Mail Detail</h4>
    <div class="card p-4 shadow-sm">
      <h5>From: {{ mail.sender }}</h5>
      <p><strong>Subject:</strong> {{ mail.subject }}</p>
      <p>{{ mail.body }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const mail = ref({})

onMounted(() => {
  // Giả lập fetch mail từ id
  const id = route.params.id
  mail.value = {
    id,
    sender: 'noreply@gmail.com',
    subject: 'Demo Mail Subject',
    body: 'This is the body of the email message.'
  }
})
</script>
