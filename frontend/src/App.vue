<template>
  <div :data-theme="theme">
    <!-- ═══ Premium Sticky Glass Navbar ═══ -->
    <nav class="bf-navbar" :class="{ 'bf-navbar-scrolled': isScrolled }" @mouseleave="activeMegaMenu = null">
      <div class="container-fluid px-4">
        <div class="bf-navbar-inner">
          
          <!-- Left: Brand & Mobile Toggle -->
          <div class="d-flex align-items-center gap-4">
            <button class="bf-navbar-toggle" @click="mobileMenuOpen = !mobileMenuOpen" :class="{ active: mobileMenuOpen }">
              <span></span><span></span><span></span>
            </button>
            
            <router-link class="bf-navbar-brand" to="/">
              <div class="bf-brand-icon-wrapper">
                <span class="bf-brand-icon">⚡</span>
              </div>
              <div class="bf-brand-text-group d-none d-sm-flex">
                <span class="bf-brand-text">ByteForge</span>
                <span class="bf-brand-tag">Premium</span>
              </div>
            </router-link>

            <!-- Desktop Navigation (Mega Menu Triggers) -->
            <div class="bf-desktop-nav d-none d-lg-flex">
              <div 
                class="bf-nav-item" 
                @mouseenter="activeMegaMenu = 'hardware'"
              >
                <span>Hardware <i class="bi bi-chevron-down ms-1 small"></i></span>
              </div>
              <div 
                class="bf-nav-item" 
                @mouseenter="activeMegaMenu = 'peripherals'"
              >
                <span>Peripherals <i class="bi bi-chevron-down ms-1 small"></i></span>
              </div>
              <router-link class="bf-nav-item" to="/product" @mouseenter="activeMegaMenu = null">
                Explore All
              </router-link>
            </div>
          </div>

          <!-- Center: Animated Smart Search -->
          <div class="bf-navbar-search d-none d-md-block">
            <form class="bf-search-form" @submit.prevent="handleSearch">
              <i class="bi bi-search bf-search-icon"></i>
              <input 
                type="text" 
                v-model="searchQuery" 
                placeholder="Search for RTX 4090, Ryzen 9, Monitors..." 
                class="bf-search-input"
              />
              <button type="submit" class="bf-search-btn">Search</button>
            </form>
          </div>

          <!-- Right: Actions & User Profile -->
          <div class="bf-navbar-actions">
            <!-- Theme Toggle -->
            <button class="bf-icon-btn d-none d-sm-flex" @click="toggleTheme" :title="theme === 'dark' ? 'Light Mode' : 'Dark Mode'">
              <i class="bi" :class="theme === 'dark' ? 'bi-sun-fill text-warning' : 'bi-moon-stars-fill'"></i>
            </button>

            <!-- Guest Links -->
            <template v-if="!isLoggedIn">
              <router-link class="bf-btn-ghost d-none d-sm-flex" to="/signIn">Sign In</router-link>
              <router-link class="bf-btn-premium bf-btn-sm" to="/signUp">Create Account</router-link>
            </template>

            <!-- Logged In Actions -->
            <template v-else>
              <!-- Wishlist -->
              <router-link v-if="role !== 'ADMIN'" class="bf-icon-btn hover-glow" to="/profile" title="Wishlist">
                <i class="bi bi-heart"></i>
              </router-link>

              <!-- Cart with Preview -->
              <div v-if="role !== 'ADMIN'" class="bf-cart-wrapper" @mouseenter="cartPreviewOpen = true" @mouseleave="cartPreviewOpen = false">
                <router-link class="bf-icon-btn hover-glow bf-cart-btn" to="/cart">
                  <i class="bi bi-cart3"></i>
                  <span v-if="cartCount > 0" class="bf-badge-notification animate-pulse-glow">{{ cartCount }}</span>
                </router-link>
              </div>

              <!-- Admin/Seller Dashboards -->
              <router-link v-if="role === 'ADMIN'" class="bf-icon-btn text-danger" to="/admin/dashboard" title="Admin Panel">
                <i class="bi bi-speedometer2"></i>
              </router-link>
              <router-link v-if="role === 'SELLER'" class="bf-icon-btn text-warning" to="/seller/dashboard" title="Seller Hub">
                <i class="bi bi-shop"></i>
              </router-link>

              <!-- User Avatar Dropdown -->
              <div class="bf-user-menu" @click="userMenuOpen = !userMenuOpen" v-click-away="closeUserMenu">
                <div class="bf-avatar">
                  {{ userInitial }}
                </div>
                <transition name="dropdown-fade">
                  <div v-if="userMenuOpen" class="bf-dropdown-menu bf-glass-strong">
                    <div class="bf-dropdown-header">
                      <strong>{{ username }}</strong>
                      <span class="bf-role-badge" :class="roleBadgeClass">{{ role }}</span>
                    </div>
                    <div class="bf-divider"></div>
                    <router-link class="bf-dropdown-item" to="/profile" @click="closeUserMenu">
                      <i class="bi bi-person"></i> My Profile
                    </router-link>
                    <router-link class="bf-dropdown-item" to="/profile" @click="closeUserMenu">
                      <i class="bi bi-box-seam"></i> My Orders
                    </router-link>
                    <div class="bf-divider"></div>
                    <button class="bf-dropdown-item text-danger" @click="handleLogout">
                      <i class="bi bi-box-arrow-right"></i> Sign Out
                    </button>
                  </div>
                </transition>
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- Mega Menu Dropdown (Hardware) -->
      <transition name="mega-menu-fade">
        <div v-if="activeMegaMenu === 'hardware'" class="bf-mega-menu bf-glass-strong" @mouseleave="activeMegaMenu = null">
          <div class="container">
            <div class="row py-4">
              <div class="col-md-3">
                <h6 class="mega-menu-title">Processors (CPUs)</h6>
                <ul class="mega-menu-list">
                  <li><a href="#">Intel Core i9 / i7</a></li>
                  <li><a href="#">AMD Ryzen 9 / 7</a></li>
                  <li><a href="#">Server CPUs</a></li>
                  <li><a href="#">Cooling Solutions</a></li>
                </ul>
              </div>
              <div class="col-md-3">
                <h6 class="mega-menu-title">Graphics (GPUs)</h6>
                <ul class="mega-menu-list">
                  <li><a href="#">NVIDIA RTX 40 Series</a></li>
                  <li><a href="#">AMD Radeon RX 7000</a></li>
                  <li><a href="#">Workstation GPUs</a></li>
                  <li><a href="#">eGPUs</a></li>
                </ul>
              </div>
              <div class="col-md-3">
                <h6 class="mega-menu-title">Motherboards & RAM</h6>
                <ul class="mega-menu-list">
                  <li><a href="#">DDR5 Memory</a></li>
                  <li><a href="#">ATX / Micro-ATX</a></li>
                  <li><a href="#">Z790 / X670 Boards</a></li>
                  <li><a href="#">RGB Components</a></li>
                </ul>
              </div>
              <div class="col-md-3 mega-menu-promo">
                <div class="promo-card bf-glass">
                  <span>RTX 4090 in Stock</span>
                  <button class="bf-btn-premium bf-btn-sm mt-2">Shop Now</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </nav>

    <!-- Mobile Navigation Sidebar -->
    <transition name="mobile-menu-slide">
      <div v-if="mobileMenuOpen" class="bf-mobile-menu bf-glass-strong">
        <div class="p-4">
          <form class="bf-search-form mb-4" @submit.prevent="handleSearch">
            <input type="text" v-model="searchQuery" placeholder="Search..." class="bf-search-input" />
          </form>
          <div class="d-flex flex-column gap-3">
            <router-link to="/" class="bf-mobile-link" @click="mobileMenuOpen = false">Home</router-link>
            <router-link to="/product" class="bf-mobile-link" @click="mobileMenuOpen = false">All Products</router-link>
            <router-link v-if="!isLoggedIn" to="/signIn" class="bf-mobile-link" @click="mobileMenuOpen = false">Sign In</router-link>
          </div>
        </div>
      </div>
    </transition>

    <!-- ═══ Main Content ═══ -->
    <main class="bf-main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" :key="$route.fullPath" />
        </transition>
      </router-view>
    </main>

    <!-- ═══ Premium Footer ═══ -->
    <footer class="bf-footer">
      <div class="container">
        <div class="row g-4 mb-5">
          <div class="col-lg-4 col-md-6">
            <div class="bf-navbar-brand mb-3">
              <span class="bf-brand-icon">⚡</span>
              <span class="bf-brand-text">ByteForge</span>
            </div>
            <p class="text-muted small">
              The ultimate destination for premium PC hardware, custom builds, and enthusiast gear. Join the next generation of gamers and creators.
            </p>
            <div class="d-flex gap-3 mt-4">
              <a href="#" class="bf-social-icon"><i class="bi bi-twitter-x"></i></a>
              <a href="#" class="bf-social-icon"><i class="bi bi-discord"></i></a>
              <a href="#" class="bf-social-icon"><i class="bi bi-youtube"></i></a>
              <a href="#" class="bf-social-icon"><i class="bi bi-instagram"></i></a>
            </div>
          </div>
          <div class="col-lg-2 col-md-6 col-6">
            <h6 class="footer-title">Products</h6>
            <ul class="footer-links">
              <li><a href="#">Components</a></li>
              <li><a href="#">Peripherals</a></li>
              <li><a href="#">Pre-built PCs</a></li>
              <li><a href="#">Laptops</a></li>
            </ul>
          </div>
          <div class="col-lg-2 col-md-6 col-6">
            <h6 class="footer-title">Support</h6>
            <ul class="footer-links">
              <li><a href="#">Help Center</a></li>
              <li><a href="#">Returns & Warranty</a></li>
              <li><a href="#">Track Order</a></li>
              <li><a href="#">Contact Us</a></li>
            </ul>
          </div>
          <div class="col-lg-4 col-md-6">
            <h6 class="footer-title">Subscribe to Newsletter</h6>
            <p class="text-muted small">Get exclusive deals and early access to hardware drops.</p>
            <form class="footer-newsletter mt-3">
              <input type="email" placeholder="Enter your email" required />
              <button type="submit"><i class="bi bi-arrow-right"></i></button>
            </form>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; {{ new Date().getFullYear() }} ByteForge. All rights reserved.</p>
          <div class="footer-bottom-links">
            <a href="#">Privacy Policy</a>
            <a href="#">Terms of Service</a>
          </div>
        </div>
      </div>
    </footer>

    <ToastNotification position="bottom-right" />
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
      theme: 'dark', // Forced dark theme for premium feel
      isScrolled: false,
      mobileMenuOpen: false,
      userMenuOpen: false,
      cartPreviewOpen: false,
      activeMegaMenu: null,
      searchQuery: ''
    };
  },
  computed: {
    isLoggedIn() { return !!authState.token; },
    username() { return authState.user ? authState.user.username : ''; },
    userInitial() { return this.username ? this.username.charAt(0).toUpperCase() : '?'; },
    role() { return authState.user ? authState.user.role : ''; },
    cartCount() { return authState.cartCount; },
    roleBadgeClass() {
      if (this.role === 'ADMIN') return 'badge bg-danger bg-opacity-25 text-danger';
      if (this.role === 'SELLER') return 'badge bg-warning bg-opacity-25 text-warning';
      return 'badge bg-primary bg-opacity-25 text-primary';
    }
  },
  watch: {
    isLoggedIn(newVal) { if (newVal) this.fetchCartCount(); },
    theme: {
      immediate: true,
      handler(newVal) { document.documentElement.setAttribute('data-theme', newVal); }
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
    closeUserMenu() { this.userMenuOpen = false; },
    handleScroll() { this.isScrolled = window.scrollY > 20; },
    handleSearch() {
      if (this.searchQuery.trim()) {
        this.$router.push({ path: '/product', query: { q: this.searchQuery } });
        this.searchQuery = '';
      }
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
          if (!el.contains(event.target)) { binding.value(); }
        };
        document.addEventListener('click', el.__clickAway);
      },
      unmounted(el) { document.removeEventListener('click', el.__clickAway); }
    }
  },
  mounted() {
    // Inject Bootstrap Icons if not present
    if (!document.getElementById('bootstrap-icons')) {
      const link = document.createElement('link');
      link.id = 'bootstrap-icons';
      link.rel = 'stylesheet';
      link.href = 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css';
      document.head.appendChild(link);
    }
    
    this.fetchCartCount();
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  }
};
</script>

