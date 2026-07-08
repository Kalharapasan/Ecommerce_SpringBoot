<template>
  <div class="bf-page bf-fade-in py-5 text-start">
    <div class="container position-relative">
      <div class="glow-bg animate-float"></div>

      <!-- Header -->
      <div class="row mb-5 position-relative z-2">
        <div class="col-12 text-start d-flex justify-content-between align-items-center flex-wrap gap-4">
          <div>
            <span class="badge bg-warning bg-opacity-25 text-warning mb-2 px-3 py-2 rounded-pill text-uppercase">
              <i class="bi bi-shop me-1"></i> Merchant Command Center
            </span>
            <h2 class="text-white font-bold mb-1 fs-2">Seller Dashboard</h2>
            <p class="text-muted small">Configure storefront policies, track sales revenue metrics, and list hardware configurations.</p>
          </div>
          <div class="d-flex gap-2">
            <router-link to="/seller/store-setup" class="text-decoration-none">
              <button class="bf-btn-ghost py-2 px-3 border-secondary border-opacity-25 text-white">Store Settings</button>
            </router-link>
            <router-link to="/seller/products" class="text-decoration-none">
              <button class="bf-btn-premium border-0 py-2.5 px-3">Manage Inventory</button>
            </router-link>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="row g-4 mb-4 position-relative z-2">
        <div class="col-md-3" v-for="i in 4" :key="i">
          <div class="bf-skeleton" style="height: 140px;"></div>
        </div>
      </div>

      <!-- No Store Setup Alert -->
      <div v-else-if="!hasStore" class="bf-glass-card p-5 text-center text-white border-0 position-relative z-2">
        <span class="fs-1 text-cyan"><i class="bi bi-shop-window"></i></span>
        <h4 class="font-bold text-white mt-3 mb-2">No Registered Storefront Found</h4>
        <p class="text-muted small mb-4">You must configure your vendor policies, brand name, and upload a store logo before listing gear.</p>
        <router-link to="/seller/store-setup" class="text-decoration-none">
          <button class="bf-btn-premium border-0 py-2.5 px-4 shadow-glow">Launch Store Profile</button>
        </router-link>
      </div>

      <!-- Analytics Overview Grid -->
      <div v-else class="bf-fade-in text-white position-relative z-2">
        <div class="row g-4 mb-5">
          <!-- Widget 1: Revenue -->
          <div class="col-xl-3 col-md-6">
            <div class="bf-stat-widget bf-glass border-0 p-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="text-muted small font-bold">TOTAL SALES</span>
                <span class="stat-icon-glow bg-success text-success"><i class="bi bi-cash-stack"></i></span>
              </div>
              <h3 class="stat-value font-bold text-white mb-2">{{ formatPrice(stats.totalRevenue) }}</h3>
              <small class="text-success font-semibold small"><i class="bi bi-graph-up-arrow me-1"></i>Sales volume rate</small>
            </div>
          </div>
          <!-- Widget 2: Orders -->
          <div class="col-xl-3 col-md-6">
            <div class="bf-stat-widget bf-glass border-0 p-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="text-muted small font-bold">ORDERS RECEIVED</span>
                <span class="stat-icon-glow bg-primary text-cyan"><i class="bi bi-box-seam"></i></span>
              </div>
              <h3 class="stat-value font-bold text-white mb-2">{{ stats.ordersCount }}</h3>
              <small class="text-muted small">Unique customer purchases</small>
            </div>
          </div>
          <!-- Widget 3: Products -->
          <div class="col-xl-3 col-md-6">
            <div class="bf-stat-widget bf-glass border-0 p-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="text-muted small font-bold">ACTIVE PRODUCTS</span>
                <span class="stat-icon-glow bg-purple text-purple"><i class="bi bi-cpu"></i></span>
              </div>
              <h3 class="stat-value font-bold text-white mb-2">{{ stats.productsCount }}</h3>
              <small class="text-muted small">Listed inventory components</small>
            </div>
          </div>
          <!-- Widget 4: Fanbase -->
          <div class="col-xl-3 col-md-6">
            <div class="bf-stat-widget bf-glass border-0 p-4">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="text-muted small font-bold">STORE FOLLOWERS</span>
                <span class="stat-icon-glow bg-warning text-warning"><i class="bi bi-heart-fill"></i></span>
              </div>
              <h3 class="stat-value font-bold text-white mb-2">{{ stats.followersCount }}</h3>
              <small class="text-muted small">Enthusiast fanbase counts</small>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <!-- Verification Status -->
          <div class="col-lg-6">
            <div class="bf-glass-card border-0 p-4 h-100 text-start">
              <h5 class="font-bold text-white mb-4">Verification & Ratings</h5>
              
              <div class="d-flex align-items-center gap-3 mb-4 p-3 bg-black bg-opacity-20 rounded border border-secondary border-opacity-10">
                <span class="fs-2 text-cyan"><i class="bi bi-shield-check"></i></span>
                <div>
                  <h6 class="font-bold mb-1 text-white">
                    Privilege Badge Status:
                    <span v-if="stats.isVerified" class="text-success">Verified Hub</span>
                    <span v-else class="text-warning">In Review</span>
                  </h6>
                  <p class="text-muted small mb-0">
                    {{ stats.isVerified ? 'Your storefront displays the official ByteForge Verified Seller badge.' : 'ByteForge administrators are evaluating your credentials.' }}
                  </p>
                </div>
              </div>

              <div class="d-flex align-items-center justify-content-between">
                <div>
                  <span class="text-muted d-block small">Average Score</span>
                  <span class="stat-value text-warning fs-3 font-bold">{{ stats.rating.toFixed(1) }} ★</span>
                </div>
                <div class="text-end">
                  <span class="text-muted d-block small">Reviews Submitted</span>
                  <strong class="text-white">{{ stats.reviewsCount }} reviews</strong>
                </div>
              </div>
            </div>
          </div>

          <!-- Shortcuts -->
          <div class="col-lg-6">
            <div class="bf-glass-card border-0 p-4 h-100 text-start">
              <h5 class="font-bold text-white mb-3">Quick Actions Console</h5>
              <p class="text-muted small mb-4">Maintain inventory or examine how your store displays publicly.</p>
              
              <div class="d-flex flex-column gap-3 mt-4">
                <router-link :to="'/store/' + stats.storeId" class="text-decoration-none">
                  <button class="bf-btn-ghost w-100 text-center py-2.5 border-secondary border-opacity-25">
                    <i class="bi bi-eye me-1"></i> Inspect Public Storefront Page
                  </button>
                </router-link>
                <router-link to="/seller/products" class="text-decoration-none">
                  <button class="bf-btn-premium border-0 w-100 text-center py-2.5 shadow-glow">
                    <i class="bi bi-plus-circle me-1"></i> List & Maintain Components
                  </button>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { getToken } from '../../utils/auth';

