<template>
  <div class="bf-page bf-fade-in p-4 text-start">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-warning mb-2 text-dark">Store Profile</span>
          <h2 class="text-white font-weight-bold mb-1">Setup Store Profile</h2>
          <p class="text-muted small">Customize store presence for the ByteForge marketplace.</p>
        </div>
      </div>

      <div class="row text-white">
        <div class="col-lg-8 mx-auto">
          <div class="bf-card bg-dark border-light p-4 text-start">
            <div v-if="loading" class="py-5 text-center">
              <span class="spinner-border spinner-border-sm text-primary mb-2"></span>
              <p class="text-muted small">Loading store configurations...</p>
            </div>
            
            <form v-else @submit.prevent="saveStore">
              <div class="mb-3">
                <label class="form-label small text-muted font-weight-bold">Store Name</label>
                <input type="text" class="bf-input text-white bg-dark border-light" v-model="storeName" placeholder="e.g., Apex Hardware Sri Lanka" required />
              </div>
              
              <div class="mb-3">
                <label class="form-label small text-muted font-weight-bold">Store Description</label>
                <textarea class="bf-input text-white bg-dark border-light" rows="4" v-model="storeDescription" placeholder="Tell buyers about your products, specialization, and services..."></textarea>
              </div>

              <div class="mb-3">
                <label class="form-label small text-muted font-weight-bold">Store Logo URL</label>
                <input type="text" class="bf-input text-white bg-dark border-light" v-model="storeLogo" placeholder="e.g., https://example.com/logo.png" />
              </div>

              <div class="mb-3">
                <label class="form-label small text-muted font-weight-bold">Store Banner URL</label>
                <input type="text" class="bf-input text-white bg-dark border-light" v-model="storeBanner" placeholder="e.g., https://example.com/banner.png" />
              </div>

              <div class="mb-4">
                <label class="form-label small text-muted font-weight-bold">Store Policies & Warranty Terms</label>
                <textarea class="bf-input text-white bg-dark border-light" rows="4" v-model="storePolicies" placeholder="Detail shipping speeds, return policies, and hardware warranty options..."></textarea>
              </div>

              <div class="d-flex gap-3">
                <button type="submit" class="bf-btn bf-btn-primary" :disabled="saving">
                  <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
                  Save Changes
                </button>
                <router-link to="/seller/dashboard" class="bf-btn bf-btn-ghost text-muted">Cancel</router-link>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage } from '../../utils/api';
import { getCurrentUser, getToken, login } from '../../utils/auth';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'SellerStoreSetup',
  data() {
    return {
      storeName: '',
      storeDescription: '',
      storeLogo: '',
      storeBanner: '',
      storePolicies: '',
      loading: false,
      saving: false,
      isEdit: false
    };
  },
  methods: {
    async fetchStoreProfile() {
      const user = getCurrentUser();
      if (!user || user.role !== 'SELLER') return;

      this.loading = true;
      try {
        const response = await api.get(`/store/user/${user.userId}`);
        const store = response.data.data;
        if (store) {
          this.storeName = store.storeName || '';
          this.storeDescription = store.storeDescription || '';
          this.storeLogo = store.storeLogoUrl || '';
          this.storeBanner = store.storeBannerUrl || '';
          this.storePolicies = store.storePolicies || '';
          this.isEdit = true;
        }
      } catch (err) {
        console.warn('Could not fetch store setup profile:', err);
      } finally {
        this.loading = false;
      }
    },
    async saveStore() {
      const token = getToken();
      const user = getCurrentUser();
      if (!token || !user) {
        showToast({ message: 'Session expired. Please log in.', type: 'error' });
        return;
      }

      this.saving = true;
      try {
        const payload = {
          storeName: this.storeName,
          storeDescription: this.storeDescription,
          storeLogoUrl: this.storeLogo,
          storeBannerUrl: this.storeBanner,
          storePolicies: this.storePolicies
        };

        if (this.isEdit || user.role === 'SELLER') {
          // Update Store profile
          await api.put(`/store/update?token=${token}`, payload);
          showToast({
            message: 'Your seller store profile has been updated.',
            type: 'success',
            title: 'Store Settings Saved'
          });
        } else {
          // Register new Store & become Seller
          const res = await api.post(`/store/register?token=${token}`, payload);
          const { token: newToken } = res.data.data;
          
          if (newToken) {
            login(newToken);
          }
          
          showToast({
            message: 'Congratulations! Your storefront is now live.',
            type: 'success',
            title: 'Marketplace Registration Completed'
          });
        }
        
        this.$router.push('/seller/dashboard');
      } catch (err) {
        showToast({
          message: 'Failed to save store profile: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      } finally {
        this.saving = false;
      }
    }
  },
  mounted() {
    this.fetchStoreProfile();
  }
};
</script>

<style scoped>
</style>
