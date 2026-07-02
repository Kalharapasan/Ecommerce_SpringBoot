<template>
  <tr class="align-middle">
    <td>
      <div class="d-flex align-items-center gap-3">
        <img 
          :src="currentImageUrl" 
          @error="handleImageError" 
          alt="Product Thumbnail" 
          class="img-thumbnail img-fluid rounded" 
          style="width: 80px; height: 80px; object-fit: cover;"
        />
        <div class="text-start">
          <h6 class="mb-0 font-weight-bold">{{ item.product.productName }}</h6>
          <small class="text-muted">Unit Price: {{ formattedUnitPrice }}</small>
        </div>
      </div>
    </td>
    <td>
      <div class="input-group quantity-stepper">
        <button 
          class="btn btn-sm btn-outline-secondary" 
          type="button" 
          @click="decrement"
          :disabled="item.count <= 1 || loading"
        >
          -
        </button>
        <input 
          type="number" 
          class="form-control form-control-sm text-center" 
          v-model.number="count"
          min="1"
          @change="validateAndEmit"
          :disabled="loading"
        />
        <button 
          class="btn btn-sm btn-outline-secondary" 
          type="button" 
          @click="increment"
          :disabled="loading"
        >
          +
        </button>
      </div>
    </td>
    <td class="text-start">
      <span class="font-weight-bold text-dark">{{ formattedSubtotal }}</span>
    </td>
    <td>
      <button 
        class="btn btn-sm btn-danger d-flex align-items-center justify-content-center gap-1" 
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
      fallbackUrl: 'https://via.placeholder.com/80x80?text=No+Img'
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
.quantity-stepper {
  width: 100px;
}
/* Hide default arrows on number inputs */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type=number] {
  -moz-appearance: textfield;
}
</style>
