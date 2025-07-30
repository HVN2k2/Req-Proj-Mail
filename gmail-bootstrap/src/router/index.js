import { createRouter, createWebHistory } from 'vue-router'

// Import các component
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import MailView from '../views/MailView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    component: LoginView,
  },
  {
    path: '/register',
    component: RegisterView,
  },
  {
    path: '/mail',
    component: MailView,
    // Có thể mở rộng thêm children ở đây nếu có inbox/sent/compose
    // children: [
    //   { path: 'inbox', component: InboxView },
    //   { path: 'sent', component: SentView },
    // ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// ✅ Auth Guard: chặn người dùng chưa login vào /mail
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.path.startsWith('/mail') && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
