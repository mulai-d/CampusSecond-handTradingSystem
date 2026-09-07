<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { Store } from 'lucide-vue-next'
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">
        <Store :size="22" />
        <span>校园二手</span>
      </RouterLink>
    </header>

    <main class="page">
      <RouterView />
    </main>
  </div>
</template>
