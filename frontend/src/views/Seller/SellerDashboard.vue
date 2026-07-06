<template>
  <div class="bf-page bf-fade-in p-4">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-warning mb-2">Seller Hub</span>
          <h2 class="bf-section-title">Seller Dashboard</h2>
          <p class="bf-section-desc">Manage your store, track sales, and list hardware components.</p>
        </div>
      </div>

      <!-- Overview Stats -->
      <div class="row g-4 mb-4">
        <div class="col-md-4">
          <div class="bf-stat-widget">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <span class="stat-label">Total Revenue</span>
              <div class="stat-icon" style="background: var(--bf-success-light); color: var(--bf-success);">LKR</div>
            </div>
            <div class="stat-value">LKR 0.00</div>
            <small class="text-success font-weight-bold">⚡ Active store</small>
          </div>
        </div>
        <div class="col-md-4">
          <div class="bf-stat-widget">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <span class="stat-label">Orders Received</span>
              <div class="stat-icon" style="background: var(--bf-primary-light); color: var(--bf-primary);">📦</div>
            </div>
            <div class="stat-value">0</div>
            <small class="text-muted">Pending shipment</small>
          </div>
        </div>
        <div class="col-md-4">
          <div class="bf-stat-widget">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <span class="stat-label">Total Products</span>
              <div class="stat-icon" style="background: var(--bf-info-light); color: var(--bf-info);">🔌</div>
            </div>
            <div class="stat-value">0</div>
            <small class="text-muted">Listed in marketplace</small>
          </div>
        </div>
      </div>

      <!-- Quick Setup Alert -->
      <div class="bf-card bf-glass p-4 mb-4 text-start">
        <h5 class="font-weight-bold text-dark mb-2">Complete Your Store Setup</h5>
        <p class="text-secondary small mb-3">Setup your store name, logo, banner, and policies so buyers can trust your products.</p>
        <router-link to="/seller/store-setup" class="bf-btn bf-btn-primary bf-btn-sm">Setup Store Profile</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SellerDashboard'
};
</script>
