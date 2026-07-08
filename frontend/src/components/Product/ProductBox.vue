<template>
  <div class="ebx-card" @mouseenter="hovered = true" @mouseleave="hovered = false">

    <!-- Image -->
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

      <!-- Watchlist heart -->
      <button
        class="ebx-watch-btn"
        :class="{ active: isWatched }"
        @click="toggleWatch"
        :title="isWatched ? 'Remove from Watchlist' : 'Add to Watchlist'"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 16 16" :fill="isWatched ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="1.4">
          <path d="M8 14.25s-6-3.87-6-8.1A3.4 3.4 0 0 1 8 4.1a3.4 3.4 0 0 1 6 2.05c0 4.23-6 8.1-6 8.1z"/>
        </svg>
      </button>

      <!-- Stock / shipping tag -->
      <div class="ebx-tag" :class="stockClass">{{ stockLabel }}</div>

      <!-- Quick view (hover) -->
      <transition name="page-fade">
        <router-link
          v-if="hovered"
          :to="{ name: 'ProductDetail', params: { id: product.productId } }"
          class="ebx-quickview-btn"
        >
          Quick View
        </router-link>
      </transition>
    </div>

    <!-- Body -->
    <div class="ebx-body">
      <span v-if="product.category" class="ebx-category">{{ product.category.categoryName }}</span>

      <h3 class="ebx-title">
        <router-link :to="{ name: 'ProductDetail', params: { id: product.productId } }">
          {{ product.productName }}
        </router-link>
      </h3>

      <!-- Seller row -->
      <div class="ebx-seller-row">
        <span class="ebx-seller-name">{{ sellerName }}</span>
        <span v-if="isVerifiedSeller" class="ebx-verified-badge" title="Verified Seller">✓</span>
        <span v-if="sellerRating" class="ebx-stars">
          <span class="ebx-stars-fill" :style="{ width: starPercent + '%' }">★★★★★</span>
          <span class="ebx-stars-base">★★★★★</span>
        </span>
        <span v-if="sellerReviewCount" class="ebx-review-count">({{ sellerReviewCount }})</span>
      </div>

      <p class="ebx-desc">{{ product.description }}</p>

      <!-- Price -->
      <div class="ebx-price-row">
        <span class="ebx-price">{{ formattedPrice }}</span>
        <span class="ebx-shipping-note">+ shipping from seller</span>
      </div>

      <!-- Actions -->
      <div class="ebx-actions">
        <router-link
          :to="{ name: 'ProductDetail', params: { id: product.productId } }"
          class="ebx-btn ebx-btn-outline"
        >
          View Details
        </router-link>
        <button
          class="ebx-btn ebx-btn-primary"
          :disabled="isOutOfStock || addingToCart"
          @click="quickAddToCart"
        >
          <span v-if="addingToCart">Adding…</span>
          <span v-else-if="isOutOfStock">Sold Out</span>
          <span v-else>Add to Cart</span>
        </button>
      </div>

      <router-link
        v-if="isAdmin"
        :to="{ name: 'EditProduct', params: { id: product.productId } }"
        class="ebx-admin-edit"
      >
        Edit Product
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
      fallbackUrl: 'https://placehold.co/300x200?text=No+Image+Available',
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
      if (this.isOutOfStock) return 'Out of Stock';
      if (this.product.stock <= 5) return `Only ${this.product.stock} left`;
      return 'In Stock';
    },
    stockClass() {
      if (this.isOutOfStock) return 'tag-out';
      if (this.product.stock <= 5) return 'tag-low';
      return 'tag-in';
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
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  overflow: hidden;
  transition: all var(--bf-transition-base);
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.ebx-card:hover {
  box-shadow: var(--bf-shadow-xl);
  border-color: var(--bf-primary-glow);
  transform: translateY(-4px);
}

/* Image */
.ebx-image-wrapper {
  position: relative;
  overflow: hidden;
  aspect-ratio: 4/3;
  background: var(--bf-bg-tertiary);
}

.ebx-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  display: block;
}

.ebx-card:hover .ebx-image {
  transform: scale(1.06);
}

