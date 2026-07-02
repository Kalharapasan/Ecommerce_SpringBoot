import { reactive } from 'vue';
import { decodeToken } from './jwt';

export const authState = reactive({
  token: localStorage.getItem('token') || null,
  user: decodeToken(localStorage.getItem('token')),
  cartCount: 0
});

export function isLoggedIn() {
  return !!authState.token;
}

export function getToken() {
  return authState.token;
}

export function getCurrentUser() {
  return authState.user;
}

export function updateCartCount(count) {
  authState.cartCount = count;
}

export function login(token) {
  localStorage.setItem('token', token);
  authState.token = token;
  authState.user = decodeToken(token);
  authState.cartCount = 0;
}

export function logout() {
  localStorage.removeItem('token');
  authState.token = null;
  authState.user = null;
  authState.cartCount = 0;
}
