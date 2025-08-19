  import { createRouter, createWebHistory } from 'vue-router'

  // View chính
  import LoginView from '../views/LoginView.vue'
  import RegisterView from '../views/RegisterView.vue'
  import MailView from '../views/MailView.vue'
  import CallBackView from '../components/CallBack.vue'

  // View con của /mail
  import InboxView from '../views/InboxView.vue'
  import SentView from '../views/SentView.vue'
  import ComposeView from '../views/ComposeView.vue'
  import DraftsView from '../views/DraftsView.vue'
  import TrashView from '../views/TrashView.vue'
  import MailDetail from '../components/MailDetail.vue'

  const routes = [
    {
      path: '/',
      redirect: '/login', // Trang mặc định
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterView,
    },
    {
      path: '/inbox',
      redirect: '/mail/inbox', // Redirect /inbox về /mail/inbox
    },
    {
      path: '/laoid/auth/callback',
      name: 'LaoIdCallback',
      component: CallBackView,
    },
    {
      path: '/mail',
      name: 'Mail',
      component: MailView,
      children: [
        { path: '', redirect: 'inbox' }, // redirect nội bộ trong /mail
        { path: 'inbox', name: 'Inbox', component: InboxView },
        { path: 'sent', name: 'Sent', component: SentView },
        { path: 'compose', name: 'Compose', component: ComposeView },
        { path: 'drafts', name: 'Drafts', component: DraftsView },
        { path: 'trash', name: 'Trash', component: TrashView },
        { path: 'detail/:id', name: 'MailDetail', component: MailDetail },
      ]
    }
  ]

  const router = createRouter({
    history: createWebHistory(),
    routes,
  })

  // ✅ Route Guard: chặn truy cập vào /mail nếu chưa đăng nhập
  router.beforeEach((to, from, next) => {
    // Xóa token khi vào trang (để test)
    // localStorage.removeItem('token')
    
    const token = localStorage.getItem('token')

    // Nếu đang vào các route con của /mail mà không có token → về login
    if (to.path.startsWith('/mail') && !token) {
      next('/login')
    } else if ((to.path === '/login' || to.path === '/register') && token) {
      // Nếu đã login mà cố quay về login/register thì về /mail
      next('/mail')
    } else {
      next()
    }
  })

  export default router
