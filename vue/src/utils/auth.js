const TOKEN_KEY = 'campus_token'
const USERNAME_KEY = 'campus_username'

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

export function clearAuth() {
  clearToken()
  clearUsername()
}

export function isLoggedIn() {
  return !!getToken()
}
