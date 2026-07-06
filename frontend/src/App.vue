<template>
  <div :data-theme="theme">
    <!-- ═══ Premium Sticky Navbar ═══ -->
    <nav class="bf-navbar" :class="{ 'bf-navbar-scrolled': isScrolled }">
      <div class="container">
        <div class="bf-navbar-inner">
          <!-- Brand -->
          <router-link class="bf-navbar-brand" to="/">
            <span class="bf-brand-icon">⚡</span>
            <span class="bf-brand-text">ByteForge</span>
            <span class="bf-brand-tag">PC Store</span>
          </router-link>

          <!-- Mobile Toggle -->
          <button class="bf-navbar-toggle" @click="mobileMenuOpen = !mobileMenuOpen" :class="{ active: mobileMenuOpen }">
            <span></span><span></span><span></span>
          </button>

          <!-- Nav Links -->
          <div class="bf-navbar-menu" :class="{ 'bf-navbar-menu-open': mobileMenuOpen }">
            <div class="bf-navbar-nav">
              <router-link class="bf-nav-link" to="/product" @click="mobileMenuOpen = false">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M1 2.5A1.5 1.5 0 0 1 2.5 1h3A1.5 1.5 0 0 1 7 2.5v3A1.5 1.5 0 0 1 5.5 7h-3A1.5 1.5 0 0 1 1 5.5zm8 0A1.5 1.5 0 0 1 10.5 1h3A1.5 1.5 0 0 1 15 2.5v3A1.5 1.5 0 0 1 13.5 7h-3A1.5 1.5 0 0 1 9 5.5zm-8 8A1.5 1.5 0 0 1 2.5 9h3A1.5 1.5 0 0 1 7 10.5v3A1.5 1.5 0 0 1 5.5 15h-3A1.5 1.5 0 0 1 1 13.5zm8 0A1.5 1.5 0 0 1 10.5 9h3a1.5 1.5 0 0 1 1.5 1.5v3a1.5 1.5 0 0 1-1.5 1.5h-3A1.5 1.5 0 0 1 9 13.5z"/></svg>
                Products
              </router-link>
              <router-link v-if="isLoggedIn && role === 'ADMIN'" class="bf-nav-link" to="/admin/dashboard" @click="mobileMenuOpen = false">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M8 4a.5.5 0 0 1 .5.5V6a.5.5 0 0 1-1 0V4.5A.5.5 0 0 1 8 4M3.732 5.732a.5.5 0 0 1 .707 0l.915.914a.5.5 0 1 1-.708.708l-.914-.915a.5.5 0 0 1 0-.707M2 10a.5.5 0 0 1 .5-.5h1.586a.5.5 0 0 1 0 1H2.5A.5.5 0 0 1 2 10m9.5-.5a.5.5 0 0 0 0 1h1.586a.5.5 0 0 0 0-1zm.697-4.768a.5.5 0 0 1 0 .707l-.914.915a.5.5 0 1 1-.708-.708l.915-.914a.5.5 0 0 1 .707 0M8 1a7 7 0 0 0-4.95 11.95l.707.707A7 7 0 1 0 8 1"/></svg>
                Dashboard
              </router-link>
              <router-link v-if="isLoggedIn && role === 'SELLER'" class="bf-nav-link" to="/seller/dashboard" @click="mobileMenuOpen = false">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M2.97 1.35A1 1 0 0 1 3.73 1h8.54a1 1 0 0 1 .76.35l2.609 3.044A1.5 1.5 0 0 1 16 5.37v.255a2.375 2.375 0 0 1-4.25 1.458A2.37 2.37 0 0 1 9.875 8 2.37 2.37 0 0 1 8 7.083 2.37 2.37 0 0 1 6.125 8a2.37 2.37 0 0 1-1.875-.917A2.375 2.375 0 0 1 0 5.625V5.37a1.5 1.5 0 0 1 .361-.976zm1.78 4.275a1.375 1.375 0 0 0 2.75 0 .5.5 0 0 1 1 0 1.375 1.375 0 0 0 2.75 0 .5.5 0 0 1 1 0 1.375 1.375 0 1 0 2.75 0V5.37a.5.5 0 0 0-.12-.325L12.27 2H3.73L1.12 5.045A.5.5 0 0 0 1 5.37v.255a1.375 1.375 0 0 0 2.75 0 .5.5 0 0 1 1 0M1.5 8.5A.5.5 0 0 1 2 9v6h12V9a.5.5 0 0 1 1 0v6h.5a.5.5 0 0 1 0 1H.5a.5.5 0 0 1 0-1H1V9a.5.5 0 0 1 .5-.5m2 .5a.5.5 0 0 1 .5.5V13h8V9.5a.5.5 0 0 1 1 0V13a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9.5a.5.5 0 0 1 .5-.5"/></svg>
                Seller Hub
              </router-link>
            </div>

            <div class="bf-navbar-actions">
              <!-- Dark Mode Toggle -->
              <button class="bf-theme-toggle" @click="toggleTheme" :title="theme === 'dark' ? 'Switch to Light Mode' : 'Switch to Dark Mode'">
                <span v-if="theme === 'dark'" class="bf-theme-icon">☀️</span>
                <span v-else class="bf-theme-icon">🌙</span>
              </button>

              <!-- Guest Links -->
              <template v-if="!isLoggedIn">
                <router-link class="bf-btn bf-btn-ghost bf-btn-sm" to="/signIn" @click="mobileMenuOpen = false">Sign In</router-link>
                <router-link class="bf-btn bf-btn-primary bf-btn-sm" to="/signUp" @click="mobileMenuOpen = false">Sign Up</router-link>
              </template>

              <!-- Logged In Links -->
              <template v-else>
                <!-- Cart (not for admin) -->
                <router-link v-if="role !== 'ADMIN'" class="bf-nav-icon-btn" to="/cart" title="Cart" @click="mobileMenuOpen = false">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16"><path d="M0 1.5A.5.5 0 0 1 .5 1H2a.5.5 0 0 1 .485.379L2.89 3H14.5a.5.5 0 0 1 .491.592l-1.5 8A.5.5 0 0 1 13 12H4a.5.5 0 0 1-.491-.408L2.01 3.607 1.61 2H.5a.5.5 0 0 1-.5-.5M3.102 4l1.313 7h8.17l1.313-7zM5 12a2 2 0 1 0 0 4 2 2 0 0 0 0-4m7 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4m-7 1a1 1 0 1 1 0 2 1 1 0 0 1 0-2m7 0a1 1 0 1 1 0 2 1 1 0 0 1 0-2"/></svg>
                  <span v-if="cartCount > 0" class="bf-nav-badge">{{ cartCount }}</span>
                </router-link>

                <!-- User Menu -->
                <div class="bf-user-menu" @click="userMenuOpen = !userMenuOpen" v-click-away="closeUserMenu">
                  <div class="bf-user-avatar">
                    <span>{{ userInitial }}</span>
                  </div>
                  <div class="bf-user-info">
                    <span class="bf-user-name">{{ username }}</span>
                    <span class="bf-user-role bf-badge" :class="roleBadgeClass">{{ role }}</span>
                  </div>
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16" class="bf-chevron" :class="{ rotated: userMenuOpen }"><path d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708"/></svg>

                  <!-- Dropdown -->
                  <transition name="dropdown-fade">
                    <div v-if="userMenuOpen" class="bf-user-dropdown">
                      <router-link class="bf-dropdown-item" to="/profile" @click="closeUserMenu">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6"/></svg>
                        My Profile
                      </router-link>
                      <div class="bf-dropdown-divider"></div>
                      <button class="bf-dropdown-item bf-dropdown-danger" @click="handleLogout">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0z"/><path fill-rule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708z"/></svg>
                        Sign Out
                      </button>
                    </div>
                  </transition>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- ═══ Main Content ═══ -->
    <main class="bf-main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" :key="$route.fullPath" />
        </transition>
      </router-view>
    </main>

    <!-- ═══ Footer ═══ -->
    <footer class="bf-footer">
      <div class="container">
        <div class="bf-footer-inner">
          <div class="bf-footer-brand">
            <span class="bf-brand-icon">⚡</span>
            <span>ByteForge PC Store</span>
          </div>
          <p class="bf-footer-copy">&copy; {{ new Date().getFullYear() }} ByteForge. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <!-- ═══ Toast Notifications ═══ -->
    <ToastNotification position="top-right" />
  </div>
