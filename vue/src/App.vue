<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { Heart, Store } from 'lucide-vue-next'
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
      </nav>
    </header>

    <main class="page">
      <RouterView />
    </main>
  </div>
</template>
