<script setup>
import { computed, onMounted, ref } from 'vue'
import { LayoutDashboard } from 'lucide-vue-next'
import { getProductDistribution } from '../api/distribution'

const loading = ref(false)
const error = ref('')
const total = ref(0)
const categoryStats = ref([])
const priceStats = ref([])

const maxCategoryCount = computed(() =>
  Math.max(0, ...categoryStats.value.map((item) => Number(item.count || 0)))
)
const maxPriceCount = computed(() =>
  Math.max(0, ...priceStats.value.map((item) => Number(item.count || 0)))
)

function barHeight(count, max) {
  if (!max) return '0%'
  return `${Math.round((Number(count) / max) * 100)}%`
}

async function loadDistribution() {
  loading.value = true
  error.value = ''

  try {
    const data = await getProductDistribution()
    categoryStats.value = data.categoryStats || []
    priceStats.value = data.priceStats || []
    total.value = Number(data.total || 0)
  } catch (err) {
    error.value = err.message
    categoryStats.value = []
    priceStats.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadDistribution)
</script>

<template>
  <section class="catalog">
    <div class="catalog-heading">
      <div>
        <p class="eyebrow">Distribution</p>
        <h1>商品分布</h1>
      </div>
      <p class="count">{{ total }} 件在售商品</p>
    </div>

    <div v-if="loading" class="state-block">
      <span class="spinner" />
      <p>正在加载商品分布</p>
    </div>

    <div v-else-if="error" class="state-block error">
      <p>{{ error }}</p>
      <button type="button" class="primary-button" @click="loadDistribution">重新加载</button>
    </div>

    <div v-else class="chart-grid">
      <div class="chart-card">
        <div class="chart-title">
          <h2>按分类分布</h2>
          <span>{{ categoryStats.length }} 个分类</span>
        </div>
        <div v-if="categoryStats.length === 0" class="chart-empty">
          <LayoutDashboard :size="26" />
          <span>暂无分类数据</span>
        </div>
        <div v-else class="bar-chart">
          <div
            v-for="item in categoryStats"
            :key="item.categoryId"
            class="bar-column"
          >
            <span class="bar-value">{{ item.count }}</span>
            <div class="bar-track">
              <div
                class="bar bar-teal"
                :style="{ height: barHeight(item.count, maxCategoryCount) }"
              />
            </div>
            <span class="bar-label">{{ item.categoryName }}</span>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-title">
          <h2>按价格区间分布</h2>
          <span>{{ priceStats.length }} 个区间</span>
        </div>
        <div v-if="priceStats.length === 0" class="chart-empty">
          <LayoutDashboard :size="26" />
          <span>暂无价格数据</span>
        </div>
        <div v-else class="bar-chart">
          <div
            v-for="item in priceStats"
            :key="item.range"
            class="bar-column"
          >
            <span class="bar-value">{{ item.count }}</span>
            <div class="bar-track">
              <div
                class="bar bar-amber"
                :style="{ height: barHeight(item.count, maxPriceCount) }"
              />
            </div>
            <span class="bar-label">{{ item.range }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.chart-card {
  padding: 24px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.chart-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-title h2 {
  margin: 0;
  font-size: 18px;
}

.chart-title span {
  color: #6b7280;
  font-size: 14px;
}

.bar-chart {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  margin-top: 24px;
}

.bar-column {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.bar-value {
  color: #6b7280;
  font-size: 13px;
  font-weight: 600;
}

.bar-track {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  width: 100%;
  height: 160px;
  border-bottom: 1px solid #e5e7eb;
}

.bar {
  width: 100%;
  max-width: 56px;
  border-radius: 6px 6px 0 0;
  transition: height 300ms ease;
}

.bar-teal {
  background: linear-gradient(180deg, #14b8a6, #0f766e);
}

.bar-amber {
  background: linear-gradient(180deg, #fbbf24, #f59e0b);
}

.bar-label {
  color: #4b5563;
  font-size: 13px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.chart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 200px;
  color: #9ca3af;
  font-size: 14px;
}

@media (max-width: 760px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
