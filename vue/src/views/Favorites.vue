<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronLeft, ChevronRight, Eye, Heart } from 'lucide-vue-next'
import { getMyFavorites } from '../api/favorite'

const router = useRouter()

const favorites = ref([])
const loading = ref(false)
const error = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size.value)))

async function loadFavorites() {
  loading.value = true
  error.value = ''

  try {
    const data = await getMyFavorites(page.value, size.value)
    favorites.value = data.records || []
    total.value = Number(data.total || 0)
  } catch (err) {
    error.value = err.message
    favorites.value = []
  } finally {
    loading.value = false
  }
}

function goToPage(target) {
  if (target < 1 || target > totalPages.value) return
  page.value = target
  loadFavorites()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function openProduct(id) {
  router.push(`/products/${id}`)
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function formatTime(value) {
  if (!value) return '-'
  return new Date(value).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

onMounted(loadFavorites)
</script>

<template>
  <section class="catalog">
    <div class="catalog-heading">
      <div>
        <p class="eyebrow">My Collection</p>
        <h1>我的收藏</h1>
      </div>
      <p class="count">{{ total }} 件商品</p>
    </div>

    <div v-if="loading" class="state-block">
      <span class="spinner" />
      <p>正在加载收藏</p>
    </div>

    <div v-else-if="error" class="state-block error">
      <p>{{ error }}</p>
      <button type="button" class="primary-button" @click="loadFavorites">重新加载</button>
    </div>

    <div v-else-if="favorites.length === 0" class="state-block">
      <Heart :size="30" />
      <p>还没有收藏任何商品</p>
    </div>

    <div v-else class="product-grid">
      <article
        v-for="item in favorites"
        :key="item.favoriteId"
        class="product-card"
        tabindex="0"
        @click="openProduct(item.productId)"
        @keyup.enter="openProduct(item.productId)"
      >
        <div class="image-wrap">
          <img :src="item.imageUrl" :alt="item.title" />
          <span class="category-chip">{{ item.category || '未分类' }}</span>
        </div>
        <div class="card-body">
          <h2>{{ item.title || '商品已删除' }}</h2>
          <p class="description">{{ item.description || '暂无商品描述' }}</p>
          <div class="card-footer">
            <strong class="price">¥{{ formatPrice(item.price) }}</strong>
            <span class="views"><Eye :size="15" /> {{ item.viewCount || 0 }}</span>
          </div>
          <p class="favorite-time">收藏于 {{ formatTime(item.favoriteTime) }}</p>
        </div>
      </article>
    </div>

    <nav v-if="!loading && favorites.length > 0" class="pagination" aria-label="分页">
      <button
        type="button"
        class="icon-button"
        :disabled="page <= 1"
        @click="goToPage(page - 1)"
      >
        <ChevronLeft :size="18" />
      </button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button
        type="button"
        class="icon-button"
        :disabled="page >= totalPages"
        @click="goToPage(page + 1)"
      >
        <ChevronRight :size="18" />
      </button>
    </nav>
  </section>
</template>
