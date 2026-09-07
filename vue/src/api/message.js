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

export function getProductMessages(productId, page = 1, size = 20) {
  return request(`/messages/product/${productId}?page=${page}&size=${size}`)
}

export function addProductMessage(productId, content, parentId = 0) {
  return request(`/messages/product/${productId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      content,
      parentId
    })
  })
}
