<template>
  <div class="bf-page bf-fade-in">
    <!-- Banner Area -->
    <div class="store-banner-wrapper position-relative">
      <img
        :src="store.storeBannerUrl || defaultBanner"
        class="store-banner-img"
        alt="Store Banner"
        @error="handleBannerError"
      />
      <div class="banner-overlay"></div>
    </div>

    <div class="container store-content-container">
      <!-- Store Info Card -->
      <div class="bf-card bf-glass store-profile-card p-4 text-start mb-4 position-relative">
        <div class="d-flex flex-wrap gap-4 align-items-end justify-content-between">
          <!-- Logo & Basic info -->
          <div class="d-flex align-items-center gap-3 flex-wrap">
            <div class="store-logo-wrapper">
              <img
                :src="store.storeLogoUrl || defaultLogo"
                class="store-logo-img rounded"
                alt="Store Logo"
                @error="handleLogoError"
              />
            </div>
            <div>
              <h2 class="font-weight-bold text-dark mb-1 d-flex align-items-center gap-2">
                {{ store.storeName }}
                <span v-if="store.isVerified" class="verification-badge" title="Verified Seller">🛡️</span>
              </h2>
              <p class="text-secondary small mb-2">{{ store.storeDescription || 'Premium marketplace seller.' }}</p>
              
              <div class="d-flex align-items-center gap-3 flex-wrap text-muted small">
                <span>❤️ <strong>{{ store.followersCount }}</strong> Followers</span>
                <span>★ <strong>{{ store.rating }}</strong> ({{ store.reviewsCount }} Reviews)</span>
                <span>📅 Member Since: <strong>{{ formatYear(store.createdDate) }}</strong></span>
              </div>
            </div>
          </div>

          <!-- Actions (Follow) -->
          <div class="d-flex gap-2">
            <button
              class="bf-btn"
              :class="isFollowing ? 'bf-btn-outline' : 'bf-btn-primary'"
              @click="toggleFollow"
              :disabled="followLoading"
            >
              <span v-if="followLoading" class="bf-spinner me-1"></span>
              {{ isFollowing ? '❤️ Following' : '🤍 Follow Store' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Tab Navigation -->
      <div class="d-flex gap-2 mb-4 border-bottom pb-2">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'products' }"
          @click="activeTab = 'products'"
        >
          🔌 Products Catalog
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'reviews' }"
          @click="activeTab = 'reviews'"
        >
          📝 Reviews & Store Policies
        </button>
      </div>

      <!-- Tab Contents -->
      <div class="tab-content text-start">
        <!-- Products Tab -->
        <div v-if="activeTab === 'products'" class="bf-fade-in">
          <!-- Search Bar -->
          <div class="row mb-4">
            <div class="col-md-6">
              <div class="position-relative">
                <input
                  type="text"
                  class="bf-input ps-4 py-2.5"
                  v-model="searchQuery"
                  @input="debounceSearch"
                  placeholder="Search products in this store..."
                />
                <span class="position-absolute top-50 end-0 translate-middle-y pe-3 text-muted">🔍</span>
              </div>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="productsLoading" class="row g-4">
            <div class="col-md-4 col-lg-3" v-for="i in 4" :key="i">
              <div class="bf-card p-4 text-center">
                <span class="bf-spinner"></span>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else-if="products.length === 0" class="text-center py-5 text-muted">
            <span style="font-size: 2.5rem;">🔌</span>
            <h5 class="mt-3">No products match your search.</h5>
            <p class="small text-secondary">Browse general products or check back later.</p>
          </div>

          <!-- Products Grid -->
          <div v-else class="row g-4">
            <div class="col-md-6 col-lg-4 col-xl-3" v-for="product in products" :key="product.productId">
              <ProductBox :product="product" />
            </div>
          </div>
        </div>

        <!-- Reviews & Policies Tab -->
        <div v-if="activeTab === 'reviews'" class="row g-4 bf-fade-in">
          <!-- Policies column -->
          <div class="col-lg-5">
            <div class="bf-card p-4">
              <h5 class="font-weight-bold text-dark mb-3">Store Policies</h5>
              <div class="policies-box p-3 bg-light rounded text-secondary small" style="white-space: pre-line; line-height: 1.6;">
                {{ store.storePolicies || 'Default marketplace return and shipping policies apply.' }}
              </div>
            </div>
          </div>

          <!-- Reviews column -->
          <div class="col-lg-7">
            <div class="bf-card p-4">
              <h5 class="font-weight-bold text-dark mb-4">Store Reviews</h5>

              <!-- Write Review Section -->
              <div v-if="canReview" class="p-3 bg-light rounded mb-4">
                <h6 class="font-weight-bold mb-3">Write a Store Review</h6>
                <form @submit.prevent="submitReview">
                  <div class="mb-3">
                    <label class="form-label small text-muted font-weight-bold d-block">Store Rating</label>
                    <div class="rating-stars-input d-flex gap-1 fs-4 text-warning">
                      <span
                        v-for="star in 5"
                        :key="star"
                        class="cursor-pointer"
                        @click="newReviewRating = star"
                      >
                        {{ star <= newReviewRating ? '★' : '☆' }}
                      </span>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="form-label small text-muted font-weight-bold">Comment</label>
                    <textarea
                      class="bf-input"
                      rows="3"
                      v-model="newReviewComment"
                      placeholder="Share your experience buying from this seller..."
                      required
                    ></textarea>
                  </div>
                  <button type="submit" class="bf-btn bf-btn-primary bf-btn-sm" :disabled="reviewSubmitting">
                    <span v-if="reviewSubmitting" class="bf-spinner me-1"></span>
                    Submit Review
                  </button>
                </form>
              </div>

              <!-- Reviews List -->
              <div v-if="reviewsLoading" class="py-4 text-center">
                <span class="bf-spinner"></span>
              </div>
              <div v-else-if="reviews.length === 0" class="text-center py-4 text-muted small">
                No reviews yet. Be the first to purchase and review this store!
              </div>
              <div v-else class="d-flex flex-column gap-3">
                <div v-for="rev in reviews" :key="rev.reviewId" class="review-item border-bottom pb-3">
                  <div class="d-flex justify-content-between align-items-center mb-1">
                    <strong class="text-dark small">@{{ rev.username }}</strong>
                    <small class="text-muted">{{ formatDate(rev.createdDate) }}</small>
                  </div>
                  <div class="text-warning small mb-2">
                    <span v-for="s in 5" :key="s">{{ s <= rev.rating ? '★' : '☆' }}</span>
                  </div>
                  <p class="small text-secondary mb-0">{{ rev.comment }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage } from '../../utils/api';
