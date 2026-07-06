<template>
  <div class="bf-page bf-fade-in">
    <!-- ═══ Hero Section with Dynamic Slideshow ═══ -->
    <div class="container my-4">
      <div class="row g-4 mb-5">
        <!-- Main Hero Slideshow -->
        <div class="col-lg-8">
          <div class="bf-hero-main" :style="heroStyle">
            <div v-if="slides && slides.length > 0" class="bf-hero-content">
              <div>
                <span class="bf-hero-badge">{{ slides[activeSlide].badge }}</span>
                <h1 class="bf-hero-title">{{ slides[activeSlide].title }}</h1>
                <p class="bf-hero-subtitle">{{ slides[activeSlide].subtitle }}</p>
              </div>
              <button class="bf-btn bf-btn-accent bf-btn-lg" @click="scrollToProducts">
                Shop Now
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708"/></svg>
              </button>
            </div>

            <!-- Slide Dots -->
            <div class="bf-hero-dots">
              <span
                v-for="(slide, index) in slides"
                :key="index"
                class="bf-hero-dot"
                :class="{ active: activeSlide === index }"
                @click="activeSlide = index"
              ></span>
            </div>

            <!-- Pattern Overlay -->
            <div class="bf-hero-pattern"></div>
          </div>
        </div>

        <!-- Side Promo Cards -->
        <div class="col-lg-4 d-flex flex-column gap-3">
          <div class="bf-promo-card bf-promo-dark" @click="selectCategoryByName('Desktop Systems')">
            <span class="bf-promo-badge bf-badge bf-badge-primary">Featured Build</span>
            <h5 class="bf-promo-title">Prebuilt Gaming PCs</h5>
            <p class="bf-promo-desc">High-fps liquid-cooled rigs benchmarked and ready to play.</p>
            <span class="bf-promo-link">Explore Systems →</span>
          </div>

          <div class="bf-promo-card bf-promo-blue" @click="selectCategoryByName('Graphics Cards (GPUs)')">
            <span class="bf-promo-badge bf-badge bf-badge-warning">Hardware Drop</span>
            <h5 class="bf-promo-title">RTX 40-Series In Stock</h5>
            <p class="bf-promo-desc">Unmatched ray-tracing and DLSS 3 support.</p>
            <span class="bf-promo-link">Shop GPUs →</span>
          </div>
        </div>
      </div>

      <!-- ═══ Shop By Category ═══ -->
      <div class="bf-section bf-fade-in bf-fade-in-delay-1">
        <div class="bf-section-header">
          <h4 class="bf-section-title">Shop by Category</h4>
          <p class="bf-section-desc">Browse our curated collection of PC components and peripherals</p>
        </div>
        <div class="bf-category-scroll">
          <!-- All Categories -->
          <div class="bf-category-chip" :class="{ active: selectedCategoryId === 'all' }" @click="selectCategory('all')">
            <div class="bf-category-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" viewBox="0 0 16 16"><path d="M1 2.5A1.5 1.5 0 0 1 2.5 1h3A1.5 1.5 0 0 1 7 2.5v3A1.5 1.5 0 0 1 5.5 7h-3A1.5 1.5 0 0 1 1 5.5zm8 0A1.5 1.5 0 0 1 10.5 1h3A1.5 1.5 0 0 1 15 2.5v3A1.5 1.5 0 0 1 13.5 7h-3A1.5 1.5 0 0 1 9 5.5zm-8 8A1.5 1.5 0 0 1 2.5 9h3A1.5 1.5 0 0 1 7 10.5v3A1.5 1.5 0 0 1 5.5 15h-3A1.5 1.5 0 0 1 1 13.5zm8 0A1.5 1.5 0 0 1 10.5 9h3a1.5 1.5 0 0 1 1.5 1.5v3a1.5 1.5 0 0 1-1.5 1.5h-3A1.5 1.5 0 0 1 9 13.5z"/></svg>
            </div>
            <span>All Gears</span>
          </div>

          <!-- Dynamic Categories -->
          <div
            v-for="cat in categories"
            :key="cat.categoryId"
            class="bf-category-chip"
            :class="{ active: selectedCategoryId === cat.categoryId }"
            @click="selectCategory(cat.categoryId)"
          >
            <div class="bf-category-img">
              <img :src="cat.imageUrl" :alt="cat.categoryName" />
            </div>
            <span>{{ cat.categoryName }}</span>
          </div>
        </div>
      </div>

      <!-- ═══ Feature Highlights ═══ -->
      <div class="row g-3 mb-5 bf-fade-in bf-fade-in-delay-2">
        <div class="col-md-4">
          <div class="bf-feature-card">
            <div class="bf-feature-icon" style="background: var(--bf-primary-light); color: var(--bf-primary);">
              <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 16 16"><path d="M0 3.5A1.5 1.5 0 0 1 1.5 2h9A1.5 1.5 0 0 1 12 3.5V5h1.02a1.5 1.5 0 0 1 1.17.563l1.481 1.85a1.5 1.5 0 0 1 .329.938V10.5a1.5 1.5 0 0 1-1.5 1.5H14a2 2 0 1 1-4 0H5a2 2 0 1 1-3.998-.085A1.5 1.5 0 0 1 0 10.5z"/></svg>
            </div>
            <div>
              <h6 class="bf-feature-title">Free Shipping</h6>
              <p class="bf-feature-desc">On all orders over LKR 10,000</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="bf-feature-card">
            <div class="bf-feature-icon" style="background: var(--bf-success-light); color: var(--bf-success);">
              <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 16 16"><path d="M5.338 1.59a6.1 6.1 0 0 0-2.837.856.48.48 0 0 0-.228.389v4.287c0 3.29 1.825 6.4 4.732 7.69a.48.48 0 0 0 .39 0c2.907-1.29 4.732-4.4 4.732-7.69V2.835a.48.48 0 0 0-.228-.389 6.1 6.1 0 0 0-2.837-.855 4.8 4.8 0 0 0-3.722 0"/></svg>
            </div>
            <div>
              <h6 class="bf-feature-title">Secure Payments</h6>
              <p class="bf-feature-desc">100% SSL protected payments</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="bf-feature-card">
            <div class="bf-feature-icon" style="background: var(--bf-warning-light); color: var(--bf-warning);">
              <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" viewBox="0 0 16 16"><path d="M8 1a5 5 0 0 0-5 5v1h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V6a6 6 0 1 1 12 0v5a1 1 0 0 1-1 1h-1a1 1 0 0 1-1-1V8a1 1 0 0 1 1-1h1V6a5 5 0 0 0-5-5"/></svg>
            </div>
            <div>
              <h6 class="bf-feature-title">24/7 Support</h6>
              <p class="bf-feature-desc">Dedicated customer helpline</p>
            </div>
          </div>
        </div>
      </div>

      <!-- ═══ Search & Filter Toolbar ═══ -->
      <div id="products-section" class="bf-toolbar bf-fade-in bf-fade-in-delay-3">
        <div class="bf-toolbar-search">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/></svg>
          <input
            type="text"
            class="bf-input"
            placeholder="Search components or accessories..."
            v-model="searchQuery"
          />
        </div>
        <div class="bf-toolbar-sort">
          <span class="bf-toolbar-label">Sort by:</span>
          <select class="bf-input bf-toolbar-select" v-model="sortOrder">
            <option value="default">Relevance</option>
            <option value="name-asc">Name (A-Z)</option>
            <option value="price-asc">Price (Low → High)</option>
            <option value="price-desc">Price (High → Low)</option>
          </select>
        </div>
      </div>

      <!-- ═══ Loading Skeletons ═══ -->
      <div v-if="loading" class="row g-4 mt-2">
        <div v-for="n in 8" :key="n" class="col-xl-3 col-md-6">
          <LoadingSkeleton type="card" />
        </div>
      </div>

      <!-- ═══ Error State ═══ -->
      <div v-else-if="error" class="bf-empty-state">
        <div class="bf-empty-icon" style="color: var(--bf-danger);">⚠</div>
        <h5>Something went wrong</h5>
        <p>{{ error }}</p>
        <button class="bf-btn bf-btn-primary" @click="fetchProducts">Try Again</button>
      </div>

      <!-- ═══ Empty State ═══ -->
      <div v-else-if="processedProducts.length === 0" class="bf-empty-state">
        <div class="bf-empty-icon">🔍</div>
        <h5>No products match your criteria</h5>
        <p>Try adjusting your search filters or browse another category.</p>
      </div>

      <!-- ═══ Products Grid ═══ -->
      <div v-else class="row g-4 mt-2">
        <div
          v-for="(product, index) in processedProducts"
          :key="product.productId"
          class="col-xl-3 col-md-6 d-flex bf-fade-in"
          :class="'bf-fade-in-delay-' + Math.min(index + 1, 6)"
        >
          <ProductBox :product="product" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage } from '../../utils/api';
