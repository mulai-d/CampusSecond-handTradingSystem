<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Save, Upload } from 'lucide-vue-next'
import { createProduct } from '../api/product'
import { getCategoryList } from '../api/category'
import { uploadFile } from '../api/upload'
import { isLoggedIn } from '../utils/auth'

const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const error = ref('')
const success = ref('')

const form = ref({
  title: '',
  description: '',
  categoryId: null,
  price: '',
  imageUrl: '',
  status: 1
})

const categories = ref([])
const uploading = ref(false)
const uploadError = ref('')

async function loadCategories() {
  if (!isLoggedIn()) {
    router.push('/login?redirect=/products/publish')
    return
  }

  loading.value = true
  error.value = ''
  try {
    categories.value = await getCategoryList() || []
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

async function handleUpload(event) {
  const file = event.target.files?.[0]
  if (!file) return

  uploading.value = true
  uploadError.value = ''
  try {
    const url = await uploadFile(file)
    form.value.imageUrl = url
  } catch (err) {
    uploadError.value = err.message
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

async function submit() {
  if (saving.value) return

  const title = form.value.title.trim()
  const price = Number(form.value.price)
  if (!title) {
    error.value = '请输入商品标题'
    return
  }
  if (!form.value.categoryId) {
    error.value = '请选择商品分类'
    return
  }
  if (!price || price <= 0) {
    error.value = '请输入有效的价格'
    return
  }

  saving.value = true
  error.value = ''
  success.value = ''
  try {
    const product = await createProduct({
      title,
      description: form.value.description,
      categoryId: Number(form.value.categoryId),
      price,
      imageUrl: form.value.imageUrl,
      status: form.value.status
    })
    success.value = '发布成功'
    setTimeout(() => router.push(`/products/${product.id}`), 800)
  } catch (err) {
    error.value = err.message
  } finally {
    saving.value = false
  }
}

onMounted(loadCategories)
</script>

<template>
  <section class="edit-page">
    <button type="button" class="back-btn" @click="router.back()">
      <ArrowLeft :size="18" />
      <span>返回</span>
    </button>

    <div class="edit-card">
      <h1>发布商品</h1>

      <div v-if="loading" class="state-block">
        <span class="spinner" />
        <p>加载中...</p>
      </div>

      <form v-else class="edit-form" @submit.prevent="submit">
        <label class="field">
          <span>商品标题</span>
          <input v-model="form.title" type="text" placeholder="请输入商品标题" maxlength="100" />
        </label>

        <label class="field">
          <span>商品描述</span>
          <textarea v-model="form.description" rows="4" placeholder="请输入商品描述" maxlength="500" />
        </label>

        <label class="field">
          <span>商品分类</span>
          <select v-model="form.categoryId">
            <option :value="null" disabled>请选择分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </label>

        <label class="field">
          <span>价格（元）</span>
          <input v-model="form.price" type="number" step="0.01" min="0" placeholder="请输入价格" />
        </label>

        <div class="field">
          <span>商品图片</span>
          <div class="image-uploader">
            <div v-if="form.imageUrl" class="image-preview">
              <img :src="form.imageUrl" alt="商品图片" />
            </div>
            <label class="upload-btn">
              <Upload :size="16" />
              <span>{{ uploading ? '上传中...' : '上传图片' }}</span>
              <input type="file" accept="image/*" @change="handleUpload" :disabled="uploading" hidden />
            </label>
          </div>
          <p v-if="uploadError" class="field-error">{{ uploadError }}</p>
          <p v-if="form.imageUrl" class="image-url">{{ form.imageUrl }}</p>
        </div>

        <label class="field field-checkbox">
          <span>上架状态</span>
          <label class="switch">
            <input v-model="form.status" type="checkbox" :true-value="1" :false-value="0" />
            <span>{{ form.status === 1 ? '在售' : '已下架' }}</span>
          </label>
        </label>

        <p v-if="error" class="form-error">{{ error }}</p>
        <p v-if="success" class="form-success">{{ success }}</p>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="router.back()">取消</button>
          <button type="submit" class="save-btn" :disabled="saving">
            <Save :size="16" />
            {{ saving ? '发布中...' : '发布' }}
          </button>
        </div>
      </form>
    </div>
  </section>
</template>

<style scoped>
.edit-page {
  max-width: 640px;
  margin: 0 auto;
  padding: 24px 16px 48px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 16px;
  padding: 8px 14px;
  color: #4b5563;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
}

.back-btn:hover {
  color: #0f766e;
  border-color: #0f766e;
}

.edit-card {
  padding: 28px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
}

.edit-card h1 {
  margin: 0 0 24px;
  font-size: 22px;
  color: #1f2937;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  color: #4b5563;
}

.field input,
.field textarea,
.field select {
  padding: 10px 12px;
  color: #1f2937;
  background: #f9fafb;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  outline: none;
  font: inherit;
}

.field textarea {
  resize: vertical;
}

.field input:focus,
.field textarea:focus,
.field select:focus {
  border-color: #0f766e;
  box-shadow: 0 0 0 3px rgba(15, 118, 110, 0.12);
}

.image-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

.image-preview {
  width: 96px;
  height: 96px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: #0f766e;
  background: #f0fdfa;
  border: 1px dashed #0f766e;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.upload-btn:hover {
  background: #ccfbf1;
}

.image-url {
  margin: 0;
  font-size: 12px;
  color: #9ca3af;
  word-break: break-all;
}

.field-error {
  margin: 0;
  color: #b91c1c;
  font-size: 12px;
}

.field-checkbox {
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.switch {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.form-error {
  margin: 0;
  color: #b91c1c;
  font-size: 13px;
}

.form-success {
  margin: 0;
  color: #0f766e;
  font-size: 13px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.cancel-btn {
  padding: 10px 20px;
  color: #4b5563;
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-weight: 600;
}

.save-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  color: #ffffff;
  background: #0f766e;
  border: 0;
  border-radius: 8px;
  font-weight: 600;
}

.save-btn:hover:not(:disabled) {
  background: #115e59;
}
</style>
