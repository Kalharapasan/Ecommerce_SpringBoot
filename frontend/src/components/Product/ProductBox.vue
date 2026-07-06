<template>
  <div class="bf-product-card" @mouseenter="hovered = true" @mouseleave="hovered = false">
    <!-- Image Container -->
    <div class="bf-product-image-wrapper">
      <img
        class="bf-product-image"
        :src="currentImageUrl"
        @error="handleImageError"
        alt="Product Image"
        loading="lazy"
      />
      <!-- Stock Badge -->
      <div class="bf-product-stock-badge" :class="stockClass">
        {{ stockLabel }}
      </div>
      <!-- Quick Actions (shown on hover) -->
      <transition name="page-fade">
        <div v-if="hovered" class="bf-product-quick-actions">
          <router-link
            :to="{ name: 'ProductDetail', params: { id: product.productId } }"
            class="bf-quick-action-btn"
            title="View Details"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16"><path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0"/><path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8m8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7"/></svg>
          </router-link>
        </div>
      </transition>
    </div>

    <!-- Card Body -->
    <div class="bf-product-body">
      <!-- Category Tag -->
      <span v-if="product.category" class="bf-product-category">
        {{ product.category.categoryName }}
      </span>

      <!-- Product Name -->
      <h3 class="bf-product-name">
        <router-link :to="{ name: 'ProductDetail', params: { id: product.productId } }">
          {{ product.productName }}
        </router-link>
      </h3>

      <!-- Description -->
      <p class="bf-product-desc">{{ product.description }}</p>

      <!-- Price & Action Row -->
      <div class="bf-product-footer">
        <div class="bf-product-price">
          <span class="bf-price-amount">{{ formattedPrice }}</span>
        </div>
        <router-link
          :to="{ name: 'ProductDetail', params: { id: product.productId } }"
          class="bf-btn bf-btn-primary bf-btn-sm"
        >
          View
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708"/></svg>
        </router-link>
      </div>

      <!-- Admin Edit Button -->
      <router-link
        v-if="isAdmin"
        :to="{ name: 'EditProduct', params: { id: product.productId } }"
        class="bf-btn bf-btn-outline bf-btn-sm bf-product-admin-btn"
      >
        Edit Product
      </router-link>
    </div>
  </div>
</template>

<script>
import { formatPrice } from '../../utils/api';
import { getCurrentUser } from '../../utils/auth';

export default {
  name: 'ProductBox',
  props: ['product'],
  data() {
    return {
      hovered: false,
      currentImageUrl: this.product.imageUrl,
      fallbackUrl: 'https://placehold.co/300x200?text=No+Image+Available'
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
    stockLabel() {
      if (!this.product.stock || this.product.stock <= 0) return 'Out of Stock';
      if (this.product.stock <= 5) return `Only ${this.product.stock} left`;
      return 'In Stock';
    },
    stockClass() {
      if (!this.product.stock || this.product.stock <= 0) return 'stock-out';
      if (this.product.stock <= 5) return 'stock-low';
      return 'stock-in';
    }
  },
  methods: {
    handleImageError() {
      this.currentImageUrl = this.fallbackUrl;
    }
  }
};
</script>

<style scoped>
.bf-product-card {
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  overflow: hidden;
  transition: all var(--bf-transition-base);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.bf-product-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--bf-shadow-xl);
  border-color: var(--bf-primary-glow);
}

/* Image */
.bf-product-image-wrapper {
  position: relative;
  overflow: hidden;
  aspect-ratio: 4/3;
  background: var(--bf-bg-tertiary);
}

.bf-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.bf-product-card:hover .bf-product-image {
  transform: scale(1.08);
}

/* Stock Badge */
.bf-product-stock-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  border-radius: var(--bf-radius-full);
  font-size: 0.65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.stock-in {
  background: rgba(16, 185, 129, 0.85);
  color: #ffffff;
}

.stock-low {
  background: rgba(245, 158, 11, 0.9);
  color: #ffffff;
}

.stock-out {
  background: rgba(239, 68, 68, 0.85);
  color: #ffffff;
}

/* Quick Actions Overlay */
.bf-product-quick-actions {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  gap: 6px;
}

.bf-quick-action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: var(--bf-text-primary);
  transition: all var(--bf-transition-fast);
  box-shadow: var(--bf-shadow-md);
  text-decoration: none;
}
[data-theme="dark"] .bf-quick-action-btn {
  background: rgba(30, 41, 59, 0.9);
  color: #f1f5f9;
}

.bf-quick-action-btn:hover {
  background: var(--bf-primary);
  color: #ffffff;
  transform: scale(1.1);
}

/* Body */
.bf-product-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.bf-product-category {
  font-size: var(--bf-font-size-xs);
  color: var(--bf-primary);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}

.bf-product-name {
  font-size: var(--bf-font-size-base);
  font-weight: 700;
  margin: 0 0 6px 0;
  line-height: 1.3;
}

.bf-product-name a {
  color: var(--bf-text-primary);
  text-decoration: none;
  transition: color var(--bf-transition-fast);
}
.bf-product-name a:hover {
  color: var(--bf-primary);
}

.bf-product-desc {
  font-size: var(--bf-font-size-xs);
  color: var(--bf-text-muted);
  margin: 0 0 auto 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

/* Footer */
.bf-product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--bf-border-light);
}

.bf-price-amount {
  font-size: var(--bf-font-size-lg);
  font-weight: 800;
  color: var(--bf-primary);
}

/* Admin Edit */
.bf-product-admin-btn {
  margin-top: 8px;
  width: 100%;
  text-align: center;
}
</style>
