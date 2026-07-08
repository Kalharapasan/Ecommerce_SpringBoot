<template>
  <div 
    class="ebx-card bf-glass-card" 
    @mouseenter="hovered = true" 
    @mouseleave="hovered = false"
  >
    <!-- Image Wrapper with Zoom & badges -->
    <div class="ebx-image-wrapper">
      <router-link :to="{ name: 'ProductDetail', params: { id: product.productId } }">
        <img
          class="ebx-image"
          :src="currentImageUrl"
          @error="handleImageError"
          alt="Product Image"
          loading="lazy"
        />
      </router-link>

      <!-- Glass overlay on hover -->
      <div class="ebx-image-overlay"></div>

      <!-- Watchlist heart (Micro-interactive) -->
      <button
        class="ebx-watch-btn"
        :class="{ active: isWatched }"
        @click.stop="toggleWatch"
        :title="isWatched ? 'Remove from Watchlist' : 'Add to Watchlist'"
      >
        <i class="bi" :class="isWatched ? 'bi-heart-fill text-danger' : 'bi-heart'"></i>
      </button>

      <!-- Dynamic Stock Badge -->
      <span class="ebx-badge" :class="stockClass">{{ stockLabel }}</span>

      <!-- Fast Quick View trigger -->
      <router-link
        v-if="hovered"
        :to="{ name: 'ProductDetail', params: { id: product.productId } }"
        class="ebx-quickview-btn bf-glass-strong bf-scale-up"
      >
        <i class="bi bi-eye me-1"></i> Quick View
      </router-link>
    </div>

    <!-- Card Contents -->
    <div class="ebx-body">
      <div class="d-flex justify-content-between align-items-center mb-1">
        <span v-if="product.category" class="ebx-category text-gradient">{{ product.category.categoryName }}</span>
        <span class="ebx-shipping-badge"><i class="bi bi-truck me-1"></i>Free Shipping</span>
      </div>

      <h3 class="ebx-title">
        <router-link :to="{ name: 'ProductDetail', params: { id: product.productId } }">
          {{ product.productName }}
        </router-link>
      </h3>

      <!-- Seller Details & Rating Stars -->
      <div class="ebx-seller-row">
        <i class="bi bi-shield-check text-cyan" v-if="isVerifiedSeller"></i>
        <span class="ebx-seller-name">{{ sellerName }}</span>
        <div class="ms-auto d-flex align-items-center gap-1" v-if="sellerRating">
          <span class="text-warning small"><i class="bi bi-star-fill"></i></span>
          <span class="ebx-rating-val">{{ sellerRating.toFixed(1) }}</span>
          <span class="ebx-review-count">({{ sellerReviewCount }})</span>
        </div>
      </div>

      <p class="ebx-desc">{{ product.description }}</p>

      <!-- Price Block -->
      <div class="ebx-price-row">
        <span class="ebx-price text-gradient-primary">{{ formattedPrice }}</span>
      </div>

      <!-- Action Buttons -->
      <div class="ebx-actions mt-3">
        <button
          class="bf-btn-premium w-100 py-2.5 d-flex align-items-center justify-content-center gap-2"
          :disabled="isOutOfStock || addingToCart"
          @click.stop="quickAddToCart"
        >
          <span v-if="addingToCart" class="spinner-border spinner-border-sm" role="status"></span>
          <i v-else class="bi bi-cart-plus"></i>
          <span>{{ isOutOfStock ? 'Out of Stock' : (addingToCart ? 'Adding...' : 'Add to Cart') }}</span>
        </button>
      </div>

      <router-link
        v-if="isAdmin"
        :to="{ name: 'EditProduct', params: { id: product.productId } }"
        class="ebx-admin-edit mt-3"
      >
        <i class="bi bi-pencil-square"></i> Admin Edit
      </router-link>
    </div>
  </div>
</template>

<script>
import api, { formatPrice, extractErrorMessage } from '../../utils/api';
import { getCurrentUser, isLoggedIn, getToken, authState } from '../../utils/auth';
import { showToast } from '../Common/ToastNotification.vue';

const WATCHLIST_KEY = 'bf-watchlist';

function readWatchlist() {
  try {
    return JSON.parse(localStorage.getItem(WATCHLIST_KEY)) || [];
  } catch (e) {
    return [];
  }
}

function writeWatchlist(list) {
  localStorage.setItem(WATCHLIST_KEY, JSON.stringify(list));
}

