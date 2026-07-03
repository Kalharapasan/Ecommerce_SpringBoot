<template>
  <div class="container my-5">
    <!-- Loading Spinner -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading product details...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger" role="alert">
      {{ error }}
    </div>

    <!-- Product Details -->
    <div v-else-if="product">
      <div class="row">
        <div class="col-md-6 mb-4 mb-md-0">
          <div class="image-container shadow-sm p-3 bg-white rounded">
            <img 
              :src="currentImageUrl" 
              @error="handleImageError" 
              alt="Product Image" 
              class="img-fluid rounded" 
            />
          </div>
        </div>
        
        <div class="col-md-6">
          <div class="product-info-container bg-white p-4 p-md-5 rounded shadow-sm text-start">
            <!-- Category Badge & Stock Pill -->
            <div class="d-flex flex-wrap gap-2 mb-3 align-items-center">
              <span class="badge bg-secondary text-uppercase py-2 px-3" v-if="categoryName">
                {{ categoryName }}
              </span>
              <span class="badge bg-success py-2 px-3" v-if="product.stock && product.stock > 0">IN STOCK</span>
              <span class="badge bg-danger py-2 px-3" v-else>OUT OF STOCK</span>
            </div>

            <h2 class="font-weight-bold mb-1 text-start">{{ product.productName }}</h2>
            
            <!-- Rating stars block -->
            <div class="d-flex align-items-center gap-2 mb-3">
              <div class="text-warning">
                ★ ★ ★ ★ ★
              </div>
              <span class="text-muted small font-weight-bold">4.8 / 5.0 (42 reviews)</span>
            </div>

            <h4 class="text-primary font-weight-bold mb-3 text-start">{{ formattedPrice }}</h4>
            
            <div class="mb-4" v-if="product.stock !== undefined && product.stock !== null">
              <span class="small font-weight-bold" :class="{'text-danger': !product.stock || product.stock === 0, 'text-muted': product.stock > 0}">
                Availability: 
                <span v-if="product.stock > 0">{{ product.stock }} items remaining</span>
                <span v-else>Out of stock</span>
              </span>
            </div>

            <hr />
            
            <p class="text-muted mb-4 lead text-start" style="white-space: pre-line;">
              {{ product.description }}
            </p>
            
            <hr />

            <!-- Add to Cart / Quantity Stepper Section -->
            <div class="d-flex align-items-center gap-3 mb-4" v-if="product.stock && product.stock > 0">
              <span class="text-secondary">Quantity:</span>
              <div class="input-group quantity-stepper">
                <button 
                  class="btn btn-outline-secondary" 
                  type="button" 
                  @click="decrementQty"
                  :disabled="quantity <= 1"
                >
                  -
                </button>
                <input 
                  type="number" 
                  class="form-control text-center" 
                  v-model.number="quantity" 
                  min="1"
                  :max="product.stock"
                  @change="validateQty"
                />
                <button 
                  class="btn btn-outline-secondary" 
                  type="button" 
                  @click="incrementQty"
                  :disabled="quantity >= product.stock"
                >
                  +
                </button>
              </div>
            </div>

            <button 
              class="btn btn-primary btn-lg w-100 py-3 d-flex align-items-center justify-content-center gap-2"
              @click="addToCart"
              :disabled="addingToCart || !product.stock || product.stock <= 0"
            >
              <span v-if="addingToCart" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              {{ product.stock && product.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Related Products Section -->
      <div class="mt-5" v-if="relatedProducts.length > 0">
        <hr />
        <h4 class="text-start mb-4 font-weight-bold">Related Products</h4>
        <div class="row">
          <div 
            v-for="related in relatedProducts" 
            :key="related.productId" 
            class="col-xl-3 col-md-6 d-flex mb-4"
          >
            <ProductBox :product="related" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { isLoggedIn, getToken, authState } from '../../utils/auth';
import ProductBox from '../../components/Product/ProductBox.vue';

export default {
  name: 'ProductDetail',
  components: { ProductBox },
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
      fallbackUrl: 'https://via.placeholder.com/500x400?text=No+Image+Available'
    };
  },
  computed: {
    formattedPrice() {
      return this.product ? formatPrice(this.product.price) : '';
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

        // Fetch related products in the same category & resolve category name
        if (this.product && this.product.categoryId) {
          try {
            const relResponse = await api.get(`/category/${this.product.categoryId}/product`);
            const allProducts = relResponse.data.data || [];
            this.relatedProducts = allProducts.filter(
              p => p.productId !== this.product.productId
            ).slice(0, 4);
          } catch (relErr) {
            console.warn('Failed to fetch related products:', relErr);
          }

          try {
            const catResponse = await api.get('/category');
            const categoriesList = catResponse.data.data || [];
            const matchingCat = categoriesList.find(c => c.categoryId === this.product.categoryId);
            if (matchingCat) {
              this.categoryName = matchingCat.categoryName;
            }
          } catch (catErr) {
            console.warn('Failed to fetch category details:', catErr);
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
        alert('Please sign in to add items to your cart.');
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
        alert(response.data.message || 'Product added to cart successfully!');
        this.$router.push({ name: 'CartView' });
      } catch (err) {
        alert(extractErrorMessage(err));
      } finally {
        this.addingToCart = false;
      }
    }
  }
};
</script>

<style scoped>
.image-container img {
  max-height: 450px;
  width: 100%;
  object-fit: contain;
}
.quantity-stepper {
  width: 130px;
}
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
input[type=number] {
  -moz-appearance: textfield;
}
</style>
