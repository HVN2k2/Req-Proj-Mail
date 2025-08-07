<template>
  <div>
    <h2 class="text-xl font-bold mb-2">Thư đã xoá</h2>
    <ul>
      <li v-for="(mail, index) in trashMails" :key="index" class="border p-2 my-1">
        <strong>Chủ đề:</strong> {{ mail.subject }}<br>
        <strong>Người gửi:</strong> {{ mail.sender }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'TrashComponent',
  data() {
    return {
      trashMails: [
        { subject: 'Mail cũ 1', sender: 'abc@example.com' },
        { subject: 'Mail cũ 2', sender: 'xyz@example.com' },
      ]
    }
  }
}
</script>
