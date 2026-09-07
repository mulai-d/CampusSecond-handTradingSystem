<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Eye, Tag } from 'lucide-vue-next'
import { getProductDetail } from '../api/product'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(false)
const error = ref('')

async function loadDetail() {
  loading.value = true
  error.value = ''

  try {
    product.value = await getProductDetail(route.params.id)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.push('/')
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

onMounted(loadDetail)
</script>

<template>
  <section class="detail-page">
    <button type="button" class="back-button" @click="goBack">
      <ArrowLeft :size="18" />
      返回商品列表
    </button>

    <div v-if="loading" class="state-block detail-state">
      <span class="spinner" />
      <p>正在加载详情</p>
    </div>

    <div v-else-if="error" class="state-block detail-state error">
      <p>{{ error }}</p>
      <button type="button" class="primary-button" @click="goBack">返回列表</button>
    </div>

    <div v-else-if="product" class="product-detail">
      <div class="media-panel">
        <img :src="product.imageUrl" :alt="product.title" />
      </div>

      <div class="info-panel">
        <div class="detail-meta">
          <span class="category-chip">{{ product.category }}</span>
          <span class="views"><Eye :size="16" /> {{ product.viewCount }} 次浏览</span>
        </div>

        <h1>{{ product.title }}</h1>
        <div class="price-line">
          <strong>¥{{ formatPrice(product.price) }}</strong>
        </div>

        <div class="section-block">
          <h2>商品描述</h2>
          <p>{{ product.description }}</p>
        </div>

        <div class="meta-list">
          <span><Tag :size="16" /> 发布者 ID：{{ product.userId }}</span>
          <span>发布时间：{{ formatTime(product.createTime) }}</span>
        </div>
      </div>
    </div>
  </section>
</template>
