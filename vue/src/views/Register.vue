<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Store, UserPlus } from 'lucide-vue-next'
import { register } from '../api/auth'

const router = useRouter()

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

async function submit() {
  if (loading.value) return

  const name = username.value.trim()
  const pwd = password.value
  if (!name || !pwd || !confirmPassword.value) {
    error.value = '请填写完整信息'
    return
  }
  if (pwd.length < 6) {
    error.value = '密码长度至少 6 位'
    return
  }
  if (pwd !== confirmPassword.value) {
    error.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  error.value = ''
  try {
    await register(name, pwd)
    router.push('/login?registered=1')
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-card">
      <div class="auth-head">
        <Store :size="30" />
        <h1>注册</h1>
        <p>创建账号，开始校园二手交易</p>
      </div>

      <form class="auth-form" @submit.prevent="submit">
        <label class="field">
          <span>用户名</span>
          <input
            v-model="username"
            type="text"
            autocomplete="username"
            placeholder="请输入用户名"
          />
        </label>
        <label class="field">
          <span>密码</span>
          <input
            v-model="password"
            type="password"
            autocomplete="new-password"
            placeholder="至少 6 位"
          />
        </label>
        <label class="field">
          <span>确认密码</span>
          <input
            v-model="confirmPassword"
            type="password"
            autocomplete="new-password"
            placeholder="再次输入密码"
          />
        </label>

        <p v-if="error" class="auth-error">{{ error }}</p>

        <button type="submit" class="auth-submit" :disabled="loading">
          <UserPlus :size="17" />
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>

      <p class="auth-switch">
        已有账号？
        <RouterLink to="/login">去登录</RouterLink>
      </p>
    </div>
  </section>
</template>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  padding: 48px 16px;
}

.auth-card {
  width: min(400px, 100%);
  padding: 32px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.auth-head {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
  color: #0f766e;
}

.auth-head h1 {
  margin: 0;
  font-size: 26px;
  color: #1f2937;
}

.auth-head p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #4b5563;
}

.field input {
  height: 42px;
  padding: 0 12px;
  color: #1f2937;
  background: #f9fafb;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  outline: none;
}

.field input:focus {
  border-color: #0f766e;
  box-shadow: 0 0 0 3px rgba(15, 118, 110, 0.12);
}

.auth-error {
  margin: 0;
  color: #b91c1c;
  font-size: 13px;
}

.auth-submit {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  width: 100%;
  height: 42px;
  border: 0;
  border-radius: 8px;
  color: #ffffff;
  background: #0f766e;
  font-weight: 600;
}

.auth-switch {
  margin: 20px 0 0;
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

.auth-switch a {
  color: #0f766e;
  text-decoration: none;
  font-weight: 600;
}
</style>