import ProductBox from '../../components/Product/ProductBox.vue';
import LoadingSkeleton from '../../components/Common/LoadingSkeleton.vue';
import { getCurrentUser } from '../../utils/auth';

export default {
  name: 'ProductView',
  components: { ProductBox, LoadingSkeleton },
  data() {
    return {
      products: [],
      categories: [],
      selectedCategoryId: 'all',
      searchQuery: '',
      sortOrder: 'default',
      loading: true,
      error: null,

      // Dynamic Slider Configuration
      activeSlide: 0,
      slidesInterval: null,
      slides: [
        {
          badge: 'Next-Gen Computing',
          title: 'Unleash Peak Performance',
          subtitle: 'Build your dream custom rig with newly restocked Ryzen 7 7800X3D processors and DDR5 modules.'
        },
        {
          badge: 'Exclusive Tech Sale',
          title: 'Level Up Your Setup',
          subtitle: 'Save up to 40% on mechanical gaming keyboards, high-precision optical mice, and headsets.'
        },
        {
          badge: 'Apex Graphics Power',
          title: 'RTX 4090 Flagship GPUs',
          subtitle: 'Experience ray-tracing and AI-driven frame rendering with Nvidia RTX and Radeon graphics.'
        }
      ]
    };
  },
  computed: {
    isAdmin() {
      const user = getCurrentUser();
      return user && user.role === 'ADMIN';
    },
    heroStyle() {
      return {
        background: `var(--bf-gradient-hero), url('https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=1200&auto=format&fit=crop') no-repeat center/cover`
      };
    },
    processedProducts() {
      let result = [...this.products];

      // 1. Filter by Search Query
      const query = this.searchQuery.trim().toLowerCase();
      if (query) {
        result = result.filter(p =>
          p.productName.toLowerCase().includes(query) ||
          p.description.toLowerCase().includes(query)
        );
      }

      // 2. Sort results
      if (this.sortOrder === 'name-asc') {
        result.sort((a, b) => a.productName.localeCompare(b.productName));
      } else if (this.sortOrder === 'price-asc') {
        result.sort((a, b) => Number(a.price) - Number(b.price));
      } else if (this.sortOrder === 'price-desc') {
        result.sort((a, b) => Number(b.price) - Number(a.price));
      }

      return result;
    }
  },
  methods: {
    scrollToProducts() {
      const el = document.getElementById('products-section');
      if (el) {
        el.scrollIntoView({ behavior: 'smooth' });
      }
    },
    selectCategory(id) {
      this.selectedCategoryId = id;
      this.fetchProducts();
      this.scrollToProducts();
    },
    selectCategoryByName(name) {
      const category = this.categories.find(c => c.categoryName.toLowerCase().includes(name.toLowerCase()));
      if (category) {
        this.selectCategory(category.categoryId);
      } else {
        this.scrollToProducts();
      }
    },
    async fetchCategories() {
      try {
        const response = await api.get('/category');
        this.categories = response.data.data;
      } catch (err) {
        console.error('Failed to fetch categories:', extractErrorMessage(err));
      }
    },
    async fetchProducts() {
      this.loading = true;
      this.error = null;
      try {
        let response;
        if (this.selectedCategoryId === 'all') {
          response = await api.get('/product');
        } else {
          response = await api.get(`/category/${this.selectedCategoryId}/product`);
        }
        this.products = response.data.data || [];
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    }
  },
  mounted() {
    this.fetchCategories();
    this.fetchProducts();

    // Cycle promotional slides
    this.slidesInterval = setInterval(() => {
      this.activeSlide = (this.activeSlide + 1) % this.slides.length;
    }, 5000);
  },
  beforeUnmount() {
    if (this.slidesInterval) {
      clearInterval(this.slidesInterval);
    }
  }
};
</script>

<style scoped>
/* ─── Hero Section ─────────────────────────────────────────── */
.bf-hero-main {
  border-radius: var(--bf-radius-xl);
  padding: 48px 40px;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  color: white;
}

.bf-hero-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  gap: 24px;
}

