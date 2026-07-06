<template>
  <tr class="align-middle border-light-row">
    <td>
      <div class="d-flex align-items-center gap-3">
        <div class="bf-cart-thumbnail-wrapper">
          <img 
            :src="currentImageUrl" 
            @error="handleImageError" 
            alt="Product Thumbnail" 
            class="bf-cart-thumbnail"
          />
        </div>
        <div class="text-start">
          <h6 class="mb-0 font-weight-bold text-white">{{ item.product.productName }}</h6>
          <small class="text-muted">Unit Price: {{ formattedUnitPrice }}</small>
        </div>
      </div>
    </td>
    <td>
      <div class="bf-qty-stepper-cyber">
        <button 
          class="qty-control-btn" 
          type="button" 
          @click="decrement"
          :disabled="item.count <= 1 || loading"
        >
          -
        </button>
        <input 
          type="number" 
          class="qty-display-box" 
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
          +
        </button>
      </div>
    </td>
    <td class="text-start">
      <span class="font-weight-bold text-primary">{{ formattedSubtotal }}</span>
    </td>
    <td>
      <button 
        class="bf-btn bf-btn-ghost bf-btn-danger bf-btn-sm px-3" 
        @click="remove"
        :disabled="loading"
      >
        Remove
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
      fallbackUrl: 'https://placehold.co/80x80?text=No+Img'
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
  width: 72px;
  height: 72px;
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: var(--bf-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bf-cart-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.bf-qty-stepper-cyber {
  display: inline-flex;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.02);
}

.qty-control-btn {
  background: transparent;
  border: none;
  color: white;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-weight: 700;
  transition: all var(--bf-transition-fast);
}

.qty-control-btn:hover {
  background: rgba(255, 255, 255, 0.06);
}

.qty-display-box {
  width: 40px;
  border: none;
  background: transparent;
  color: white;
  text-align: center;
  font-weight: 700;
  font-size: var(--bf-font-size-xs);
  outline: none;
  -moz-appearance: textfield;
}

.qty-display-box::-webkit-outer-spin-button,
.qty-display-box::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
