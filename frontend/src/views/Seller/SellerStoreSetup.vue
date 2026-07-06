<template>
  <div class="bf-page bf-fade-in p-4">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-warning mb-2">Store Profile</span>
          <h2 class="bf-section-title">Setup Store Profile</h2>
          <p class="bf-section-desc">Customize store presence for the ByteForge marketplace.</p>
        </div>
      </div>

      <div class="row">
        <div class="col-lg-8 mx-auto">
          <div class="bf-card p-4 text-start">
            <form @submit.prevent="saveStore">
              <div class="mb-3">
                <label class="form-label font-weight-bold text-secondary">Store Name</label>
                <input type="text" class="bf-input" v-model="storeName" placeholder="e.g., Apex Hardware Sri Lanka" required />
              </div>
              
              <div class="mb-3">
                <label class="form-label font-weight-bold text-secondary">Store Description</label>
                <textarea class="bf-input" rows="4" v-model="storeDescription" placeholder="Tell buyers about your products, specialization, and services..."></textarea>
              </div>

              <div class="mb-4">
                <label class="form-label font-weight-bold text-secondary">Store Logo URL</label>
                <input type="text" class="bf-input" v-model="storeLogo" placeholder="http://example.com/logo.png" />
              </div>

              <div class="mb-4">
                <label class="form-label font-weight-bold text-secondary">Store Banner URL</label>
                <input type="text" class="bf-input" v-model="storeBanner" placeholder="http://example.com/banner.png" />
              </div>

              <div class="d-flex gap-3">
                <button type="submit" class="bf-btn bf-btn-primary">Save Changes</button>
                <router-link to="/seller/dashboard" class="bf-btn bf-btn-ghost">Cancel</router-link>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SellerStoreSetup',
  data() {
    return {
      storeName: '',
      storeDescription: '',
      storeLogo: '',
      storeBanner: ''
    };
  },
  methods: {
    saveStore() {
      // Placeholder method
    }
  }
};
</script>
