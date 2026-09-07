const API_BASE = '/api'

async function request(path, options) {
  const response = await fetch(`${API_BASE}${path}`, options)
  if (!response.ok) {
    throw new Error('网络请求失败')
  }

  const body = await response.json()
  if (body.code !== 200) {
    throw new Error(body.message || '请求失败')
  }

  return body.data
}

export function getFavoriteStatus(productId) {
  return request(`/favorites/status/${productId}`)
}

export function addFavorite(productId) {
  return request(`/favorites/product/${productId}`, {
    method: 'POST'
  })
}

export function removeFavorite(productId) {
  return request(`/favorites/product/${productId}`, {
    method: 'DELETE'
  })
}

export function getMyFavorites(page = 1, size = 10) {
  return request(`/favorites/mine?page=${page}&size=${size}`)
}