.bf-hero-badge {
  display: inline-block;
  background: rgba(245, 158, 11, 0.9);
  color: #0f172a;
  padding: 6px 16px;
  border-radius: var(--bf-radius-full);
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 16px;
}

.bf-hero-title {
  font-size: clamp(1.75rem, 4vw, 3rem);
  font-weight: 900;
  line-height: 1.1;
  margin: 0 0 12px 0;
  color: #ffffff;
  letter-spacing: -0.02em;
}

.bf-hero-subtitle {
  font-size: var(--bf-font-size-lg);
  opacity: 0.7;
  max-width: 500px;
  line-height: 1.5;
  margin: 0;
}

.bf-hero-dots {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 2;
}

.bf-hero-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 0.3s ease;
}

.bf-hero-dot.active {
  width: 28px;
  border-radius: 5px;
  background: var(--bf-accent);
}

.bf-hero-pattern {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px);
  background-size: 24px 24px;
  z-index: 1;
}

/* ─── Promo Cards ──────────────────────────────────────────── */
.bf-promo-card {
  flex: 1;
  padding: 28px 24px;
  border-radius: var(--bf-radius-xl);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  cursor: pointer;
  transition: all var(--bf-transition-base);
  position: relative;
  overflow: hidden;
}

.bf-promo-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--bf-shadow-xl);
}

