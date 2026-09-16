const TOKEN_KEY = 'campus_token'
const USERNAME_KEY = 'campus_username'
const USER_ID_KEY = 'campus_user_id'
const ROLE_KEY = 'campus_role'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY)
}

export function setUsername(username) {
  localStorage.setItem(USERNAME_KEY, username)
}

export function clearUsername() {
  localStorage.removeItem(USERNAME_KEY)
}

export function getUserId() {
  const id = localStorage.getItem(USER_ID_KEY)
  return id ? Number(id) : null
}

export function setUserId(userId) {
  localStorage.setItem(USER_ID_KEY, String(userId))
}

export function clearUserId() {
  localStorage.removeItem(USER_ID_KEY)
}

export function getRole() {
  return localStorage.getItem(ROLE_KEY) || 'user'
}

export function setRole(role) {
  localStorage.setItem(ROLE_KEY, role || 'user')
}

export function clearRole() {
  localStorage.removeItem(ROLE_KEY)
}

export function isAdmin() {
  return getRole() === 'admin'
}

export function clearAuth() {
  clearToken()
  clearUsername()
  clearUserId()
  clearRole()
}

export function isLoggedIn() {
  return !!getToken()
}
