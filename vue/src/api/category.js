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
    throw new Error('网络请求失败')
  }

  const body = await response.json()
  if (body.code !== 200) {
    throw new Error(body.message || '请求失败')
  }

  return body.data
}

export function getCategoryList() {
  return request('/categories')
}

export function addCategory(data) {
  return request('/categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
}

export function updateCategory(id, data) {
  return request(`/categories/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
}

export function deleteCategory(id) {
  return request(`/categories/${id}`, {
    method: 'DELETE'
  })
}
