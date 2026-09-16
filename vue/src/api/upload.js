import { getToken } from '../utils/auth'

const API_BASE = '/api'

export async function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)

  const headers = {}
  const token = getToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${API_BASE}/upload`, {
    method: 'POST',
    headers,
    body: formData
  })

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error('请先登录')
    }
    throw new Error('上传失败')
  }

  const body = await response.json()
  if (body.code !== 200) {
    throw new Error(body.message || '上传失败')
  }

  return body.data
}