/* Watchlist heart */
.ebx-watch-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--bf-transition-fast);
  box-shadow: var(--bf-shadow-sm);
}
[data-theme="dark"] .ebx-watch-btn {
  background: rgba(17, 24, 39, 0.85);
  color: #d1d5db;
}
.ebx-watch-btn:hover {
  transform: scale(1.1);
}
.ebx-watch-btn.active {
  color: var(--bf-danger);
}

/* Stock tag */
.ebx-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 3px 10px;
  border-radius: var(--bf-radius-full);
  font-size: 0.65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #ffffff;
}
.tag-in { background: rgba(16, 185, 129, 0.9); }
.tag-low { background: rgba(245, 158, 11, 0.92); }
.tag-out { background: rgba(239, 68, 68, 0.9); }

/* Quick view */
.ebx-quickview-btn {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px;
  text-align: center;
  background: rgba(17, 24, 39, 0.82);
  color: #fff;
  font-size: 0.8rem;
  font-weight: 600;
  text-decoration: none;
  backdrop-filter: blur(4px);
}

/* Body */
.ebx-body {
  padding: 14px 16px 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.ebx-category {
  font-size: var(--bf-font-size-xs);
  color: var(--bf-primary);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 4px;
}

.ebx-title {
  font-size: var(--bf-font-size-base);
  font-weight: 700;
  margin: 0 0 6px 0;
  line-height: 1.3;
  min-height: 2.4em;
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
  color: var(--bf-primary);
}

/* Seller row */
.ebx-seller-row {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-wrap: wrap;
  margin-bottom: 6px;
  font-size: 0.72rem;
}

.ebx-seller-name {
  color: var(--bf-text-secondary);
  font-weight: 600;
}

.ebx-verified-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: var(--bf-primary);
  color: #fff;
  font-size: 0.55rem;
  font-weight: 900;
}

.ebx-stars {
  position: relative;
  display: inline-block;
  font-size: 0.72rem;
  line-height: 1;
  letter-spacing: 1px;
}
.ebx-stars-base {
  color: var(--bf-border);
}
.ebx-stars-fill {
  position: absolute;
  top: 0;
  left: 0;
  overflow: hidden;
  white-space: nowrap;
  color: var(--bf-warning);
}

.ebx-review-count {
  color: var(--bf-text-muted);
}

.ebx-desc {
  font-size: var(--bf-font-size-xs);
  color: var(--bf-text-muted);
  margin: 0 0 auto 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

/* Price */
.ebx-price-row {
  display: flex;
  flex-direction: column;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--bf-border-light);
}

.ebx-price {
  font-size: var(--bf-font-size-xl);
  font-weight: 800;
  color: var(--bf-text-primary);
}

.ebx-shipping-note {
  font-size: 0.7rem;
  color: var(--bf-text-muted);
  margin-top: 2px;
}

/* Actions */
.ebx-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.ebx-btn {
  flex: 1;
  padding: 9px 10px;
  border-radius: var(--bf-radius-md);
  font-size: 0.8rem;
  font-weight: 700;
  text-align: center;
  text-decoration: none;
  cursor: pointer;
  border: 1.5px solid transparent;
  transition: all var(--bf-transition-fast);
}

.ebx-btn-outline {
  background: transparent;
  border-color: var(--bf-border);
  color: var(--bf-text-primary);
}
.ebx-btn-outline:hover {
  border-color: var(--bf-primary);
  color: var(--bf-primary);
}

.ebx-btn-primary {
  background: var(--bf-primary);
  color: #fff;
}
.ebx-btn-primary:hover:not(:disabled) {
  background: var(--bf-primary-hover);
}
.ebx-btn-primary:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.ebx-admin-edit {
  margin-top: 8px;
  text-align: center;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 6px;
  border-radius: var(--bf-radius-md);
  border: 1.5px dashed var(--bf-border);
  color: var(--bf-text-muted);
  text-decoration: none;
}
.ebx-admin-edit:hover {
  color: var(--bf-primary);
  border-color: var(--bf-primary);
}
</style>
