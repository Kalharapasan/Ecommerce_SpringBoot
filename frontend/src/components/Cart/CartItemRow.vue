<template>
  <tr class="align-middle border-light-row">
    <!-- Product details column -->
    <td class="py-4">
      <div class="d-flex align-items-center gap-3">
        <div class="bf-cart-thumbnail-wrapper bf-glass border-0">
          <img 
            :src="currentImageUrl" 
            @error="handleImageError" 
            alt="Product Thumbnail" 
            class="bf-cart-thumbnail"
          />
        </div>
        <div class="text-start">
          <h6 class="mb-1 font-bold text-white fs-6">{{ item.product.productName }}</h6>
          <small class="text-muted d-block mb-1">Seller: <span class="text-secondary font-semibold">{{ item.product.store ? item.product.store.storeName : 'Marketplace Vendor' }}</span></small>
          <small class="text-muted">Unit Rate: <span class="text-white">{{ formattedUnitPrice }}</span></small>
        </div>
      </div>
    </td>
    
    <!-- Quantity configuration -->
    <td class="py-4">
      <div class="bf-qty-stepper-cyber bf-glass border-0">
        <button 
          class="qty-control-btn" 
          type="button" 
          @click="decrement"
          :disabled="item.count <= 1 || loading"
        >
          <i class="bi bi-dash"></i>
        </button>
        <input 
          type="number" 
          class="qty-display-box text-white" 
          v-model.number="count"
          min="1"
          @change="validateAndEmit"
          :disabled="loading"
        />
        <button 
          class="qty-control-btn" 
          type="button" 
          @click="increment"
          :disabled="loading"
        >
          <i class="bi bi-plus"></i>
        </button>
      </div>
    </td>
    
    <!-- Total Cost -->
    <td class="py-4 text-start">
      <span class="font-bold text-gradient-primary fs-5">{{ formattedSubtotal }}</span>
    </td>
    
    <!-- Delete Action -->
    <td class="py-4">
      <button 
        class="bf-btn-ghost text-danger border-danger border-opacity-25 px-3 py-1.5 small" 
        @click="remove"
        :disabled="loading"
      >
        <i class="bi bi-trash-fill"></i>
      </button>
    </td>
  </tr>
</template>

<script>
import { formatPrice } from '../../utils/api';

export default {
  name: 'CartItemRow',
  props: {
    item: {
      type: Object,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      count: this.item.count,
      currentImageUrl: this.item.product.imageUrl,
      fallbackUrl: 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?auto=format&fit=crop&w=100&q=80'
    };
  },
  watch: {
    'item.count'(newVal) {
      this.count = newVal;
    },
    'item.product.imageUrl'(newVal) {
      this.currentImageUrl = newVal;
    }
  },
  computed: {
    formattedUnitPrice() {
      return formatPrice(this.item.product.price);
    },
    formattedSubtotal() {
      return formatPrice(this.item.product.price * this.item.count);
    }
  },
  methods: {
    handleImageError() {
      this.currentImageUrl = this.fallbackUrl;
    },
    increment() {
      this.count += 1;
      this.validateAndEmit();
    },
    decrement() {
      if (this.count > 1) {
        this.count -= 1;
        this.validateAndEmit();
      }
    },
    validateAndEmit() {
      if (!Number.isInteger(this.count) || this.count < 1) {
        this.count = 1;
      }
      this.$emit('update-qty', {
        cartId: this.item.cartId,
        count: this.count
      });
    },
    remove() {
      if (confirm(`Remove "${this.item.product.productName}" from cart?`)) {
        this.$emit('remove', this.item.cartId);
      }
    }
  }
};
</script>

<style scoped>
.border-light-row {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.bf-cart-thumbnail-wrapper {
  width: 76px;
  height: 76px;
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.bf-cart-thumbnail-wrapper:hover {
  box-shadow: 0 0 10px rgba(6, 182, 212, 0.2);
}

.bf-cart-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.bf-qty-stepper-cyber {
  display: inline-flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 8px;
}

.qty-control-btn {
  background: transparent;
  border: none;
  color: white;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s ease;
}

.qty-control-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
}
.qty-control-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-display-box {
  width: 44px;
  border: none;
  background: transparent;
  text-align: center;
  font-weight: 700;
  font-size: 0.85rem;
  outline: none;
}
.qty-display-box::-webkit-outer-spin-button,
.qty-display-box::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
