<template>
  <div class="bf-page bf-fade-in py-5 text-start">
    <div class="container position-relative">
      
      <!-- Glow effect for background ambience -->
      <div class="glow-bg animate-float"></div>

      <!-- Breadcrumbs navigation -->
      <nav aria-label="breadcrumb" class="mb-4 position-relative z-3">
        <ol class="breadcrumb text-muted small">
          <li class="breadcrumb-item"><router-link to="/" class="text-muted text-decoration-none hover-cyan">Home</router-link></li>
          <li class="breadcrumb-item"><router-link to="/product" class="text-muted text-decoration-none hover-cyan">Products</router-link></li>
          <li class="breadcrumb-item active text-white" aria-current="page" v-if="product">{{ product.productName }}</li>
        </ol>
      </nav>

      <!-- Loading Skeletons -->
      <div v-if="loading" class="row g-4">
        <div class="col-md-6">
          <div class="bf-skeleton" style="height: 450px;"></div>
        </div>
        <div class="col-md-6">
          <div class="bf-skeleton mb-3" style="height: 40px; width: 60%;"></div>
          <div class="bf-skeleton mb-4" style="height: 80px;"></div>
          <div class="bf-skeleton mb-4" style="height: 150px;"></div>
          <div class="bf-skeleton" style="height: 60px;"></div>
        </div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bf-empty-state bf-glass p-5 text-center">
        <div class="bf-empty-icon text-danger fs-1"><i class="bi bi-exclamation-triangle"></i></div>
        <h5 class="text-white font-bold mt-3">Hardware Loading Interrupted</h5>
        <p class="text-muted">{{ error }}</p>
        <button class="bf-btn-premium border-0 mt-3" @click="fetchProduct($route.params.id)">Retry Sync</button>
      </div>

      <!-- Main Product Details -->
      <div v-else-if="product" class="bf-fade-in position-relative z-2">
        <div class="row g-5">
          <!-- Left Column: Media Showcase -->
          <div class="col-lg-6">
            <div class="bf-card bf-glass media-card p-4 d-flex align-items-center justify-content-center position-relative overflow-hidden">
              <div class="details-glow"></div>
              <img
                :src="currentImageUrl"
                @error="handleImageError"
                alt="Product Spec Image"
                class="img-fluid rounded-3 product-detail-img animate-float"
              />
              <span class="product-badge-condition">{{ product.condition || 'Factory Sealed' }}</span>
            </div>
            
            <!-- Quick Features Banner -->
            <div class="row g-3 mt-3">
              <div class="col-6">
                <div class="bf-glass p-3 text-center text-muted small border-0">
                  <i class="bi bi-patch-check text-cyan fs-5 d-block mb-1"></i>
                  1-Year Local Warranty
                </div>
              </div>
              <div class="col-6">
                <div class="bf-glass p-3 text-center text-muted small border-0">
                  <i class="bi bi-cpu text-purple fs-5 d-block mb-1"></i>
                  Tested Benchmarks
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column: Specs Selection -->
          <div class="col-lg-6">
            <div class="bf-glass p-4 p-md-5 h-100 d-flex flex-column justify-content-between border-0 shadow-lg">
              <div>
                <!-- Category badge & Seller Info -->
                <div class="d-flex flex-wrap gap-2 mb-3 align-items-center justify-content-between">
                  <div class="d-flex gap-2">
                    <span class="badge bg-primary bg-opacity-25 text-cyan" v-if="categoryName">
                      {{ categoryName }}
                    </span>
                    <span class="badge" :class="stockBadgeClass">
                      {{ stockLabel }}
                    </span>
                  </div>
                  <router-link 
                    v-if="product.storeId" 
                    :to="'/store/' + product.storeId" 
                    class="text-cyan small font-bold text-decoration-none hover-glow"
                  >
                    <i class="bi bi-shop me-1"></i> {{ product.storeName || 'Partner Store' }}
                  </router-link>
                </div>

                <h1 class="text-white font-bold mb-3 fs-2">{{ product.productName }}</h1>

                <!-- Reviews overview -->
                <div class="d-flex align-items-center gap-2 mb-4">
                  <div class="text-warning">
                    <i class="bi bi-star-fill me-1" v-for="n in 5" :key="n"></i>
                  </div>
                  <span class="text-muted small">4.9 / 5.0 (28 customer benchmarks)</span>
                </div>

                <!-- Price Box -->
                <div class="price-box-details bf-glass p-3 border-0 mb-4 bg-black bg-opacity-20">
                  <span class="text-muted d-block small mb-1">Standard Market Rate</span>
                  <span class="bf-price-amount fs-2 font-bold text-gradient-primary">{{ formattedPrice }}</span>
                </div>

                <!-- Tabs (Overview / Specifications) -->
                <div class="mb-4">
                  <div class="d-flex gap-4 border-bottom border-secondary border-opacity-25 pb-2 mb-3">
                    <button 
                      class="btn btn-sm text-secondary p-0 border-0 font-bold position-relative tab-btn" 
                      :class="{ 'text-cyan active': activeTab === 'desc' }"
                      @click="activeTab = 'desc'"
                    >
                      Overview
                    </button>
                    <button 
                      class="btn btn-sm text-secondary p-0 border-0 font-bold position-relative tab-btn" 
                      :class="{ 'text-cyan active': activeTab === 'specs' }"
                      @click="activeTab = 'specs'"
                    >
                      Specifications
                    </button>
                  </div>

                  <!-- Tab Contents -->
                  <div class="tab-content-details text-secondary small" style="line-height: 1.7;">
                    <div v-if="activeTab === 'desc'" style="white-space: pre-line;" class="bf-fade-in text-muted">
                      {{ product.description }}
                    </div>
                    <div v-else class="bf-fade-in">
                      <table class="table table-sm table-borderless mb-0 text-start text-white">
                        <tbody>
                          <tr class="border-bottom border-secondary border-opacity-10">
                            <td class="text-muted py-2" style="width: 140px;">Module Type:</td>
                            <td class="text-secondary font-semibold py-2">{{ categoryName || 'PC Hardware' }}</td>
                          </tr>
                          <tr class="border-bottom border-secondary border-opacity-10">
                            <td class="text-muted py-2">Part Condition:</td>
                            <td class="text-secondary font-semibold py-2">{{ product.condition || 'Brand New' }}</td>
                          </tr>
                          <tr class="border-bottom border-secondary border-opacity-10">
                            <td class="text-muted py-2">Stock Quantity:</td>
                            <td class="text-secondary font-semibold py-2">{{ product.stock || 'Not Specified' }} units</td>
                          </tr>
                          <tr>
                            <td class="text-muted py-2">Vendor Registry:</td>
                            <td class="text-secondary font-semibold py-2">{{ product.storeName || 'ByteForge Partner' }}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

              </div>

              <!-- Purchase Control Section -->
              <div class="mt-4 pt-4 border-top border-secondary border-opacity-25">
                <div v-if="!isAdmin">
                  <div class="d-flex align-items-center justify-content-between gap-3 mb-4" v-if="product.stock && product.stock > 0">
                    <span class="text-muted small font-bold">QUANTITY CONFIG</span>
                    <div class="bf-qty-stepper-premium bf-glass border-0">
                      <button
                        class="qty-btn"
                        type="button"
                        @click="decrementQty"
                        :disabled="quantity <= 1"
                      >
                        <i class="bi bi-dash"></i>
                      </button>
                      <input
                        type="number"
                        class="qty-input-box text-white"
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
                        <i class="bi bi-plus"></i>
                      </button>
                    </div>
                  </div>

                  <div class="d-flex gap-3">
                    <button
                      class="bf-btn-premium flex-grow-1 py-3 text-center border-0 d-flex align-items-center justify-content-center gap-2"
                      @click="addToCart"
                      :disabled="addingToCart || !product.stock || product.stock <= 0"
                    >
                      <span v-if="addingToCart" class="spinner-border spinner-border-sm" role="status"></span>
                      <i v-else class="bi bi-cart-plus-fill"></i>
                      <span>{{ product.stock && product.stock > 0 ? 'Inject to Cart' : 'Temporarily Out of Stock' }}</span>
                    </button>
                  </div>
                </div>

                <!-- Admin operations console link -->
                <div v-else class="bf-glass p-3 border-danger border-opacity-35" role="alert">
                  <h6 class="font-bold text-danger mb-1 d-flex align-items-center gap-2">
                    <i class="bi bi-shield-lock-fill"></i> Administrator Session
                  </h6>
                  <p class="small text-muted mb-0">Purchasing actions are disabled. Edit product settings using the Admin panel.</p>
                </div>
              </div>

            </div>
          </div>
        </div>

        <!-- Related Items Grid -->
        <div class="mt-5 text-start position-relative z-2" v-if="relatedProducts.length > 0">
          <hr class="my-5 opacity-25 border-secondary" />
          <h4 class="text-white font-bold mb-4">Related Hardware Modules</h4>
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
import { showToast } from '../../components/Common/ToastNotification.vue';

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
      activeTab: 'desc',
      fallbackUrl: 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?auto=format&fit=crop&w=500&q=80'
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
      if (!this.product.stock || this.product.stock <= 0) return 'bg-danger bg-opacity-25 text-danger';
      if (this.product.stock <= 5) return 'bg-warning bg-opacity-25 text-warning';
      return 'bg-success bg-opacity-25 text-success';
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
        if (this.product && this.product.categoryId) {
          this.categoryName = this.product.categoryName;
          const catId = this.product.categoryId;
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
.glow-bg {
  position: absolute;
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.08) 0%, transparent 75%);
  top: -100px; left: -100px;
  border-radius: 50%;
  pointer-events: none;
  z-index: 1;
}