.bf-promo-dark {
  background: linear-gradient(135deg, rgba(9, 9, 11, 0.9) 0%, rgba(39, 39, 42, 0.95) 100%),
    url('https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=600&auto=format&fit=crop') no-repeat center/cover;
}

.bf-promo-blue {
  background: linear-gradient(135deg, rgba(12, 74, 110, 0.9) 0%, rgba(3, 105, 161, 0.95) 100%),
    url('https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=600&auto=format&fit=crop') no-repeat center/cover;
}

.bf-promo-badge { margin-bottom: 10px; align-self: flex-start; }
.bf-promo-title { font-weight: 800; margin: 0 0 6px 0; font-size: var(--bf-font-size-lg); }
.bf-promo-desc { font-size: var(--bf-font-size-xs); opacity: 0.6; margin: 0 0 12px 0; }
.bf-promo-link {
  font-size: var(--bf-font-size-sm);
  font-weight: 600;
  color: rgba(255,255,255,0.8);
  transition: color 0.2s;
}
.bf-promo-card:hover .bf-promo-link { color: #fff; }

/* ─── Section Styling ──────────────────────────────────────── */
.bf-section {
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-xl);
  padding: 28px;
  margin-bottom: 32px;
}

.bf-section-header { margin-bottom: 20px; }
.bf-section-title { font-weight: 800; margin: 0 0 4px 0; color: var(--bf-text-primary); }
.bf-section-desc { margin: 0; font-size: var(--bf-font-size-sm); color: var(--bf-text-muted); }

/* ─── Category Chips ───────────────────────────────────────── */
.bf-category-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.bf-category-chip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
  transition: all var(--bf-transition-base);
}

.bf-category-chip span {
  font-size: var(--bf-font-size-xs);
  font-weight: 600;
  color: var(--bf-text-secondary);
  max-width: 80px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bf-category-chip.active span { color: var(--bf-primary); }

.bf-category-icon,
.bf-category-img {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  border: 2.5px solid var(--bf-border);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bf-bg-tertiary);
  transition: all var(--bf-transition-base);
}

.bf-category-chip.active .bf-category-icon,
.bf-category-chip.active .bf-category-img {
  border-color: var(--bf-primary);
  box-shadow: 0 0 0 3px var(--bf-primary-light);
}

.bf-category-chip:hover .bf-category-icon,
.bf-category-chip:hover .bf-category-img {
  transform: scale(1.08);
  box-shadow: var(--bf-shadow-md);
}

.bf-category-icon { color: var(--bf-primary); }
.bf-category-img img { width: 100%; height: 100%; object-fit: cover; }

/* ─── Feature Cards ────────────────────────────────────────── */
.bf-feature-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  transition: all var(--bf-transition-base);
}

.bf-feature-card:hover {
  box-shadow: var(--bf-shadow-md);
  transform: translateY(-2px);
}

.bf-feature-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--bf-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bf-feature-title {
  font-weight: 700;
  margin: 0 0 2px 0;
  font-size: var(--bf-font-size-sm);
  color: var(--bf-text-primary);
}

.bf-feature-desc {
  margin: 0;
  font-size: var(--bf-font-size-xs);
  color: var(--bf-text-muted);
}

/* ─── Toolbar ──────────────────────────────────────────────── */
.bf-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 20px;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.bf-toolbar-search {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  max-width: 400px;
  color: var(--bf-text-muted);
}

.bf-toolbar-search .bf-input {
  border: none;
  background: var(--bf-bg-tertiary);
  padding: 10px 14px;
}

.bf-toolbar-sort {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bf-toolbar-label {
  font-size: var(--bf-font-size-xs);
  color: var(--bf-text-muted);
  white-space: nowrap;
}

.bf-toolbar-select {
  max-width: 180px;
  padding: 8px 12px !important;
  background: var(--bf-bg-tertiary) !important;
  border: 1px solid var(--bf-border) !important;
}

/* ─── Empty State ──────────────────────────────────────────── */
.bf-empty-state {
  text-align: center;
  padding: 64px 24px;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-xl);
  margin-top: 16px;
}

.bf-empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.bf-empty-state h5 {
  font-weight: 700;
  color: var(--bf-text-primary);
  margin-bottom: 8px;
}

.bf-empty-state p {
  color: var(--bf-text-muted);
  font-size: var(--bf-font-size-sm);
  margin-bottom: 20px;
}

/* ─── Responsive ───────────────────────────────────────────── */
@media (max-width: 768px) {
  .bf-hero-main {
    padding: 32px 24px;
    min-height: 300px;
  }

  .bf-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .bf-toolbar-search { max-width: none; }
  .bf-toolbar-sort { justify-content: space-between; }
}
</style>
