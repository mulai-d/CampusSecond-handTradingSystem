<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChevronLeft, ChevronRight, Eye, Search } from 'lucide-vue-next'
import { getProductList } from '../api/product'

const router = useRouter()

const products = ref([])
const loading = ref(false)
const error = ref('')
const keyword = ref('')
const category = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

const categories = ['生活用品', '教材', '数码', '运动', '出行']

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size.value)))

async function loadProducts() {
  loading.value = true
  error.value = ''

  try {
    const data = await getProductList({
      page: page.value,
      size: size.value,
      keyword: keyword.value,
      category: category.value
    })
    products.value = data.records || []
    total.value = Number(data.total || 0)
  } catch (err) {
    error.value = err.message
    products.value = []
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  loadProducts()
}

function selectCategory(value) {
  category.value = value
  page.value = 1
  loadProducts()
}

function goToPage(target) {
  if (target < 1 || target > totalPages.value) return
  page.value = target
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function openProduct(id) {
  router.push(`/products/${id}`)
}

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

onMounted(loadProducts)
</script>

<template>
  <section class="catalog">
    <div class="catalog-heading">
      <div>
        <p class="eyebrow">Campus Market</p>
        <h1>在售好物</h1>
      </div>
      <p class="count">{{ total }} 件商品</p>
    </div>

    <div class="toolbar">
      <label class="search-box">
        <Search :size="18" />
        <input
          v-model="keyword"
          type="search"
          placeholder="搜索商品"
          @keyup.enter="search"
        />
      </label>

      <div class="category-tabs" role="tablist" aria-label="商品分类">
        <button
          type="button"
          :class="{ active: category === '' }"
          @click="selectCategory('')"
        >
          全部
        </button>
        <button
          v-for="item in categories"
          :key="item"
          type="button"
          :class="{ active: category === item }"
          @click="selectCategory(item)"
        >
          {{ item }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="state-block">
      <span class="spinner" />
      <p>正在加载商品</p>
    </div>

    <div v-else-if="error" class="state-block error">
      <p>{{ error }}</p>
      <button type="button" class="primary-button" @click="loadProducts">重新加载</button>
    </div>

    <div v-else-if="products.length === 0" class="state-block">
      <p>暂时没有符合条件的商品</p>
    </div>

    <div v-else class="product-grid">
      <article
        v-for="product in products"
        :key="product.id"
        class="product-card"
        tabindex="0"
        @click="openProduct(product.id)"
        @keyup.enter="openProduct(product.id)"
      >
        <div class="image-wrap">
          <img :src="product.imageUrl" :alt="product.title" />
          <span class="category-chip">{{ product.category }}</span>
        </div>
        <div class="card-body">
          <h2>{{ product.title }}</h2>
          <p class="description">{{ product.description }}</p>
          <div class="card-footer">
            <strong class="price">¥{{ formatPrice(product.price) }}</strong>
            <span class="views"><Eye :size="15" /> {{ product.viewCount }}</span>
          </div>
        </div>
      </article>
    </div>

    <nav v-if="!loading && products.length > 0" class="pagination" aria-label="分页">
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
