<template>
  <div>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
      <div class="container-fluid container">
        <router-link class="navbar-brand font-weight-bold" to="/">E-Shop</router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/product">Products</router-link>
            </li>
            <li class="nav-item" v-if="isLoggedIn && role === 'ADMIN'">
              <router-link class="nav-link" to="/admin/category">Categories</router-link>
            </li>
          </ul>
          <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
            <!-- Logged Out Links -->
            <template v-if="!isLoggedIn">
              <li class="nav-item">
                <router-link class="nav-link" to="/signIn">Sign In</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link" to="/signUp">Sign Up</router-link>
              </li>
            </template>
            <!-- Logged In Links -->
            <template v-else>
              <li class="nav-item">
                <span class="nav-link text-light me-2 d-flex align-items-center">
                  Welcome, <strong>{{ username }}</strong>
                  <span class="badge bg-secondary ms-2 text-uppercase" style="font-size: 0.7rem;">{{ role }}</span>
                </span>
              </li>
              <li class="nav-item">
                <router-link class="nav-link text-decoration-none text-light opacity-75 hover-opacity-100 me-3" to="/profile">Profile</router-link>
              </li>
              <li class="nav-item">
                <router-link class="nav-link text-decoration-none text-light opacity-75 hover-opacity-100 me-3 position-relative d-flex align-items-center" to="/cart">
                  Cart
                  <span v-if="cartCount > 0" class="badge rounded-pill bg-danger ms-2" style="font-size: 0.75rem;">
                    {{ cartCount }}
                  </span>
                </router-link>
              </li>
              <li class="nav-item">
                <button class="btn btn-outline-light btn-sm" @click="handleLogout">Logout</button>
              </li>
            </template>
          </ul>
        </div>
      </div>
    </nav>
    <router-view/>
  </div>
</template>

<script>
import { authState, logout } from './utils/auth';
import api from './utils/api';

export default {
  name: 'App',
  computed: {
    isLoggedIn() {
      return !!authState.token;
    },
    username() {
      return authState.user ? authState.user.username : '';
    },
    role() {
      return authState.user ? authState.user.role : '';
    },
    cartCount() {
      return authState.cartCount;
    }
  },
  watch: {
    isLoggedIn(newVal) {
      if (newVal) {
        this.fetchCartCount();
      }
    }
  },
  methods: {
    handleLogout() {
      logout();
      this.$router.push({ name: 'SignIn' });
    },
    async fetchCartCount() {
      if (!this.isLoggedIn) return;
      try {
        const token = authState.token;
        const response = await api.get(`/cart/${token}`);
        const items = response.data.data || [];
        const count = items.reduce((sum, item) => sum + item.count, 0);
        authState.cartCount = count;
      } catch (err) {
        console.warn('Failed to fetch initial cart count:', err);
      }
    }
  },
  mounted() {
    this.fetchCartCount();
  }
}
</script>

<style>
#app {
  font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
  background-color: #f8f9fa;
}
.hover-opacity-100:hover {
  opacity: 1 !important;
}
</style>
