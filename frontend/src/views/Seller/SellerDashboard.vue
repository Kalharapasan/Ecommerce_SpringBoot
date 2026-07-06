<template>
  <div class="bf-page bf-fade-in p-4">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start d-flex justify-content-between align-items-center flex-wrap gap-3">
          <div>
            <span class="bf-badge bf-badge-warning mb-2">Seller Hub</span>
            <h2 class="bf-section-title">Seller Dashboard</h2>
            <p class="bf-section-desc">Manage your store, track sales, and list hardware components.</p>
          </div>
          <div>
            <router-link to="/seller/store-setup" class="bf-btn bf-btn-outline bf-btn-sm me-2">Store Profile Settings</router-link>
            <router-link to="/seller/products" class="bf-btn bf-btn-primary bf-btn-sm">Manage Products</router-link>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="row g-4 mb-4">
        <div class="col-md-4" v-for="i in 3" :key="i">
          <div class="bf-stat-widget">
            <span class="bf-spinner mb-2"></span>
          </div>
        </div>
      </div>

      <!-- No Store Alert -->
      <div v-else-if="!hasStore" class="bf-card bf-glass p-5 text-center text-start">
        <span style="font-size: 3rem;">🔌</span>
        <h4 class="font-weight-bold text-dark mt-3 mb-2">No Active Store Found</h4>
        <p class="text-secondary small mb-4">Please set up your storefront first to register your policies, logo, and brand name.</p>
        <router-link to="/seller/store-setup" class="bf-btn bf-btn-primary">Setup Store Profile</router-link>
      </div>

      <!-- Overview Stats -->
      <div v-else class="bf-fade-in">
        <div class="row g-4 mb-4 text-start">
          <div class="col-md-3">
            <div class="bf-stat-widget">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="stat-label">Total Revenue</span>
                <div class="stat-icon" style="background: var(--bf-success-light); color: var(--bf-success);">LKR</div>
              </div>
              <div class="stat-value">{{ formatPrice(stats.totalRevenue) }}</div>
              <small class="text-success font-weight-bold">⚡ Sales volume</small>
            </div>
          </div>
          <div class="col-md-3">
            <div class="bf-stat-widget">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="stat-label">Orders Received</span>
                <div class="stat-icon" style="background: var(--bf-primary-light); color: var(--bf-primary);">📦</div>
              </div>
              <div class="stat-value">{{ stats.ordersCount }}</div>
              <small class="text-muted">Unique purchases</small>
            </div>
          </div>
          <div class="col-md-3">
            <div class="bf-stat-widget">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="stat-label">Total Products</span>
                <div class="stat-icon" style="background: var(--bf-info-light); color: var(--bf-info);">🔌</div>
              </div>
              <div class="stat-value">{{ stats.productsCount }}</div>
              <small class="text-muted">Listed components</small>
            </div>
          </div>
          <div class="col-md-3">
            <div class="bf-stat-widget">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="stat-label">Store Followers</span>
                <div class="stat-icon" style="background: var(--bf-warning-light); color: var(--bf-warning);">❤️</div>
              </div>
              <div class="stat-value">{{ stats.followersCount }}</div>
              <small class="text-muted">Active fanbase</small>
            </div>
          </div>
        </div>

        <div class="row g-4 text-start">
          <!-- Verification & Ratings Card -->
          <div class="col-md-6">
            <div class="bf-card p-4 h-100">
              <h5 class="font-weight-bold text-dark mb-4">Verification & Ratings</h5>
              
              <div class="d-flex align-items-center gap-3 mb-4 p-3 bg-light rounded">
                <span style="font-size: 2rem;">🛡️</span>
                <div>
                  <h6 class="font-weight-bold mb-1">
                    Store Verification Status:
                    <span v-if="stats.isVerified" class="text-success">Verified</span>
                    <span v-else class="text-warning">Pending Review</span>
                  </h6>
                  <p class="text-secondary small mb-0">
                    {{ stats.isVerified ? 'Your store carries the official verified seller trust badge.' : 'ByteForge admins are reviewing your store profile details.' }}
                  </p>
                </div>
              </div>

              <div class="d-flex align-items-center justify-content-between">
                <div>
                  <span class="text-muted d-block small">Average Score</span>
                  <span class="stat-value text-warning">{{ stats.rating }} ★</span>
                </div>
                <div class="text-end">
                  <span class="text-muted d-block small">Reviews Submitted</span>
                  <strong class="text-dark">{{ stats.reviewsCount }} reviews</strong>
                </div>
              </div>
            </div>
          </div>

          <!-- Shortcuts & Management -->
          <div class="col-md-6">
            <div class="bf-card p-4 h-100">
              <h5 class="font-weight-bold text-dark mb-3">Quick Actions</h5>
              <p class="text-secondary small mb-4">Easily jump to catalog maintenance or test view your store page.</p>
              
              <div class="d-flex flex-column gap-3">
                <router-link :to="'/store/' + stats.storeId" class="bf-btn bf-btn-outline w-100 text-center py-2.5">
                  👁️ View Public Storefront Page
                </router-link>
                <router-link to="/seller/products" class="bf-btn bf-btn-primary w-100 text-center py-2.5">
                  ➕ List & Manage Store Products
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
    formatPrice(val) {
      return formatPrice(val);
    },
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
