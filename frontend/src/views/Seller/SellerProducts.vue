<template>
  <div class="bf-page bf-fade-in p-4 text-start">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start d-flex justify-content-between align-items-center flex-wrap gap-3">
          <div>
            <span class="bf-badge bf-badge-warning mb-2 text-dark">Inventory Management</span>
            <h2 class="text-white font-weight-bold mb-1">My Listed Products</h2>
            <p class="text-muted small">Manage, add, and update products in your storefront catalog.</p>
          </div>
          <div v-if="hasStore">
            <router-link :to="{ name: 'AddProduct' }" class="bf-btn bf-btn-primary bf-btn-sm shadow-glow">
              List New Product
            </router-link>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="py-5 text-center">
        <span class="spinner-border spinner-border-sm text-primary mb-2"></span>
        <p class="text-muted small">Loading catalog items...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bf-empty-state text-center">
        <div class="bf-empty-icon text-danger">⚠</div>
        <h5 class="text-white font-weight-bold">Catalogue Sync Interrupted</h5>
        <p>{{ error }}</p>
        <button class="bf-btn bf-btn-primary" @click="fetchStoreAndProducts">Retry Sync</button>
      </div>

      <!-- No Store Alert -->
      <div v-else-if="!hasStore" class="bf-card bf-glass p-5 text-center text-white border-light">
        <span style="font-size: 3rem;">🔌</span>
        <h4 class="font-weight-bold text-white mt-3 mb-2">No Active Store Found</h4>
        <p class="text-muted small mb-4">Please set up your storefront first to register your policies, logo, and brand name.</p>
        <router-link to="/seller/store-setup" class="bf-btn bf-btn-primary px-4 shadow-glow">Setup Store Profile</router-link>
      </div>

      <!-- Products Inventory Table -->
      <div v-else class="bf-card bg-dark border-light p-4 text-white">
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
          <h5 class="font-weight-bold text-white mb-0">Hardware Catalog</h5>
          <span class="bf-badge bf-badge-primary">{{ products.length }} Product(s) Listed</span>
        </div>

        <div class="table-responsive" v-if="products.length > 0">
          <table class="bf-table text-white">
            <thead>
              <tr>
                <th class="text-white">Product Spec</th>
                <th class="text-white">Price Rate</th>
                <th class="text-white">Stock Level</th>
                <th class="text-white text-end">Operations</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in products" :key="p.productId" class="border-light-bottom">
                <td>
                  <div class="d-flex align-items-center gap-3">
                    <div class="bf-product-mini-img-wrapper border-light">
                      <img :src="p.imageUrl" class="bf-product-mini-img" alt="Product Thumbnail" />
                    </div>
                    <div>
                      <h6 class="mb-0 font-weight-bold text-white">{{ p.productName }}</h6>
                      <small class="text-muted" v-if="p.storeName">{{ p.storeName }}</small>
                    </div>
                  </div>
                </td>
                <td class="font-weight-bold text-primary">{{ formatPrice(p.price) }}</td>
                <td>
                  <span class="bf-badge" :class="p.stock > 0 ? 'bf-badge-success' : 'bf-badge-danger'">
                    {{ p.stock > 0 ? p.stock + ' in stock' : 'Out of stock' }}
                  </span>
                </td>
                <td class="text-end">
                  <div class="d-inline-flex gap-2">
                    <router-link :to="{ name: 'EditProduct', params: { id: p.productId } }" class="bf-btn bf-btn-ghost bf-btn-sm text-primary">
                      Edit
                    </router-link>
                    <button class="bf-btn bf-btn-danger bf-btn-sm" @click="handleDeleteProduct(p.productId)">
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Empty Products List -->
        <div v-else class="text-center py-5 text-muted small">
          No components listed in your store catalogue yet. Click above to list your first hardware component!
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { getCurrentUser } from '../../utils/auth';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'SellerProducts',
  data() {
    return {
      storeId: null,
      products: [],
      loading: true,
      error: null,
      hasStore: false
    };
  },
  methods: {
    formatPrice(val) {
      return formatPrice(val);
    },
    async fetchStoreAndProducts() {
      const user = getCurrentUser();
      if (!user || user.role !== 'SELLER') {
        this.loading = false;
        return;
      }

      this.loading = true;
      this.error = null;
      try {
        const storeRes = await api.get(`/store/user/${user.userId}`);
        const store = storeRes.data.data;
        if (store) {
          this.storeId = store.storeId;
          this.hasStore = true;
          
          // Fetch products listed under this store
          const productsRes = await api.get(`/store/${this.storeId}/products`);
          this.products = productsRes.data.data || [];
        } else {
          this.hasStore = false;
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
        console.warn('Seller products sync failure:', err);
      } finally {
        this.loading = false;
      }
    },
    async handleDeleteProduct(productId) {
      if (!confirm('Are you sure you want to delete this product? This action cannot be undone.')) return;
      try {
        await api.delete(`/product/${productId}`);
        showToast({
          message: 'Component removed from your store catalog successfully',
          type: 'success',
          title: 'Component Deleted'
        });
        this.products = this.products.filter(p => p.productId !== productId);
      } catch (err) {
        showToast({
          message: 'Failed to delete product: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      }
    }
  },
  mounted() {
    this.fetchStoreAndProducts();
  }
};
</script>

<style scoped>
.bf-product-mini-img-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: var(--bf-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bf-product-mini-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.border-light-bottom {
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}
</style>
