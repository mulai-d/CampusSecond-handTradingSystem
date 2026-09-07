const API_BASE = '/api'

async function request(path) {
  const response = await fetch(`${API_BASE}${path}`)
  if (!response.ok) {
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
