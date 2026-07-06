<template>
  <div class="bf-page bf-fade-in p-4 text-start">
    <div class="container">
      
      <!-- Breadcrumbs navigation -->
      <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb text-muted small">
          <li class="breadcrumb-item"><router-link to="/">Home</router-link></li>
          <li class="breadcrumb-item"><router-link to="/product">Products</router-link></li>
          <li class="breadcrumb-item active text-white" aria-current="page" v-if="product">{{ product.productName }}</li>
        </ol>
      </nav>

      <!-- Loading Skeletons -->
      <div v-if="loading" class="row g-4">
        <div class="col-md-6">
          <LoadingSkeleton type="block" height="420px" />
        </div>
        <div class="col-md-6">
          <LoadingSkeleton type="text" :lines="8" titleWidth="70%" />
          <div class="mt-4">
            <LoadingSkeleton type="block" height="60px" />
          </div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bf-empty-state">
        <div class="bf-empty-icon text-danger">⚠</div>
        <h5 class="text-white font-weight-bold">Hardware Loading Interrupted</h5>
        <p>{{ error }}</p>
        <button class="bf-btn bf-btn-primary" @click="fetchProduct($route.params.id)">Retry Sync</button>
      </div>

      <!-- Main Product Specs Info -->
      <div v-else-if="product" class="bf-fade-in">
        <div class="row g-4">
          <!-- Left Column: Media Showcase -->
          <div class="col-lg-6">
            <div class="bf-card bf-glass media-card p-4 d-flex align-items-center justify-content-center position-relative overflow-hidden">
              <div class="details-glow"></div>
              <img
                :src="currentImageUrl"
                @error="handleImageError"
                alt="Product Spec Image"
                class="img-fluid rounded-3 product-detail-img"
              />
              <span class="product-badge-condition">{{ product.condition || 'Factory Sealed' }}</span>
            </div>
            
            <!-- Quick features list under image -->
            <div class="row g-2 mt-2">
              <div class="col-6">
                <div class="p-3 rounded border text-center text-muted small">
                  🛡️ 1-Year Local Warranty
                </div>
              </div>
              <div class="col-6">
                <div class="p-3 rounded border text-center text-muted small">
                  ⚡ Tested Benchmarks
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column: Specs Selection -->
          <div class="col-lg-6">
            <div class="bf-card bf-glass p-4 p-md-5 h-100 d-flex flex-column justify-content-between">
              <div>
                <!-- Category badge & Seller reference -->
                <div class="d-flex flex-wrap gap-2 mb-3 align-items-center justify-content-between">
                  <div class="d-flex gap-2">
                    <span class="bf-badge bf-badge-primary text-uppercase" v-if="categoryName">
                      {{ categoryName }}
                    </span>
                    <span class="bf-badge" :class="stockBadgeClass">
                      {{ stockLabel }}
                    </span>
                  </div>
                  <router-link 
                    v-if="product.storeId" 
                    :to="'/store/' + product.storeId" 
                    class="text-primary small font-weight-bold"
                  >
                    🏢 {{ product.storeName || 'Partner Store' }} 🛡️
                  </router-link>
                </div>

                <h1 class="font-weight-bold mb-3 fs-3">{{ product.productName }}</h1>

                <!-- Reviews overview -->
                <div class="d-flex align-items-center gap-2 mb-4">
                  <div class="text-warning">
                    ★ ★ ★ ★ ★
                  </div>
                  <span class="text-muted small font-weight-bold">4.9 / 5.0 (28 customer benchmarks)</span>
                </div>

                <!-- Price Rates -->
                <div class="price-box-details p-3 rounded border mb-4">
                  <span class="text-muted d-block small mb-1">Standard Market Rate</span>
                  <span class="bf-price-amount fs-2 font-weight-bold text-primary">{{ formattedPrice }}</span>
                </div>

                <!-- Description & Details Tabs -->
                <div class="mb-4">
                  <div class="d-flex gap-3 border-bottom pb-2 mb-3">
                    <button 
                      class="btn btn-sm text-secondary p-0 border-0 font-weight-bold" 
                      :class="{ 'text-primary': activeTab === 'desc' }"
                      @click="activeTab = 'desc'"
                    >
                      Overview
                    </button>
                    <button 
                      class="btn btn-sm text-secondary p-0 border-0 font-weight-bold" 
                      :class="{ 'text-primary': activeTab === 'specs' }"
                      @click="activeTab = 'specs'"
                    >
                      Specifications
                    </button>
                  </div>

                  <div class="tab-content-details text-secondary small" style="line-height: 1.6;">
                    <div v-if="activeTab === 'desc'" style="white-space: pre-line;">
                      {{ product.description }}
                    </div>
                    <div v-else>
                      <table class="table table-sm table-borderless mb-0 text-start">
                        <tbody>
                          <tr>
                            <td class="text-muted" style="width: 140px;">Module Type:</td>
                            <td class="text-secondary font-weight-bold">{{ categoryName || 'PC Hardware' }}</td>
                          </tr>
                          <tr>
                            <td class="text-muted">Part Condition:</td>
                            <td class="text-secondary font-weight-bold">{{ product.condition || 'Brand New' }}</td>
                          </tr>
                          <tr>
                            <td class="text-muted">Stock Quantity:</td>
                            <td class="text-secondary font-weight-bold">{{ product.stock || 'Not Specified' }} units</td>
                          </tr>
                          <tr>
                            <td class="text-muted">Vendor Registry:</td>
                            <td class="text-secondary font-weight-bold">{{ product.storeName || 'ByteForge Partner' }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

              </div>

              <!-- Purchase control section -->
              <div class="mt-4 pt-4 border-top">
                <div v-if="!isAdmin">
                  <div class="d-flex align-items-center justify-content-between gap-3 mb-4" v-if="product.stock && product.stock > 0">
                    <span class="text-muted small font-weight-bold">QUANTITY CONFIG</span>
                    <div class="bf-qty-stepper-premium">
                      <button
                        class="qty-btn"
                        type="button"
                        @click="decrementQty"
                        :disabled="quantity <= 1"
                      >
                        -
                      </button>
                      <input
                        type="number"
                        class="qty-input-box"
                        v-model.number="quantity"
                        min="1"
                        :max="product.stock"
                        @change="validateQty"
                      />
                      <button
                        class="qty-btn"
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
                      class="bf-btn bf-btn-primary flex-grow-1 bf-btn-lg py-3 shadow-glow"
                      @click="addToCart"
                      :disabled="addingToCart || !product.stock || product.stock <= 0"
                    >
                      <span v-if="addingToCart" class="spinner-border spinner-border-sm me-2"></span>
                      {{ product.stock && product.stock > 0 ? 'Inject to Cart' : 'Temporarily Out of Stock' }}
                    </button>
                  </div>
                </div>

                <!-- Admin operations console link -->
                <div v-else class="bf-card p-3 bg-dark border-danger" role="alert">
                  <h6 class="font-weight-bold text-danger mb-1">Administrator Session</h6>
                  <p class="small text-muted mb-0">Purchasing acts are locked. Configure item fields from operations panel.</p>
                </div>
              </div>

            </div>
          </div>
        </div>

        <!-- Related Items Grid -->
        <div class="mt-5 text-start" v-if="relatedProducts.length > 0">
          <hr class="my-5 opacity-25" />
          <h4 class="text-white font-weight-bold mb-4">Related Hardware Modules</h4>
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
      activeTab: 'desc',
      fallbackUrl: 'https://placehold.co/600x450?text=No+Image+Available'
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

        // Fetch related catalog items
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
.media-card {
  min-height: 400px;
  background: var(--bf-bg-card) !important;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.details-glow {
  position: absolute;
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.08) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  pointer-events: none;
}

.product-detail-img {
  max-height: 420px;
  width: 100%;
  object-fit: contain;
  z-index: 2;
  transition: transform var(--bf-transition-base);
}

.product-detail-img:hover {
  transform: scale(1.03);
}

.product-badge-condition {
  position: absolute;
  top: 16px;
  left: 16px;
  background: var(--bf-bg-sidebar);
  color: white;
  padding: 4px 12px;
  border-radius: var(--bf-radius-md);
  font-size: 0.68rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 3;
}

.price-box-details {
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.bf-qty-stepper-premium {
  display: inline-flex;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
}

.qty-btn {
  background: transparent;
  border: none;
  color: white;
  padding: 8px 16px;
  cursor: pointer;
  font-weight: 700;
  transition: all var(--bf-transition-fast);
}

.qty-btn:hover {
  background: rgba(255, 255, 255, 0.08);
}

.qty-input-box {
  width: 55px;
  border: none;
  background: transparent;
  color: white;
  text-align: center;
  font-weight: 700;
  outline: none;
  -moz-appearance: textfield;
}

.qty-input-box::-webkit-outer-spin-button,
.qty-input-box::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
