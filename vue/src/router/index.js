import { createRouter, createWebHistory } from 'vue-router'
import ProductList from '../views/ProductList.vue'
import ProductDetail from '../views/ProductDetail.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'product-list',
      component: ProductList
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: ProductDetail
    }
  ]
})

export default router
