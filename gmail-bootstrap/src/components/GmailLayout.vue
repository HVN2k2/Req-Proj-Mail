<template>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-md-2 bg-white border-end vh-100 p-0">
        <div class="d-flex flex-column h-100">
          <div class="p-3 border-bottom">
            <button class="btn btn-danger w-100">📨 Compose</button>
          </div>
          <ul class="nav flex-column p-3">
            <li class="nav-item"><a class="nav-link active" href="#">📥 Inbox</a></li>
            <li class="nav-item"><a class="nav-link" href="#">📤 Sent</a></li>
            <li class="nav-item"><a class="nav-link" href="#">📝 Drafts</a></li>
            <li class="nav-item"><a class="nav-link" href="#">🗑 Trash</a></li>
          </ul>
        </div>
      </div>

      <!-- Main content -->
      <div class="col-md-10 px-4 pt-3">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
          <h1 class="h4">Inbox</h1>
          <input type="text" class="form-control w-25" placeholder="Search mail...">
        </div>

        <!-- Mail list -->
        <div class="list-group shadow-sm">
          <a href="#" class="list-group-item list-group-item-action">
            <div class="d-flex justify-content-between">
              <h5 class="mb-1">Google</h5>
              <small>10:30 AM</small>
            </div>
            <p class="mb-1">Welcome to your Gmail clone project. Let's get started.</p>
            <small class="text-muted">noreply@gmail.com</small>
          </a>
          <a href="#" class="list-group-item list-group-item-action">
            <div class="d-flex justify-content-between">
              <h5 class="mb-1">YouTube</h5>
              <small>9:15 AM</small>
            </div>
            <p class="mb-1">Your video has been approved. Check it now!</p>
            <small class="text-muted">youtube-noreply@gmail.com</small>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// nothing for now, can add props or pinia store later
</script>

<style scoped>
.nav-link.active {
  font-weight: bold;
  color: #d93025 !important;
}
</style>