.media-card {
  min-height: 450px;
  border-color: rgba(255, 255, 255, 0.04);
  background: rgba(26, 34, 56, 0.4) !important;
}

.details-glow {
  position: absolute;
  width: 300px; height: 300px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.1) 0%, transparent 70%);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 1;
}

.product-detail-img {
  max-height: 420px;
  width: 100%;
  object-fit: contain;
  z-index: 2;
}

.product-badge-condition {
  position: absolute;
  top: 20px; left: 20px;
  background: rgba(11, 15, 25, 0.85);
  color: #fff;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 0.72rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 3;
}

.tab-btn {
  transition: all 0.3s ease;
}
.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -2px; left: 0; right: 0;
  height: 2px;
  background: var(--bf-cyan);
  box-shadow: 0 0 8px var(--bf-cyan);
}

.bf-qty-stepper-premium {
  display: inline-flex;
  align-items: center;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--bf-radius-md);
}

.qty-btn {
  background: transparent;
  border: none;
  color: white;
  padding: 10px 18px;
  cursor: pointer;
  transition: background 0.2s ease;
}
.qty-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.08);
}
.qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-input-box {
  width: 50px;
  border: none;
  background: transparent;
  text-align: center;
  font-weight: 700;
  outline: none;
}
.qty-input-box::-webkit-outer-spin-button,
.qty-input-box::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
</style>