import { getToken, isLoggedIn } from '../../utils/auth';
import ProductBox from '../../components/Product/ProductBox.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'StorePage',
  components: { ProductBox },
  data() {
    return {
      storeId: this.$route.params.id,
      store: {},
      isFollowing: false,
      activeTab: 'products',
      searchQuery: '',
      searchTimeout: null,
      
      products: [],
      productsLoading: true,
      
      reviews: [],
      reviewsLoading: true,

      // Submit Review Form
      newReviewRating: 5,
      newReviewComment: '',
      reviewSubmitting: false,
      followLoading: false,

      // Asset Placeholders
      defaultBanner: 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=1920&auto=format&fit=crop',
      defaultLogo: 'https://placehold.co/100x100?text=Store'
    };
  },
  computed: {
    canReview() {
      return isLoggedIn();
    }
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return '';
      return new Date(dateStr).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },
    formatYear(dateStr) {
      if (!dateStr) return '';
      return new Date(dateStr).getFullYear();
    },
    handleBannerError(e) {
      e.target.src = this.defaultBanner;
    },
    handleLogoError(e) {
      e.target.src = this.defaultLogo;
    },
    async fetchStoreDetails() {
      try {
        const response = await api.get(`/store/${this.storeId}`);
        this.store = response.data.data;
      } catch (err) {
        showToast({
          message: 'Failed to fetch store details: ' + extractErrorMessage(err),
          type: 'error'
        });
      }
    },
    async fetchStoreProducts() {
      this.productsLoading = true;
      try {
        const response = await api.get(`/store/${this.storeId}/products`, {
          params: { search: this.searchQuery }
        });
        this.products = response.data.data || [];
      } catch (err) {
        console.warn('Failed to load store products: ', err);
      } finally {
        this.productsLoading = false;
      }
    },
    async fetchStoreReviews() {
      this.reviewsLoading = true;
      try {
        const response = await api.get(`/store/${this.storeId}/reviews`);
        this.reviews = response.data.data || [];
      } catch (err) {
        console.warn('Failed to load store reviews: ', err);
      } finally {
        this.reviewsLoading = false;
      }
    },
    async checkFollowing() {
      if (!isLoggedIn()) return;
      try {
        const token = getToken();
        const response = await api.get(`/store/${this.storeId}/is-following?token=${token}`);
        this.isFollowing = response.data.data;
      } catch (err) {
        console.warn('Could not check following status: ', err);
      }
    },
    async toggleFollow() {
      if (!isLoggedIn()) {
        showToast({ message: 'Please sign in to follow stores.', type: 'warning' });
        this.$router.push({ name: 'SignIn', query: { redirect: this.$route.fullPath } });
        return;
      }

      this.followLoading = true;
      try {
        const token = getToken();
        const response = await api.post(`/store/${this.storeId}/follow?token=${token}`);
        this.isFollowing = response.data.data;
        
        // Refresh stats
        await this.fetchStoreDetails();
        
        showToast({
          message: this.isFollowing ? 'You are now following this store!' : 'Removed store from your followings.',
          type: 'success'
        });
      } catch (err) {
        showToast({
          message: 'Could not complete follow operation: ' + extractErrorMessage(err),
          type: 'error'
        });
      } finally {
        this.followLoading = false;
      }
    },
    async submitReview() {
      const token = getToken();
      if (!token) return;

      this.reviewSubmitting = true;
      try {
        await api.post(`/store/${this.storeId}/review?token=${token}`, {
          rating: this.newReviewRating,
          comment: this.newReviewComment
        });

        showToast({ message: 'Thank you for your feedback!', type: 'success' });
        this.newReviewComment = '';
        this.newReviewRating = 5;

        // Refresh reviews and ratings
        await Promise.all([
          this.fetchStoreDetails(),
          this.fetchStoreReviews()
        ]);
      } catch (err) {
        showToast({
          message: 'Failed to submit review: ' + extractErrorMessage(err),
          type: 'error'
        });
      } finally {
        this.reviewSubmitting = false;
      }
    },
    debounceSearch() {
      clearTimeout(this.searchTimeout);
      this.searchTimeout = setTimeout(() => {
        this.fetchStoreProducts();
      }, 400);
    }
  },
  mounted() {
    this.fetchStoreDetails();
    this.fetchStoreProducts();
    this.fetchStoreReviews();
    this.checkFollowing();
  }
};
</script>