</template>

<script>
import { authState, logout } from './utils/auth';
import api from './utils/api';
import ToastNotification from './components/Common/ToastNotification.vue';

export default {
  name: 'App',
  components: { ToastNotification },
  data() {
    return {
      theme: localStorage.getItem('bf-theme') || 'light',
      isScrolled: false,
      mobileMenuOpen: false,
      userMenuOpen: false
    };
  },
  computed: {
    isLoggedIn() {
      return !!authState.token;
    },
    username() {
      return authState.user ? authState.user.username : '';
    },
    userInitial() {
      return this.username ? this.username.charAt(0).toUpperCase() : '?';
    },
    role() {
      return authState.user ? authState.user.role : '';
    },
    cartCount() {
      return authState.cartCount;
    },
    roleBadgeClass() {
      if (this.role === 'ADMIN') return 'bf-badge-danger';
      if (this.role === 'SELLER') return 'bf-badge-warning';
      return 'bf-badge-primary';
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
      this.userMenuOpen = false;
      this.mobileMenuOpen = false;
      this.$router.push({ name: 'SignIn' });
    },
    toggleTheme() {
      this.theme = this.theme === 'dark' ? 'light' : 'dark';
      localStorage.setItem('bf-theme', this.theme);
    },
    closeUserMenu() {
      this.userMenuOpen = false;
    },
    handleScroll() {
      this.isScrolled = window.scrollY > 20;
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
  directives: {
    'click-away': {
      mounted(el, binding) {
        el.__clickAway = (event) => {
          if (!el.contains(event.target)) {
            binding.value();
          }
        };
        document.addEventListener('click', el.__clickAway);
      },
      unmounted(el) {
        document.removeEventListener('click', el.__clickAway);
      }
    }
  },
  mounted() {
    this.fetchCartCount();
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  }
};
</script>

<style>
/* ─── App Global Styles ────────────────────────────────────── */
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.bf-main-content {
  flex: 1;
  padding-top: 80px; /* navbar height */
}

/* ─── Premium Navbar ───────────────────────────────────────── */
.bf-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 72px;
  background: var(--bf-bg-navbar);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  transition: all var(--bf-transition-base);
}

.bf-navbar-scrolled {
  height: 64px;
  background: rgba(15, 23, 42, 0.98);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.15);
}

