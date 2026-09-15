<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { LogIn, Store } from 'lucide-vue-next'
import { login } from '../api/auth'
import { setToken, setUsername } from '../utils/auth'

const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const registered = computed(() => route.query.registered === '1')

async function submit() {
  if (loading.value) return

  const name = username.value.trim()
  const pwd = password.value
  if (!name || !pwd) {
    error.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  error.value = ''
  try {
    const data = await login(name, pwd)
    setToken(data.token)
    setUsername(data.username)
    router.push(route.query.redirect || '/')
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
        <h1>登录</h1>
        <p>登录后即可收藏商品、发表留言</p>
      </div>

      <p v-if="registered" class="auth-success">注册成功，请登录</p>

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
            autocomplete="current-password"
            placeholder="请输入密码"
          />
        </label>

        <p v-if="error" class="auth-error">{{ error }}</p>

        <button type="submit" class="auth-submit" :disabled="loading">
          <LogIn :size="17" />
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <p class="auth-switch">
        还没有账号？
        <RouterLink to="/register">去注册</RouterLink>
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

.auth-success {
  margin: 0 0 16px;
  padding: 8px 10px;
  color: #0f766e;
  background: #f0fdfa;
  border-radius: 8px;
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