<style scoped>
.store-banner-wrapper {
  height: 280px;
  width: 100%;
  overflow: hidden;
}

.store-banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(15, 23, 42, 0.2) 0%, rgba(15, 23, 42, 0.7) 100%);
}

.store-content-container {
  margin-top: -60px;
  position: relative;
  z-index: 10;
}

.store-profile-card {
  border-radius: var(--bf-radius-xl);
}

.store-logo-wrapper {
  width: 90px;
  height: 90px;
  background: var(--bf-bg-card);
  border: 3px solid rgba(255, 255, 255, 0.15);
  box-shadow: var(--bf-shadow-lg);
  border-radius: var(--bf-radius-lg);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.store-logo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.verification-badge {
  font-size: 1.15rem;
}

.tab-btn {
  background: none;
  border: none;
  padding: 10px 20px;
  font-weight: 700;
  font-size: var(--bf-font-size-sm);
  color: var(--bf-text-secondary);
  position: relative;
  transition: all var(--bf-transition-fast);
}

.tab-btn:hover {
  color: var(--bf-text-primary);
}

.tab-btn.active {
  color: var(--bf-primary);
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--bf-primary);
  border-radius: var(--bf-radius-full);
}

.policies-box {
  border: 1px dashed var(--bf-border);
}

.review-item {
  border-color: var(--bf-border-light) !important;
}
.review-item:last-child {
  border-bottom: 0 !important;
}

.rating-stars-input span {
  transition: transform var(--bf-transition-fast);
}
.rating-stars-input span:hover {
  transform: scale(1.15);
}
</style>
