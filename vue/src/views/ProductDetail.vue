<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  ChevronLeft,
  ChevronRight,
  CornerDownRight,
  Eye,
  Heart,
  MessageCircle,
  Send,
  Tag
} from 'lucide-vue-next'
import { getProductDetail } from '../api/product'
import { addFavorite, getFavoriteStatus, removeFavorite } from '../api/favorite'
import { addProductMessage, getProductMessages } from '../api/message'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(false)
const error = ref('')

const favoriteStatus = ref(false)
const favoriteLoading = ref(false)
const favoriteError = ref('')

const messages = ref([])
const messageLoading = ref(false)
const messageError = ref('')
const messageContent = ref('')
const messageSubmitting = ref(false)
const replyParentId = ref(0)
const messagePage = ref(1)
const messageSize = ref(20)
const messageTotal = ref(0)
const messageComposer = ref(null)

const messageTotalPages = computed(() => Math.max(1, Math.ceil(messageTotal.value / messageSize.value)))

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

async function loadFavoriteStatus() {
  try {
    favoriteStatus.value = await getFavoriteStatus(route.params.id)
  } catch (err) {
    favoriteStatus.value = false
  }
}

async function toggleFavorite() {
  if (favoriteLoading.value) return

  favoriteLoading.value = true
  favoriteError.value = ''

  try {
    if (favoriteStatus.value) {
      await removeFavorite(route.params.id)
    } else {
      await addFavorite(route.params.id)
    }
    favoriteStatus.value = !favoriteStatus.value
  } catch (err) {
    favoriteError.value = err.message
  } finally {
    favoriteLoading.value = false
  }
}

async function loadMessages() {
  messageLoading.value = true
  messageError.value = ''

  try {
    const data = await getProductMessages(route.params.id, messagePage.value, messageSize.value)
    messages.value = data.records || []
    messageTotal.value = Number(data.total || 0)
  } catch (err) {
    messageError.value = err.message
    messages.value = []
  } finally {
    messageLoading.value = false
  }
}

async function submitMessage() {
  const content = messageContent.value.trim()
  if (!content || messageSubmitting.value) return

  messageSubmitting.value = true
  messageError.value = ''

  try {
    await addProductMessage(route.params.id, content, replyParentId.value)
    messageContent.value = ''
    replyParentId.value = 0
    messagePage.value = 1
    await loadMessages()
  } catch (err) {
    messageError.value = err.message
  } finally {
    messageSubmitting.value = false
  }
}

function replyTo(message) {
  replyParentId.value = message.id
  messageComposer.value?.scrollIntoView({ behavior: 'smooth', block: 'center' })
}

function cancelReply() {
  replyParentId.value = 0
}

function goToMessagePage(target) {
  if (target < 1 || target > messageTotalPages.value) return
  messagePage.value = target
  loadMessages()
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

function formatMessageTime(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadDetail()
  loadFavoriteStatus()
  loadMessages()
})
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

    <template v-else-if="product">
      <div class="product-detail">
        <div class="media-panel">
          <img :src="product.imageUrl" :alt="product.title" />
        </div>

        <div class="info-panel">
          <div class="detail-meta">
            <span class="category-chip">{{ product.category }}</span>
            <span class="views"><Eye :size="16" /> {{ product.viewCount }} 次浏览</span>
            <button
              type="button"
              class="favorite-button"
              :class="{ active: favoriteStatus }"
              :disabled="favoriteLoading"
              @click="toggleFavorite"
            >
              <Heart :size="18" :fill="favoriteStatus ? 'currentColor' : 'none'" />
              {{ favoriteStatus ? '已收藏' : '收藏' }}
            </button>
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
          <p v-if="favoriteError" class="favorite-error">{{ favoriteError }}</p>
        </div>
      </div>

      <div class="message-section">
        <div class="message-heading">
          <div class="message-title">
            <MessageCircle :size="20" />
            <h2>留言互动</h2>
            <span>{{ messageTotal }} 条</span>
          </div>
        </div>

        <div ref="messageComposer" class="message-composer">
          <p v-if="replyParentId" class="reply-tip">
            正在回复留言 #{{ replyParentId }}
            <button type="button" @click="cancelReply">取消回复</button>
          </p>
          <textarea
            v-model="messageContent"
            maxlength="500"
            rows="3"
            placeholder="写下你的留言，友善交流"
          />
          <div class="composer-footer">
            <span>{{ messageContent.length }}/500</span>
            <button
              type="button"
              class="primary-button"
              :disabled="messageSubmitting || !messageContent.trim()"
              @click="submitMessage"
            >
              <Send :size="17" />
              发布留言
            </button>
          </div>
        </div>

        <div v-if="messageLoading" class="state-block message-state">
          <span class="spinner" />
          <p>正在加载留言</p>
        </div>

        <div v-else-if="messageError" class="state-block message-state error">
          <p>{{ messageError }}</p>
          <button type="button" class="primary-button" @click="loadMessages">重新加载</button>
        </div>

        <div v-else-if="messages.length === 0" class="state-block message-state">
          <MessageCircle :size="28" />
          <p>还没有留言，来发布第一条吧</p>
        </div>

        <div v-else class="message-list">
          <article
            v-for="message in messages"
            :key="message.id"
            class="message-item"
            :class="{ reply: message.parentId !== 0 }"
          >
            <div class="message-meta">
              <strong>用户 {{ message.userId }}</strong>
              <span>{{ formatMessageTime(message.createTime) }}</span>
              <span v-if="message.parentId !== 0">回复 #{{ message.parentId }}</span>
            </div>
            <p>{{ message.content }}</p>
            <button type="button" class="reply-button" @click="replyTo(message)">
              <CornerDownRight :size="15" />
              回复
            </button>
          </article>
        </div>

        <nav v-if="messages.length > 0" class="pagination" aria-label="留言分页">
          <button
            type="button"
            class="icon-button"
            :disabled="messagePage <= 1"
            @click="goToMessagePage(messagePage - 1)"
          >
            <ChevronLeft :size="18" />
          </button>
          <span>{{ messagePage }} / {{ messageTotalPages }}</span>
          <button
            type="button"
            class="icon-button"
            :disabled="messagePage >= messageTotalPages"
            @click="goToMessagePage(messagePage + 1)"
          >
            <ChevronRight :size="18" />
          </button>
        </nav>
      </div>
    </template>
  </section>
</template>
