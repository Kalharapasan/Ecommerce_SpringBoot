<template>
  <div class="bf-page bf-fade-in p-4">
    <div class="container">
      <!-- Loading Skeletons -->
      <div v-if="loading" class="row g-4 mt-2">
        <div class="col-md-6">
          <LoadingSkeleton type="block" height="450px" />
        </div>
        <div class="col-md-6">
          <LoadingSkeleton type="text" :lines="6" titleWidth="80%" />
          <div class="mt-4">
            <LoadingSkeleton type="block" height="50px" />
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bf-empty-state">
        <div class="bf-empty-icon" style="color: var(--bf-danger);">⚠</div>
        <h5>Unable to load details</h5>
        <p>{{ error }}</p>
        <button class="bf-btn bf-btn-primary" @click="fetchProduct($route.params.id)">Try Again</button>
      </div>

      <!-- Product Details -->
      <div v-else-if="product" class="bf-fade-in">
        <div class="row g-4">
          <!-- Gallery / Image View -->
          <div class="col-lg-6">
            <div class="bf-card bf-glass p-3 h-100 d-flex align-items-center justify-content-center position-relative">
              <img
                :src="currentImageUrl"
                @error="handleImageError"
                alt="Product Image"
                class="img-fluid rounded-3 bf-product-zoom-img"
              />
              <span class="bf-product-condition-tag">{{ product.condition || 'Brand New' }}</span>
            </div>
          </div>

          <!-- Product Details Form/Info -->
          <div class="col-lg-6">
            <div class="bf-card p-4 p-md-5 text-start h-100 d-flex flex-column justify-content-between">
              <div>
                <!-- Category & Stock Status -->
                <div class="d-flex flex-wrap gap-2 mb-3 align-items-center">
                  <span class="bf-badge bf-badge-primary text-uppercase" v-if="categoryName">
                    {{ categoryName }}
                  </span>
                  <span class="bf-badge" :class="stockBadgeClass">
                    {{ stockLabel }}
                  </span>
                </div>

                <h1 class="bf-hero-title text-dark mb-2" style="font-size: 1.875rem;">{{ product.productName }}</h1>

                <!-- Rating -->
                <div class="d-flex align-items-center gap-2 mb-3">
                  <div class="text-warning font-weight-bold">
                    ★ ★ ★ ★ ★
                  </div>
                  <span class="text-muted small font-weight-bold">4.8 / 5.0 (42 ratings)</span>
                </div>

                <!-- Price -->
                <h3 class="bf-price-amount mb-4" style="font-size: 1.75rem;">{{ formattedPrice }}</h3>

                <!-- Stock Limit Indicator -->
                <div class="mb-4" v-if="product.stock !== undefined && product.stock !== null">
                  <span class="small font-weight-bold" :class="{'text-danger': !product.stock || product.stock === 0, 'text-muted': product.stock > 0}">
                    Availability:
                    <span v-if="product.stock > 0">{{ product.stock }} components available in stock</span>
                    <span v-else>Out of Stock</span>
                  </span>
                </div>

                <hr class="my-4" />

                <!-- Description -->
                <p class="text-secondary mb-4" style="white-space: pre-line; line-height: 1.6;">
                  {{ product.description }}
                </p>
              </div>

              <div>
                <hr class="my-4" />

                <!-- Add to Cart or Administrator Mode -->
                <div v-if="!isAdmin">
                  <div class="d-flex align-items-center gap-3 mb-4" v-if="product.stock && product.stock > 0">
                    <span class="text-secondary font-weight-bold">Quantity:</span>
                    <div class="bf-qty-stepper">
                      <button
                        class="bf-btn bf-btn-ghost bf-btn-icon"
                        type="button"
                        @click="decrementQty"
                        :disabled="quantity <= 1"
                      >
                        -
                      </button>
                      <input
                        type="number"
                        class="bf-qty-input"
                        v-model.number="quantity"
                        min="1"
                        :max="product.stock"
                        @change="validateQty"
                      />
                      <button
                        class="bf-btn bf-btn-ghost bf-btn-icon"
                        type="button"
                        @click="incrementQty"
                        :disabled="quantity >= product.stock"
                      >
                        +
                      </button>
                    </div>
                  </div>

                  <div class="d-flex gap-3">
                    <button
                      class="bf-btn bf-btn-primary flex-grow-1 bf-btn-lg py-3"
                      @click="addToCart"
                      :disabled="addingToCart || !product.stock || product.stock <= 0"
                    >
                      <span v-if="addingToCart" class="bf-spinner me-2"></span>
                      {{ product.stock && product.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}
                    </button>
                  </div>
                </div>

                <!-- Admin alert -->
                <div v-else class="bf-card bf-glass p-3 border-danger-light" role="alert">
                  <h6 class="font-weight-bold text-danger mb-1">Administrator Control Mode</h6>
                  <p class="small text-secondary mb-0">Admins can browse product detail views but are restricted from shopping cart purchases.</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Related Products Grid -->
        <div class="mt-5 text-start" v-if="relatedProducts.length > 0">
          <hr class="my-5" />
          <h4 class="font-weight-bold text-dark mb-4">Related PC Components</h4>
          <div class="row g-4">
            <div
              v-for="related in relatedProducts"
              :key="related.productId"
              class="col-xl-3 col-md-6 d-flex"
            >
              <ProductBox :product="related" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { isLoggedIn, getToken, authState, getCurrentUser } from '../../utils/auth';
import ProductBox from '../../components/Product/ProductBox.vue';
import LoadingSkeleton from '../../components/Common/LoadingSkeleton.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'ProductDetail',
  components: { ProductBox, LoadingSkeleton },
  data() {
    return {
      product: null,
      relatedProducts: [],
      categoryName: '',
      quantity: 1,
      loading: true,
      error: null,
      addingToCart: false,
      currentImageUrl: '',
      fallbackUrl: 'https://placehold.co/500x400?text=No+Image+Available'
    };
  },
  computed: {
    isAdmin() {
      const user = getCurrentUser();
      return user && user.role === 'ADMIN';
    },
    formattedPrice() {
      return this.product ? formatPrice(this.product.price) : '';
    },
    stockLabel() {
      if (!this.product.stock || this.product.stock <= 0) return 'OUT OF STOCK';
      if (this.product.stock <= 5) return 'LOW STOCK';
      return 'IN STOCK';
    },
    stockBadgeClass() {
      if (!this.product.stock || this.product.stock <= 0) return 'bf-badge-danger';
      if (this.product.stock <= 5) return 'bf-badge-warning';
      return 'bf-badge-success';
    }
  },
  watch: {
    '$route.params.id': {
      async handler(newId) {
        if (newId) {
          this.quantity = 1;
          await this.fetchProduct(newId);
        }
      },
      immediate: true
    }
  },
  methods: {
    async fetchProduct(productId) {
      this.loading = true;
      this.error = null;
      this.relatedProducts = [];
      this.categoryName = '';
      try {
        const response = await api.get(`/product/${productId}`);
        this.product = response.data.data;
        this.currentImageUrl = this.product.imageUrl;

        // Fetch related products and resolve category name
        if (this.product && this.product.category) {
          this.categoryName = this.product.category.categoryName;
          const catId = this.product.category.categoryId;
          try {
            const relResponse = await api.get(`/category/${catId}/product`);
            const allProducts = relResponse.data.data || [];
            this.relatedProducts = allProducts.filter(
              p => p.productId !== this.product.productId
            ).slice(0, 4);
          } catch (relErr) {
            console.warn('Failed to fetch related products:', relErr);
          }
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    },
    handleImageError() {
      this.currentImageUrl = this.fallbackUrl;
    },
    incrementQty() {
      if (this.product && this.product.stock && this.quantity < this.product.stock) {
        this.quantity += 1;
      }
    },
    decrementQty() {
      if (this.quantity > 1) {
        this.quantity -= 1;
      }
    },
    validateQty() {
      if (!Number.isInteger(this.quantity) || this.quantity < 1) {
        this.quantity = 1;
      }
      if (this.product && this.product.stock && this.quantity > this.product.stock) {
        this.quantity = this.product.stock;
      }
    },
    async addToCart() {
      if (!isLoggedIn()) {
        showToast({
          message: 'Please sign in to add items to your shopping cart.',
          type: 'warning',
          title: 'Authentication Required'
        });
        this.$router.push({
          name: 'SignIn',
          query: { redirect: this.$route.fullPath }
        });
        return;
      }

      this.addingToCart = true;
      try {
        const token = getToken();
        const payload = {
          productId: this.product.productId,
          count: this.quantity
        };

        const response = await api.post(`/cart/${token}`, payload);
        authState.cartCount += this.quantity;
        showToast({
          message: response.data.message || 'Added to shopping cart successfully!',
          type: 'success',
          title: 'Cart Updated'
        });
        this.$router.push({ name: 'CartView' });
      } catch (err) {
        showToast({
          message: extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      } finally {
        this.addingToCart = false;
      }
    }
  }
};
</script>

<style scoped>
.bf-product-zoom-img {
  max-height: 480px;
  width: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.bf-product-zoom-img:hover {
  transform: scale(1.05);
}

.bf-product-condition-tag {
  position: absolute;
  top: 16px;
  left: 16px;
  background: var(--bf-bg-sidebar);
  color: white;
  padding: 4px 12px;
  border-radius: var(--bf-radius-md);
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.bf-qty-stepper {
  display: inline-flex;
  align-items: center;
  border: 1.5px solid var(--bf-border);
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  background: var(--bf-bg-secondary);
}

.bf-qty-input {
  width: 50px;
  border: none;
  text-align: center;
  font-weight: 700;
  color: var(--bf-text-primary);
  background: transparent;
  outline: none;
  -moz-appearance: textfield;
}

.bf-qty-input::-webkit-outer-spin-button,
.bf-qty-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