<style>
/* ─── App Global Layout ────────────────────────────────────── */
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.bf-main-content {
  flex: 1;
  padding-top: 80px; 
}

/* ─── Premium Glass Navbar ─────────────────────────────────── */
.bf-navbar {
  position: fixed;
  top: 0; left: 0; right: 0;
  z-index: 1000;
  height: 80px;
  background: rgba(11, 15, 25, 0.4);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.bf-navbar-scrolled {
  height: 64px;
  background: rgba(11, 15, 25, 0.85);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.5);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.bf-navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

/* Brand */
.bf-navbar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
}

.bf-brand-icon-wrapper {
  width: 40px; height: 40px;
  background: var(--bf-gradient-primary);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: var(--bf-shadow-glow-purple);
}

.bf-brand-icon {
  font-size: 1.2rem;
  color: white;
}

.bf-brand-text-group {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.bf-brand-text {
  font-size: 1.3rem;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.5px;
}

.bf-brand-tag {
  font-size: 0.65rem;
  font-weight: 700;
  color: var(--bf-cyan);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Desktop Nav Links */
.bf-desktop-nav {
  margin-left: 2rem;
  gap: 24px;
}

.bf-nav-item {
  color: var(--bf-text-secondary);
  font-weight: 500;
  font-size: 0.95rem;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s ease;
  padding: 10px 0;
}

.bf-nav-item:hover {
  color: var(--bf-cyan);
}

/* Mega Menu */
.bf-mega-menu {
  position: absolute;
  top: 100%; left: 0; right: 0;
  background: rgba(17, 24, 39, 0.98);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  box-shadow: var(--bf-shadow-xl);
  transform-origin: top;
}

.mega-menu-fade-enter-active, .mega-menu-fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.mega-menu-fade-enter-from, .mega-menu-fade-leave-to {
  opacity: 0; transform: translateY(-10px);
}

.mega-menu-title {
  color: #fff;
  font-weight: 700;
  margin-bottom: 16px;
  font-size: 1.1rem;
}

.mega-menu-list {
  list-style: none; padding: 0; margin: 0;
  display: flex; flex-direction: column; gap: 12px;
}

.mega-menu-list a {
  color: var(--bf-text-muted);
  text-decoration: none;
  transition: color 0.2s ease;
  font-size: 0.95rem;
}
.mega-menu-list a:hover { color: var(--bf-cyan); }

.mega-menu-promo .promo-card {
  padding: 24px;
  border-radius: 16px;
  display: flex; flex-direction: column; align-items: flex-start; justify-content: center;
  height: 100%;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(124, 58, 237, 0.1) 100%);
  border: 1px solid rgba(124, 58, 237, 0.2);
}
.promo-card span { font-weight: 700; color: #fff; font-size: 1.2rem; }

/* Animated Search */
.bf-navbar-search {
  flex: 0 1 400px;
}

.bf-search-form {
  position: relative;
  display: flex; align-items: center;
}

.bf-search-input {
  width: 100%;
  padding: 10px 100px 10px 45px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.bf-search-input:focus {
  outline: none;
  border-color: var(--bf-cyan);
  background: rgba(0, 0, 0, 0.5);
  box-shadow: 0 0 15px rgba(6, 182, 212, 0.2);
}

.bf-search-icon {
  position: absolute; left: 16px;
  color: var(--bf-text-muted);
}

.bf-search-btn {
  position: absolute; right: 4px;
  background: var(--bf-gradient-primary);
  color: white; border: none;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 0.85rem; font-weight: 600;
  transition: opacity 0.2s;
}
.bf-search-btn:hover { opacity: 0.9; }

/* Actions */
.bf-navbar-actions {
  display: flex; align-items: center; gap: 16px;
}

.bf-icon-btn {
  width: 40px; height: 40px;
  border-radius: 50%; border: none; background: transparent;
  color: var(--bf-text-secondary);
  display: flex; align-items: center; justify-content: center;
  font-size: 1.2rem;
  transition: all 0.2s ease; text-decoration: none; cursor: pointer;
}
.bf-icon-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.bf-cart-wrapper { position: relative; }
.bf-cart-btn { position: relative; }

.bf-badge-notification {
  position: absolute; top: 0px; right: -2px;
  background: var(--bf-danger); color: #fff;
  font-size: 0.65rem; font-weight: 800;
  width: 18px; height: 18px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%; border: 2px solid var(--bf-bg-navbar);
}

/* Avatar Menu */
.bf-user-menu { position: relative; cursor: pointer; }
.bf-avatar {
  width: 40px; height: 40px; border-radius: 50%;
  background: var(--bf-gradient-accent); color: white;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 1.1rem;
  box-shadow: 0 0 10px rgba(124, 58, 237, 0.3);
}

.bf-dropdown-menu {
  position: absolute; top: 120%; right: 0;
  width: 240px; padding: 8px 0;
}
.bf-dropdown-header {
  padding: 12px 20px; display: flex; flex-direction: column; gap: 4px;
}
.bf-role-badge { font-size: 0.7rem; align-self: flex-start; }
.bf-divider { height: 1px; background: rgba(255,255,255,0.1); margin: 8px 0; }
.bf-dropdown-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 20px; color: var(--bf-text-secondary);
  text-decoration: none; border: none; background: transparent; width: 100%; text-align: left;
  transition: all 0.2s ease;
}
.bf-dropdown-item:hover { background: rgba(255,255,255,0.05); color: #fff; padding-left: 24px; }

/* Mobile Menu */
.bf-navbar-toggle { display: none; background: none; border: none; flex-direction: column; gap: 5px; padding: 5px; }
.bf-navbar-toggle span { width: 24px; height: 2px; background: #fff; transition: 0.3s; }
.bf-mobile-menu { position: fixed; top: 72px; left: 0; right: 0; bottom: 0; z-index: 999; }
.bf-mobile-link { display: block; padding: 16px; color: #fff; text-decoration: none; font-size: 1.1rem; border-bottom: 1px solid rgba(255,255,255,0.05); }

/* Footer */
.bf-footer {
  background: var(--bf-bg-secondary);
  padding: 80px 0 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: auto;
}
.footer-title { color: #fff; margin-bottom: 24px; font-weight: 700; }
.footer-links { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 12px; }
.footer-links a { color: var(--bf-text-muted); text-decoration: none; transition: 0.2s; }
.footer-links a:hover { color: var(--bf-cyan); padding-left: 5px; }
.bf-social-icon { width: 40px; height: 40px; border-radius: 50%; background: rgba(255,255,255,0.05); display: flex; align-items: center; justify-content: center; color: #fff; transition: 0.3s; }
.bf-social-icon:hover { background: var(--bf-primary); transform: translateY(-3px); }
.footer-newsletter { display: flex; position: relative; }
.footer-newsletter input { width: 100%; padding: 12px 45px 12px 16px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.1); background: rgba(0,0,0,0.2); color: #fff; }
.footer-newsletter button { position: absolute; right: 5px; top: 5px; bottom: 5px; width: 35px; border-radius: 6px; border: none; background: var(--bf-primary); color: #fff; }
.footer-bottom { border-top: 1px solid rgba(255,255,255,0.05); padding-top: 24px; display: flex; justify-content: space-between; align-items: center; color: var(--bf-text-muted); font-size: 0.85rem; }
.footer-bottom-links { display: flex; gap: 16px; }
.footer-bottom-links a { color: var(--bf-text-muted); text-decoration: none; }
.footer-bottom-links a:hover { color: #fff; }

@media (max-width: 992px) {
  .bf-navbar-toggle { display: flex; }
}
</style>
