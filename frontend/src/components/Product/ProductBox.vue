<template>
  <div class="card w-100 h-100 shadow-sm border-0">
    <div class="embed-responsive embed-responsive-16by9 img-container">
      <img 
        class="card-img-top" 
        :src="currentImageUrl" 
        @error="handleImageError" 
        alt="Product Image" 
      />
    </div>
    <div class="card-body d-flex flex-column">
      <h5 class="card-title text-start">{{ product.productName }}</h5>
      <h6 class="card-subtitle mb-2 text-primary text-start font-weight-bold">
        {{ formattedPrice }}
      </h6>
      <p class="card-text text-start text-muted flex-grow-1 text-truncate-2">
        {{ product.description }}
      </p>
      <div class="mt-auto">
        <router-link 
          :to="{ name: 'ProductDetail', params: { id: product.productId } }" 
          class="btn btn-primary w-100 mb-2"
        >
          View Detail
        </router-link>
        <router-link 
          v-if="isAdmin" 
          :to="{ name: 'EditProduct', params: { id: product.productId } }" 
          class="btn btn-outline-secondary w-100"
        >
          Edit
        </router-link>
      </div>
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
.card-img-top {
  object-fit: cover;
  height: 200px;
  width: 100%;
}
.img-container {
  overflow: hidden;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}
.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card {
  border-radius: 12px;
  transition: transform 0.2s;
}
.card:hover {
  transform: translateY(-5px);
}
</style>