.bf-navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  gap: 20px;
}

/* Brand */
.bf-navbar-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  flex-shrink: 0;
}

.bf-brand-icon {
  font-size: 1.5rem;
  filter: drop-shadow(0 0 8px rgba(99, 102, 241, 0.5));
}

.bf-brand-text {
  font-size: 1.25rem;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.02em;
}

.bf-brand-tag {
  font-size: 0.65rem;
  font-weight: 600;
  color: var(--bf-primary);
  background: rgba(99, 102, 241, 0.15);
  padding: 2px 8px;
  border-radius: var(--bf-radius-full);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Nav Links */
.bf-navbar-menu {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
}

.bf-navbar-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.bf-nav-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: rgba(255, 255, 255, 0.7);
  font-size: var(--bf-font-size-sm);
  font-weight: 500;
  text-decoration: none;
  border-radius: var(--bf-radius-md);
  transition: all var(--bf-transition-fast);
}

.bf-nav-link:hover,
.bf-nav-link.router-link-active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.08);
}

.bf-nav-link.router-link-exact-active {
  color: var(--bf-primary);
  background: rgba(99, 102, 241, 0.12);
}

/* Navbar Actions */
.bf-navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Theme Toggle */
.bf-theme-toggle {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: rgba(255, 255, 255, 0.06);
  border-radius: var(--bf-radius-md);
  cursor: pointer;
  transition: all var(--bf-transition-fast);
  font-size: 1.1rem;
}

