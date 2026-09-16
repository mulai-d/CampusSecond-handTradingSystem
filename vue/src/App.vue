<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Heart, LayoutDashboard, LogIn, LogOut, Store, UserPlus } from 'lucide-vue-next'
import { clearAuth, getUsername, isLoggedIn } from './utils/auth'

const route = useRoute()
const router = useRouter()

const loggedIn = ref(isLoggedIn())
const username = ref(getUsername())

watch(
  () => route.fullPath,
  () => {
    loggedIn.value = isLoggedIn()
    username.value = getUsername()
  }
)

function logout() {
  clearAuth()
  loggedIn.value = false
  username.value = ''
  router.push('/')
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">
        <Store :size="22" />
        <span>校园二手</span>
      </RouterLink>
      <nav class="nav-links">
        <RouterLink class="nav-link" to="/favorites">
          <Heart :size="18" />
          <span>我的收藏</span>
        </RouterLink>
        <RouterLink class="nav-link" to="/distribution">
          <LayoutDashboard :size="18" />
          <span>商品分布</span>
        </RouterLink>

        <template v-if="loggedIn">
          <span class="nav-user">
            <User :size="18" />
            <span>{{ username }}</span>
          </span>
          <button type="button" class="nav-link nav-action" @click="logout">
            <LogOut :size="18" />
            <span>退出</span>
          </button>
        </template>

        <template v-else>
          <RouterLink class="nav-link" to="/login">
            <LogIn :size="18" />
            <span>登录</span>
          </RouterLink>
          <RouterLink class="nav-link" to="/register">
            <UserPlus :size="18" />
            <span>注册</span>
          </RouterLink>
        </template>
      </nav>
    </header>

    <main class="page">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.nav-user {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  min-height: 36px;
  padding: 0 12px;
  color: #0f766e;
  font-size: 14px;
  font-weight: 600;
}

.nav-action {
  border: 0;
  background: transparent;
  font-weight: 600;
}
</style>
