const API_BASE = '/api'

export async function getProductDistribution() {
  const response = await fetch(`${API_BASE}/products/distribution`)
  if (!response.ok) {
    throw new Error('网络请求失败')
  }

  const body = await response.json()
  if (body.code !== 200) {
    throw new Error(body.message || '请求失败')
  }

  return body.data
}
