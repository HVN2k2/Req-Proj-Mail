<template>
  <div class="login-wrapper d-flex justify-content-center align-items-center min-vh-100 bg-body-tertiary">
    <div class="login-card card shadow-sm p-4 w-100" style="max-width: 400px;">
      <div class="text-center mb-4">
        <img src="https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x_r2.png" alt="Gmail Logo" height="36" />
        <h5 class="fw-semibold mt-3">Đăng nhập vào Gmail Clone</h5>
        <p class="text-muted small">Quản lý email dễ dàng và an toàn</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label for="email" class="form-label">Địa chỉ Email</label>
          <input
            v-model="email"
            type="email"
            class="form-control"
            id="email"
            placeholder="example@gmail.com"
            required
          />
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">Mật khẩu</label>
          <input
            v-model="password"
            type="password"
            class="form-control"
            id="password"
            placeholder="••••••••"
            required
          />
        </div>

        <div class="d-grid">
          <button class="btn btn-primary fw-bold" type="submit">
            Đăng nhập
          </button>
        </div>
      </form>
      <div class="d-grid mt-3">
        <button id="laoid-signin" class="btn btn-outline-secondary fw-bold">
          Đăng nhập bằng LaoID
        </button>
      </div>
      <div class="text-center mt-3">
        <router-link to="/register" class="text-decoration-none small">
          Chưa có tài khoản? <strong>Đăng ký</strong>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const router = useRouter()
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  try {
    const response = await axios.post('http://localhost:8080/mail-project/api/auth/login', {
      email: email.value,
      password: password.value
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    const { token, message } = response.data
    localStorage.setItem('token', token)
    console.log('Đăng nhập thành công:', message)
    router.push('/mail')
  } catch (error) {
    console.error('Lỗi đăng nhập:', error.response?.data?.message || error.message)
    alert('Đăng nhập thất bại. Vui lòng kiểm tra email/mật khẩu.')
  } finally {
    loading.value = false
  }
}

const clientId = '660dfa27-5a95-4c88-8a55-abe1310bf579' // Thay bằng ClientID thực
const redirectUri = 'http://localhost:8080/mail-project/api/auth/laoid/callback'
const useCallbackUri = true

onMounted(() => {
  const script = document.createElement('script')
  script.text = `
    function getMetas() {
      const metas = {};
      const metaElements = document.getElementsByTagName("meta");
      for (let i = 0; i < metaElements.length; i++) {
        metas[metaElements[i].getAttribute("name")] =
          metaElements[i].getAttribute("content");
      }
      return metas;
    }

    function initializeSSO() {
      if (window.LaoIdSSO && window.LaoIdSSO.isInitialize) {
        return;
      }
      window.LaoIdSSO = {};
      window.LaoIdSSO.isInitialize = true;

      console.log("Load LaoID SSO");
      const metas = getMetas();

      window.LaoIdSSO.clientId = metas["laoid-signin-client-id"];
      window.LaoIdSSO.redirectUri = metas["laoid-signin-redirect-uri"];
      window.LaoIdSSO.useCallbackUri =
        metas["laoid-signin-use-callback-uri"] === "true";
      window.LaoIdSSO.apiEndpoint =
        "https://demo-sso.tinasoft.io/api/v1/third-party/authorize-host";

      window.LaoIdSSO.init = (clientId, redirectUri, useCallbackUri) => {
        window.LaoIdSSO.clientId = clientId;
        window.LaoIdSSO.redirectUri = redirectUri;
        window.LaoIdSSO.useCallbackUri = useCallbackUri || false;
        initSignInButton();
      };

      if (window.LaoIdSSO.clientId) {
        initSignInButton();
      }
    }

    async function openSSO() {
      const popupWidth = 455;
      const popupHeight = 810;
      const windowWidth = window.innerWidth || document.documentElement.clientWidth || screen.width;
      const windowHeight = window.innerHeight || document.documentElement.clientHeight || screen.height;
      const left = windowWidth / 2 - popupWidth / 2 + window.screenLeft;
      const top = windowHeight / 2 - popupHeight / 2 + window.screenTop;

      window.open(
        \`https://demo-sso.tinasoft.io/login?client_id=\${window.LaoIdSSO.clientId}&redirect_uri=\${window.LaoIdSSO.redirectUri}&use_callback_uri=\${window.LaoIdSSO.useCallbackUri}\`,
        "LaoID",
        \`height=\${popupHeight},width=\${popupWidth},top=\${top},left=\${left},resizable=no,location=no,menubar=no\`
      );
    }

    async function initSignInButton() {
      const signInButton = document.getElementById("laoid-signin");
      if (!signInButton) {
        console.log("Load LaoID SSO failed due to no button with id laoid-signin");
        return;
      }

      const response = await fetch(window.LaoIdSSO.apiEndpoint, {
        method: "POST",
        headers: {
          "Accept": "application/json",
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          clientId: window.LaoIdSSO.clientId,
          host: window.location.host,
        }),
      });
      const responseData = await response.json();

      if (
        response.ok &&
        responseData.success &&
        window.LaoIdSSO.clientId &&
        window.LaoIdSSO.redirectUri
      ) {
        console.log("Load LaoID SSO successfully");
        signInButton.removeEventListener("click", openSSO);
        signInButton.addEventListener("click", openSSO);
      } else if (response.ok && !responseData.success) {
        console.error(
          "Wrong laoid-signin-client-id or laoid-signin-redirect-uri. Please input the correct information or contact admin of LaoID",
        );
      } else {
        console.error(
          "Please add laoid-signin-client-id and laoid-signin-redirect-uri to metadata",
        );
      }
    }

    initializeSSO();
  `
  document.head.appendChild(script)

  if (window.LaoIdSSO) {
    window.LaoIdSSO.init(clientId, redirectUri, useCallbackUri)
  }
})

async function handleLaoIdLogin(authorizationCode) {
  loading.value = true
  try {
    const response = await axios.post('http://localhost:8080/mail-project/api/auth/laoid', {
      code: authorizationCode
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    const { token, message } = response.data
    if (token) {
      localStorage.setItem('token', token)
      console.log('Đăng nhập LaoID thành công:', message)
      router.push('/mail')
    } else {
      throw new Error(message || 'No token received')
    }
  } catch (error) {
    console.error('Lỗi đăng nhập LaoID:', error.response?.data?.message || error.message)
    alert('Đăng nhập bằng LaoID thất bại.')
  } finally {
    loading.value = false
  }
}

if (!useCallbackUri) {
  window.addEventListener('message', (event) => {
    if (event.origin !== 'https://demo-sso.tinasoft.io') return
    const { message, data } = event.data
    if (message === 'login_success') {
      handleLaoIdLogin(data.authorizationCode)
    } else if (message === 'login_fail') {
      alert('Đăng nhập bằng LaoID thất bại.')
    }
  }, false)
}
</script>

<style scoped>
.login-wrapper {
  background-color: #f5f7f9;
}

.login-card {
  border-radius: 12px;
}

.btn-primary {
  background-color: #1a73e8;
  border-color: #1a73e8;
}

.btn-primary:hover {
  background-color: #1669c1;
}
</style>