.bf-theme-toggle:hover {
  background: rgba(255, 255, 255, 0.12);
  transform: scale(1.05);
}

.bf-theme-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

/* Icon Button (Cart, Notifications) */
.bf-nav-icon-btn {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--bf-radius-md);
  color: rgba(255, 255, 255, 0.75);
  transition: all var(--bf-transition-fast);
  text-decoration: none;
}

.bf-nav-icon-btn:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.08);
}

.bf-nav-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--bf-danger);
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  border-radius: var(--bf-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

/* User Menu */
.bf-user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px 6px 6px;
  border-radius: var(--bf-radius-lg);
  cursor: pointer;
  transition: all var(--bf-transition-fast);
  position: relative;
  user-select: none;
}

.bf-user-menu:hover {
  background: rgba(255, 255, 255, 0.06);
}

.bf-user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--bf-gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 0.875rem;
  flex-shrink: 0;
}

.bf-user-info {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.bf-user-name {
  color: #ffffff;
  font-size: var(--bf-font-size-sm);
  font-weight: 600;
}

.bf-user-role {
  font-size: 0.6rem !important;
  padding: 1px 6px !important;
  align-self: flex-start;
}

.bf-chevron {
  color: rgba(255, 255, 255, 0.5);
  transition: transform 0.2s ease;
}

.bf-chevron.rotated {
  transform: rotate(180deg);
}

/* User Dropdown */
.bf-user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  min-width: 200px;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  box-shadow: var(--bf-shadow-xl);
  overflow: hidden;
  z-index: 1001;
}

.bf-dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: var(--bf-font-size-sm);
  color: var(--bf-text-secondary);
  text-decoration: none;
  transition: all var(--bf-transition-fast);
  border: none;
  background: none;
  width: 100%;
  cursor: pointer;
  font-family: var(--bf-font-family);
}

.bf-dropdown-item:hover {
  background: var(--bf-primary-light);
  color: var(--bf-primary);
}

.bf-dropdown-danger:hover {
  background: var(--bf-danger-light);
  color: var(--bf-danger);
}

.bf-dropdown-divider {
  height: 1px;
  background: var(--bf-border);
  margin: 4px 0;
}

/* Dropdown transition */
.dropdown-fade-enter-active {
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.dropdown-fade-leave-active {
  transition: all 0.15s ease-in;
}
.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}

/* ─── Mobile Toggle ────────────────────────────────────────── */
.bf-navbar-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: 8px;
  background: none;
  border: none;
  cursor: pointer;
}

.bf-navbar-toggle span {
  display: block;
  width: 22px;
  height: 2px;
  background: #fff;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.bf-navbar-toggle.active span:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}
.bf-navbar-toggle.active span:nth-child(2) {
  opacity: 0;
}
.bf-navbar-toggle.active span:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

/* ─── Footer ───────────────────────────────────────────────── */
.bf-footer {
  background: var(--bf-bg-navbar);
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  padding: 24px 0;
  margin-top: auto;
}

.bf-footer-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.bf-footer-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 700;
  font-size: var(--bf-font-size-sm);
}

.bf-footer-copy {
  color: rgba(255, 255, 255, 0.4);
  font-size: var(--bf-font-size-xs);
  margin: 0;
}

/* ─── Responsive ───────────────────────────────────────────── */
@media (max-width: 768px) {
  .bf-navbar-toggle {
    display: flex;
  }

  .bf-navbar-menu {
    position: fixed;
    top: 72px;
    left: 0;
    right: 0;
    bottom: 0;
    background: var(--bf-bg-navbar);
    flex-direction: column;
    padding: 24px;
    gap: 24px;
    transform: translateX(100%);
    transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    overflow-y: auto;
  }

  .bf-navbar-menu-open {
    transform: translateX(0);
  }

  .bf-navbar-nav {
    flex-direction: column;
    width: 100%;
  }

  .bf-nav-link {
    width: 100%;
    padding: 14px 16px;
    font-size: var(--bf-font-size-base);
  }

  .bf-navbar-actions {
    flex-wrap: wrap;
    justify-content: center;
  }

  .bf-user-info {
    display: none;
  }

  .bf-footer-inner {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>