export default {
  name: 'ProductBox',
  props: ['product'],
  data() {
    return {
      hovered: false,
      addingToCart: false,
      currentImageUrl: this.product.imageUrl,
      fallbackUrl: 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?auto=format&fit=crop&w=500&q=80', // Premium hardware fallback
      isWatched: readWatchlist().includes(this.product.productId)
    };
  },
  watch: {
    'product.imageUrl'(newVal) {
      this.currentImageUrl = newVal;
    }
  },
  computed: {
    formattedPrice() {
      return formatPrice(this.product.price);
    },
    isAdmin() {
      const user = getCurrentUser();
      return user && user.role === 'ADMIN';
    },
    isOutOfStock() {
      return !this.product.stock || this.product.stock <= 0;
    },
    stockLabel() {
      if (this.isOutOfStock) return 'Sold Out';
      if (this.product.stock <= 5) return `Only ${this.product.stock} Left`;
      return 'In Stock';
    },
    stockClass() {
      if (this.isOutOfStock) return 'bg-danger';
      if (this.product.stock <= 5) return 'bg-warning text-dark';
      return 'bg-success';
    },
    sellerName() {
      return this.product.store && this.product.store.storeName
        ? this.product.store.storeName
        : 'Marketplace Seller';
    },
    isVerifiedSeller() {
      return !!(this.product.store && this.product.store.isVerified);
    },
    sellerRating() {
      return this.product.store ? Number(this.product.store.rating) || 0 : 0;
    },
    sellerReviewCount() {
      return this.product.store ? Number(this.product.store.reviewsCount) || 0 : 0;
    },
    starPercent() {
      return Math.max(0, Math.min(100, (this.sellerRating / 5) * 100));
    }
  },
  methods: {
    handleImageError() {
      this.currentImageUrl = this.fallbackUrl;
    },
    toggleWatch() {
      const list = readWatchlist();
      const idx = list.indexOf(this.product.productId);
      if (idx >= 0) {
        list.splice(idx, 1);
        this.isWatched = false;
        showToast({ message: 'Removed from watchlist', type: 'info' });
      } else {
        list.push(this.product.productId);
        this.isWatched = true;
        showToast({ message: 'Added to watchlist', type: 'success' });
      }
      writeWatchlist(list);
    },
    async quickAddToCart() {
      if (!isLoggedIn()) {
        showToast({
          message: 'Please sign in to add items to your shopping cart.',
          type: 'warning',
          title: 'Authentication Required'
        });
        this.$router.push({ name: 'SignIn', query: { redirect: this.$route.fullPath } });
        return;
      }
      this.addingToCart = true;
      try {
        const token = getToken();
        const payload = { productId: this.product.productId, count: 1 };
        const response = await api.post(`/cart/${token}`, payload);
        authState.cartCount += 1;
        showToast({
          message: response.data.message || 'Added to shopping cart successfully!',
          type: 'success',
          title: 'Cart Updated'
        });
      } catch (err) {
        showToast({ message: extractErrorMessage(err), type: 'error', title: 'Operation Failed' });
      } finally {
        this.addingToCart = false;
      }
    }
  }
};
</script>

<style scoped>
.ebx-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.ebx-image-wrapper {
  position: relative;
  overflow: hidden;
  aspect-ratio: 16/10;
  background: var(--bf-bg-secondary);
}

.ebx-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.ebx-card:hover .ebx-image {
  transform: scale(1.08);
}

.ebx-image-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(to top, rgba(11, 15, 25, 0.7) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.ebx-card:hover .ebx-image-overlay {
  opacity: 1;
}

/* Glass Watchlist button */
.ebx-watch-btn {
  position: absolute;
  top: 12px; right: 12px;
  width: 36px; height: 36px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(11, 15, 25, 0.6);
  color: var(--bf-text-secondary);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex; align-items: center; justify-content: center;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  z-index: 5;
}

.ebx-watch-btn:hover {
  transform: scale(1.1);
  background: rgba(11, 15, 25, 0.8);
  border-color: rgba(255, 255, 255, 0.2);
}

.ebx-watch-btn.active {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
  color: var(--bf-danger);
}

/* Badge overlay */
.ebx-badge {
  position: absolute;
  top: 12px; left: 12px;
  padding: 4px 10px;
  font-size: 0.7rem;
  font-weight: 700;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  z-index: 5;
}

/* Quickview Hover trigger */
.ebx-quickview-btn {
  position: absolute;
  bottom: 12px; left: 12px; right: 12px;
  padding: 10px;
  text-align: center;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
  text-decoration: none;
  border-radius: var(--bf-radius-md);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 10;
  display: flex; align-items: center; justify-content: center;
}

.ebx-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.ebx-category {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.ebx-shipping-badge {
  font-size: 0.7rem;
  color: var(--bf-text-muted);
  font-weight: 500;
}

.ebx-title {
  font-size: 1.05rem;
  font-weight: 700;
  margin: 8px 0 12px 0;
  min-height: 2.6em;
  line-height: 1.35;
}

.ebx-title a {
  color: var(--bf-text-primary);
  text-decoration: none;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ebx-title a:hover {
  color: var(--bf-cyan);
}

.ebx-seller-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.78rem;
  color: var(--bf-text-muted);
  margin-bottom: 12px;
}

.ebx-seller-name {
  font-weight: 600;
  color: var(--bf-text-secondary);
}

.ebx-rating-val {
  font-weight: 700;
  color: var(--bf-text-primary);
}

.ebx-desc {
  font-size: 0.85rem;
  color: var(--bf-text-muted);
  line-height: 1.5;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ebx-price-row {
  margin-top: auto;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.ebx-price {
  font-size: 1.35rem;
  font-weight: 800;
}

.ebx-admin-edit {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px;
  border-radius: var(--bf-radius-md);
  border: 1px dashed rgba(255, 255, 255, 0.15);
  font-size: 0.8rem;
  color: var(--bf-text-muted);
  transition: all 0.2s ease;
}

.ebx-admin-edit:hover {
  border-color: var(--bf-cyan);
  color: var(--bf-cyan);
}
</style>