export default {
  name: 'SellerDashboard',
  data() {
    return {
      stats: {
        storeId: null,
        storeName: '',
        totalRevenue: 0.0,
        ordersCount: 0,
        productsCount: 0,
        followersCount: 0,
        rating: 0.0,
        reviewsCount: 0,
        isVerified: false
      },
      loading: true,
      hasStore: false
    };
  },
  methods: {
    formatPrice(val) { return formatPrice(val); },
    async fetchAnalytics() {
      const token = getToken();
      if (!token) {
        this.loading = false;
        return;
      }

      this.loading = true;
      try {
        const response = await api.get(`/store/analytics?token=${token}`);
        this.stats = response.data.data;
        this.hasStore = true;
      } catch (err) {
        this.hasStore = false;
        console.warn('Failed to fetch store analytics: ', extractErrorMessage(err));
      } finally {
        this.loading = false;
      }
    }
  },
  mounted() {
    this.fetchAnalytics();
  }
};
</script>

<style scoped>
.glow-bg {
  position: absolute;
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.05) 0%, transparent 75%);
  top: -100px; left: -100px;
  pointer-events: none;
  z-index: 1;
}

.bf-stat-widget {
  transition: all var(--bf-transition-base);
}

.bf-stat-widget:hover {
  transform: translateY(-4px);
  box-shadow: var(--bf-shadow-xl), 0 0 15px rgba(255, 255, 255, 0.05);
}

.stat-icon-glow {
  width: 38px; height: 38px;
  border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.25rem;
}
.stat-icon-glow.bg-success { background: rgba(16, 185, 129, 0.15) !important; }
.stat-icon-glow.bg-primary { background: rgba(6, 182, 212, 0.15) !important; }
.stat-icon-glow.bg-purple { background: rgba(124, 58, 237, 0.15) !important; }
.stat-icon-glow.bg-warning { background: rgba(245, 158, 11, 0.15) !important; }
</style>
