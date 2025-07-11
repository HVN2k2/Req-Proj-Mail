<template>
  <div class="mail-item">
    <div class="mail-header">
      <span class="mail-from">{{ mail.from || mail.to }}</span>
      <span class="mail-date">{{ mail.date }}</span>
    </div>
    <div class="mail-subject">{{ mail.subject }}</div>
  </div>
</template>

<script>
export default {
  props: {
    mail: Object
  }
}
</script>

<style scoped>
.mail-item {
  background: #fff;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.3s;
}

.mail-item:hover {
  background: #f5f7fa;
}

.mail-header {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #888;
}

.mail-subject {
  font-size: 15px;
  font-weight: 500;
  margin-top: 4px;
  color: #333;
}
</style>
