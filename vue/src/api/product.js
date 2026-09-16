import { getToken } from '../utils/auth'

const API_BASE = '/api'

async function request(path, options = {}) {
  const headers = { ...(options.headers || {}) }
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!response.ok) {
    if (response.status === 401) {
      throw new Error('请先登录')
    }
    throw new Error('网络请求失败')
  }

  const body = await response.json()
  if (body.code !== 200) {
    throw new Error(body.message || '请求失败')
  }

  return body.data
}

export function getProductList({ page = 1, size = 10, keyword = '', category = '' } = {}) {
  const params = new URLSearchParams({
    page,
    size,
    keyword,
    category
  })
  return request(`/products?${params.toString()}`)
}

export function getProductDetail(id) {
  return request(`/products/${id}`)
}

export function updateProduct(id, data) {
  return request(`/products/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
}

export function createProduct(data) {
  return request('/products', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
}
