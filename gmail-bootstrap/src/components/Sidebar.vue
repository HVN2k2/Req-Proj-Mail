<template>
  <div class="d-flex flex-column h-100 p-3 border-end bg-white">
    <button class="btn btn-danger w-100 mb-3" @click="$router.push('/mail/compose')">📨 Compose</button>
    <ul class="nav flex-column">
      <li class="nav-item">
        <router-link to="/mail/inbox" class="nav-link" active-class="active">📥 Inbox</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/mail/sent" class="nav-link" active-class="active">📤 Sent</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/mail/drafts" class="nav-link" active-class="active">📝 Drafts</router-link>
      </li>
      <li class="nav-item">
        <router-link to="/mail/trash" class="nav-link" active-class="active">🗑 Trash</router-link>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.nav-link.active {
  font-weight: bold;
  color: #d93025 !important;
}
</style>