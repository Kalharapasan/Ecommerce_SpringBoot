<template>
  <div class="bf-page bf-fade-in position-relative">
    
    <!-- ═══ LAYOUT A: HOMEPAGE (/) ═══ -->
    <div v-if="isHomeRoute" class="bf-home-layout">
      <!-- Particle Background Canvas -->
      <canvas ref="particleCanvas" class="bf-particle-canvas"></canvas>

      <!-- Hero Header Section -->
      <div class="container py-5">
        <div class="row g-4 align-items-center mb-5 hero-row">
          <div class="col-lg-7 text-start position-relative" style="z-index: 2;">
            <div v-if="slides && slides.length > 0" class="bf-hero-content-slider">
              <transition name="page-fade" mode="out-in">
                <div :key="activeSlide" class="bf-slide-content">
                  <span class="bf-hero-badge-tag bf-badge bf-badge-primary mb-3">
                    <span class="pulse-dot"></span>
                    {{ slides[activeSlide].badge }}
                  </span>
                  <h1 class="bf-hero-title-main mb-3">
                    {{ slides[activeSlide].title }}
                  </h1>
                  <p class="bf-hero-subtitle-main text-muted mb-4 fs-5">
                    {{ slides[activeSlide].subtitle }}
                  </p>
                </div>
              </transition>
              <div class="d-flex gap-3">
                <router-link to="/product" class="bf-btn bf-btn-primary bf-btn-lg px-4 shadow-glow">
                  Explore Components
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708"/></svg>
                </router-link>
                <button class="bf-btn bf-btn-outline bf-btn-lg px-4" @click="scrollToFeatured">
                  Quick Specs
                </button>
              </div>
            </div>
            
            <!-- Slides navigation dots -->
            <div class="bf-slides-dots mt-4">
              <span
                v-for="(slide, idx) in slides"
                :key="idx"
                class="bf-slide-dot"
                :class="{ active: activeSlide === idx }"
                @click="activeSlide = idx"
              ></span>
            </div>
          </div>

          <!-- Banner Tech Graphic -->
          <div class="col-lg-5 position-relative text-center">
            <div class="hero-graphic-glow"></div>
            <img 
              src="https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=800&auto=format&fit=crop" 
              class="img-fluid hero-tech-img rounded-4 shadow-xl border-light" 
              alt="Premium PC Rig"
            />
          </div>
        </div>

        <!-- Animated Statistics Counter Row -->
        <div class="row g-4 mb-5 text-start">
          <div class="col-md-3" v-for="(stat, idx) in homeStats" :key="idx">
            <div class="bf-card bf-glass stat-card p-4">
              <div class="d-flex align-items-center gap-3">
                <span class="stat-icon-badge fs-3">{{ stat.icon }}</span>
                <div>
                  <h4 class="mb-0 font-weight-bold">{{ stat.value }}</h4>
                  <small class="text-muted text-uppercase tracking-wider font-weight-bold">{{ stat.label }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Brand Showcase -->
        <div class="bf-section-bg p-5 mb-5 text-center">
          <span class="bf-badge bf-badge-primary mb-3">OFFICIAL VENDORS & BRANDS</span>
          <h3 class="font-weight-bold mb-4">Ultimate Hardware Ecosystem</h3>
          <div class="row row-cols-2 row-cols-md-5 g-4 justify-content-center align-items-center brand-grid">
            <div class="col" v-for="brand in brandList" :key="brand">
              <div class="brand-item-card p-3 rounded border text-muted">
                {{ brand }}
              </div>
            </div>
          </div>
        </div>

        <!-- Featured Collections Header -->
        <div id="featured-section" class="text-start mb-4">
          <h4 class="font-weight-bold mb-2">Featured Rig Gear</h4>
          <p class="text-muted">Uncompromising engineering built to exceed performance benchmarks.</p>
        </div>

        <!-- Products Grid for Home Page -->
        <div v-if="loading" class="row g-4">
          <div v-for="n in 4" :key="n" class="col-xl-3 col-md-6">
            <LoadingSkeleton type="card" />
          </div>
        </div>
        <div v-else class="row g-4 mb-5">
          <div v-for="product in homeFeaturedProducts" :key="product.productId" class="col-xl-3 col-md-6 d-flex">
            <ProductBox :product="product" />
          </div>
        </div>

        <!-- Customer Testimonials -->
        <div class="row g-4 mb-5 text-start">
          <div class="col-md-4" v-for="(t, index) in testimonials" :key="index">
            <div class="bf-card bf-glass p-4 h-100 d-flex flex-column justify-content-between testimonial-card">
              <p class="text-secondary small italic-text">"{{ t.quote }}"</p>
              <div class="d-flex align-items-center gap-3 mt-3 pt-3 border-top">
                <div class="testimonial-avatar">{{ t.author.charAt(0) }}</div>
                 <div>
                  <h6 class="mb-0 font-weight-bold">{{ t.author }}</h6>
                  <small class="text-muted">{{ t.role }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Newsletter section -->
        <div class="bf-newsletter-banner p-5 text-center text-white rounded-4 border position-relative overflow-hidden mb-5">
          <div class="newsletter-glow"></div>
          <h4 class="font-weight-bold mb-2">Join the Forge Elite Network</h4>
          <p class="text-secondary small mb-4">Get hot restock alerts, flash sales releases, and custom PC hardware guides first.</p>
          <div class="d-flex gap-2 max-width-sm mx-auto justify-content-center flex-wrap">
            <input type="email" class="bf-input py-2 px-3" placeholder="Enter your email address" style="max-width: 320px;" />
            <button class="bf-btn bf-btn-primary" @click="handleSubscribe">Subscribe</button>
          </div>
        </div>

      </div>
    </div>

    <!-- ═══ LAYOUT B: CATALOG / CATEGORY PAGE (/product) ═══ -->
    <div v-else class="bf-catalog-layout py-4">
      <div class="container">
        
        <!-- Toolbar Header -->
        <div class="row mb-4 align-items-center">
          <div class="col-md-6 text-start">
            <span class="bf-badge bf-badge-primary mb-2">Hardware Inventory</span>
            <h2 class="text-white font-weight-bold mb-1">Advanced Catalog Explorer</h2>
            <p class="text-muted small">Filters components dynamically by price, manufacturer, and stock availability.</p>
          </div>
          <!-- Grid/List layout toggle -->
          <div class="col-md-6 d-flex justify-content-md-end gap-2 align-items-center mt-3 mt-md-0">
            <span class="text-muted small">Layout:</span>
            <div class="btn-group border rounded p-1 bg-dark-toggle">
              <button 
                class="bf-btn bf-btn-ghost bf-btn-sm px-3" 
                :class="{ 'bf-btn-primary': viewMode === 'grid' }"
                @click="viewMode = 'grid'"
              >
                Grid
              </button>
              <button 
                class="bf-btn bf-btn-ghost bf-btn-sm px-3" 
                :class="{ 'bf-btn-primary': viewMode === 'list' }"
                @click="viewMode = 'list'"
              >
                List
              </button>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <!-- Left Sidebar Filters -->
          <div class="col-lg-3 text-start">
            <div class="bf-card bf-glass p-4 sticky-sidebar">
              <h5 class="text-white font-weight-bold mb-4 border-bottom pb-2">Filter Hardware</h5>
              
              <!-- Search Filter Input -->
              <div class="mb-4">
                <label class="form-label text-muted small font-weight-bold uppercase">Search</label>
                <div class="position-relative">
                  <input 
                    type="text" 
                    class="bf-input py-2" 
                    v-model="searchQuery" 
                    placeholder="Enter keywords..."
                  />
                  <span class="position-absolute top-50 end-0 translate-middle-y pe-3 text-muted">🔍</span>
                </div>
              </div>

              <!-- Categories Select Panel -->
              <div class="mb-4">
                <label class="form-label text-muted small font-weight-bold uppercase">Component Type</label>
                <div class="d-flex flex-column gap-2 max-height-chips">
                  <div 
                    class="filter-chip"
                    :class="{ active: selectedCategoryId === 'all' }"
                    @click="selectedCategoryId = 'all'"
                  >
                    All Categories
                  </div>
                  <div 
                    v-for="cat in categories" 
                    :key="cat.categoryId"
                    class="filter-chip text-truncate"
                    :class="{ active: selectedCategoryId === cat.categoryId }"
                    @click="selectedCategoryId = cat.categoryId"
                    :title="cat.categoryName"
                  >
                    {{ cat.categoryName }}
                  </div>
                </div>
              </div>

              <!-- Price Limit Filter Slider -->
              <div class="mb-4">
                <label class="form-label text-muted small font-weight-bold d-flex justify-content-between uppercase">
                  <span>Max Price Limit</span>
                  <span class="text-primary font-weight-bold">LKR {{ priceRange.toLocaleString() }}</span>
                </label>
                <input 
                  type="range" 
                  class="form-range" 
                  min="0" 
                  max="1000000" 
                  step="5000"
                  v-model.number="priceRange"
                />
                <div class="d-flex justify-content-between text-muted small mt-1">
                  <span>LKR 0</span>
                  <span>LKR 1,000,000</span>
                </div>
              </div>

              <!-- Brand Filter Options -->
              <div class="mb-4">
                <label class="form-label text-muted small font-weight-bold uppercase">Manufacturers / Brand</label>
                <select class="bf-input py-2" v-model="selectedBrand">
                  <option value="all">All Manufacturers</option>
                  <option v-for="b in brandList" :key="b" :value="b">{{ b }}</option>
                </select>
              </div>

              <!-- Availability Checkbox -->
              <div class="mb-4">
                <div class="form-check form-switch">
                  <input 
                    class="form-check-input" 
                    type="checkbox" 
                    id="stockCheck" 
                    v-model="inStockOnly"
                  />
                  <label class="form-check-label text-white small" for="stockCheck">
                    In Stock Only
                  </label>
                </div>
              </div>

              <!-- Clear Filters Button -->
              <button class="bf-btn bf-btn-outline w-100" @click="resetFilters">
                Reset Filters
              </button>
            </div>
          </div>

          <!-- Right Products Catalog Grid -->
          <div class="col-lg-9 text-start">
            <!-- Grid sorting options / Results summary -->
            <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
              <span class="text-muted small">
                Showing <strong>{{ paginatedProducts.length }}</strong> of <strong>{{ filteredProducts.length }}</strong> modules
              </span>
              <div class="d-flex align-items-center gap-2">
                <span class="text-muted small">Sort:</span>
                <select class="bf-input py-1 px-2 text-sm" v-model="sortOrder">
                  <option value="default">Relevance</option>
                  <option value="name-asc">Name (A-Z)</option>
                  <option value="price-asc">Price (Low → High)</option>
                  <option value="price-desc">Price (High → Low)</option>
                </select>
              </div>
            </div>

            <!-- Loading skeletons -->
            <div v-if="loading" class="row g-4">
              <div v-for="n in 6" :key="n" class="col-xl-4 col-md-6">
                <LoadingSkeleton type="card" />
              </div>
            </div>

            <!-- Error State -->
            <div v-else-if="error" class="bf-empty-state">
              <div class="bf-empty-icon text-danger">⚠</div>
              <h5 class="text-white font-weight-bold">Catalogue Sync Interrupted</h5>
              <p>{{ error }}</p>
              <button class="bf-btn bf-btn-primary" @click="fetchProducts">Retry Load</button>
            </div>

            <!-- Empty filter state -->
            <div v-else-if="filteredProducts.length === 0" class="bf-empty-state text-center py-5">
              <div class="bf-empty-icon text-muted">🔍</div>
              <h5 class="text-white font-weight-bold">No Rig Components Found</h5>
              <p>Try modifying your sidebar filter categories or raising the price slider limits.</p>
              <button class="bf-btn bf-btn-primary" @click="resetFilters">Clear All Filters</button>
            </div>

            <!-- Products catalog list -->
            <div v-else>
              <div v-if="viewMode === 'grid'" class="row g-4">
                <div 
                  v-for="(product, idx) in paginatedProducts" 
                  :key="product.productId" 
                  class="col-xl-4 col-md-6 d-flex bf-fade-in"
                  :style="{'animation-delay': (idx * 0.05) + 's'}"
                >
                  <ProductBox :product="product" />
                </div>
              </div>
              
              <!-- List View Option -->
              <div v-else class="d-flex flex-column gap-3">
                <div 
                  v-for="product in paginatedProducts" 
                  :key="product.productId"
                  class="bf-card bf-glass p-3 list-card d-flex align-items-center gap-4 flex-wrap flex-md-nowrap"
                >
                  <img :src="product.imageUrl" class="list-card-img rounded-3" alt="Item Image" />
                  <div class="flex-grow-1 text-start">
                    <span class="bf-badge bf-badge-primary text-uppercase mb-1 small" v-if="product.category">
                      {{ product.category.categoryName }}
                    </span>
                    <h5 class="font-weight-bold mb-2">{{ product.productName }}</h5>
                    <p class="text-secondary small mb-0 text-truncate-2">{{ product.description }}</p>
                  </div>
                  <div class="text-md-end d-flex flex-column gap-2 flex-shrink-0 align-items-end justify-content-between min-width-card">
                    <span class="text-primary font-weight-bold fs-4">LKR {{ Number(product.price).toLocaleString() }}</span>
                    <router-link :to="'/product/detail/' + product.productId" class="bf-btn bf-btn-primary bf-btn-sm w-100">
                      Configure
                    </router-link>
                  </div>
                </div>
              </div>

              <!-- Pagination widgets -->
              <div class="d-flex justify-content-center mt-5" v-if="totalPages > 1">
                <nav>
                  <ul class="pagination pagination-cyber gap-2">
                    <li class="page-item" :class="{ disabled: currentPage === 1 }">
                      <button class="page-link" @click="changePage(currentPage - 1)">Previous</button>
                    </li>
                    <li 
                      v-for="p in totalPages" 
                      :key="p" 
                      class="page-item" 
                      :class="{ active: currentPage === p }"
                    >
                      <button class="page-link" @click="changePage(p)">{{ p }}</button>
                    </li>
                    <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                      <button class="page-link" @click="changePage(currentPage + 1)">Next</button>
                    </li>
                  </ul>
                </nav>
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
import ProductBox from '../../components/Product/ProductBox.vue';
import LoadingSkeleton from '../../components/Common/LoadingSkeleton.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';

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
      
      // Catalog layouts
      viewMode: 'grid',
      priceRange: 1000000,
      selectedBrand: 'all',
      inStockOnly: false,
      currentPage: 1,
      pageSize: 9,

      // Homepage widgets
      activeSlide: 0,
      slidesInterval: null,
      slides: [
        {
          badge: 'Exclusive Tech Drop',
          title: 'Uncompromising FPS Engine',
          subtitle: 'Build custom overclocked gaming systems using newly configured Ryzen 7 7800X3D chips & custom water cooling setups.'
        },
        {
          badge: 'Next-Gen Liquid Rigs',
          title: 'Flagship RTX Graphics Systems',
          subtitle: 'Experience path-tracing rendering and DLSS 3 frame generation inside high-fps benchmarked gaming PCs.'
        },
        {
          badge: 'Tactical Peripherals Drop',
          title: 'Precision Forge Accessories',
          subtitle: 'Elevate peripheral input response rates with hot-swappable custom keyboards and laser optical gaming sensors.'
        }
      ],
      homeStats: [
        { label: 'Rigs Built & Tested', value: '4,280+', icon: '⚙️' },
        { label: 'Avg User Benchmarks', value: '98.6%', icon: '📈' },
        { label: 'Verified Sellers', value: '18', icon: '🛡️' },
        { label: 'Active Components', value: '250+', icon: '🔌' }
      ],
      brandList: ['ASUS', 'MSI', 'Corsair', 'NZXT', 'Razer', 'AMD', 'Intel', 'Gigabyte', 'NVIDIA'],
      testimonials: [
        { quote: 'The performance tuning of their prebuilts is absolute art. Temps stay under 65C during heavy path-tracing renders.', author: 'Avantha L.', role: 'Computer Scientist' },
        { quote: 'Ordered my RTX 4080 configuration on Friday, delivered securely in custom padding by Sunday noon.', author: 'Ruwan K.', role: 'Visual Motion Artist' },
        { quote: 'Genuine parts, verified seller credentials, and instant technical resolution. My absolute first-choice hardware store.', author: 'Pasan M.', role: 'Forge Competitive Player' }
      ]
    };
  },
  computed: {
    isHomeRoute() {
      return this.$route.path === '/';
    },
    isLoggedIn() {
      return !!localStorage.getItem('bf-token');
    },
    homeFeaturedProducts() {
      return this.products.slice(0, 4);
    },
    filteredProducts() {
      let results = [...this.products];

      // 1. Filter by category
      if (this.selectedCategoryId !== 'all') {
        results = results.filter(p => p.categoryId === this.selectedCategoryId);
      }

      // 2. Filter by search query
      const query = this.searchQuery.trim().toLowerCase();
      if (query) {
        results = results.filter(p => 
          p.productName.toLowerCase().includes(query) ||
          p.description.toLowerCase().includes(query)
        );
      }

      // 3. Filter by price range
      results = results.filter(p => Number(p.price) <= this.priceRange);

      // 4. Filter by brand
      if (this.selectedBrand !== 'all') {
        results = results.filter(p => p.productName.toLowerCase().includes(this.selectedBrand.toLowerCase()));
      }

      // 5. Filter by availability
      if (this.inStockOnly) {
        results = results.filter(p => p.stock && p.stock > 0);
      }

      // Sort
      if (this.sortOrder === 'name-asc') {
        results.sort((a, b) => a.productName.localeCompare(b.productName));
      } else if (this.sortOrder === 'price-asc') {
        results.sort((a, b) => Number(a.price) - Number(b.price));
      } else if (this.sortOrder === 'price-desc') {
        results.sort((a, b) => Number(b.price) - Number(a.price));
      }

      return results;
    },
    totalPages() {
      return Math.ceil(this.filteredProducts.length / this.pageSize);
    },
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredProducts.slice(start, start + this.pageSize);
    }
  },
  watch: {
    selectedCategoryId() {
      this.currentPage = 1;
    },
    searchQuery() {
      this.currentPage = 1;
    },
    priceRange() {
      this.currentPage = 1;
    },
    selectedBrand() {
      this.currentPage = 1;
    },
    inStockOnly() {
      this.currentPage = 1;
    },
    isHomeRoute(val) {
      if (val) {
        this.$nextTick(() => {
          this.initParticles();
        });
      }
    }
  },
  methods: {
    scrollToFeatured() {
      const el = document.getElementById('featured-section');
      if (el) {
        el.scrollIntoView({ behavior: 'smooth' });
      }
    },
    resetFilters() {
      this.selectedCategoryId = 'all';
      this.searchQuery = '';
      this.priceRange = 1000000;
      this.selectedBrand = 'all';
      this.inStockOnly = false;
      this.currentPage = 1;
    },
    changePage(p) {
      if (p >= 1 && p <= this.totalPages) {
        this.currentPage = p;
        window.scrollTo({ top: 120, behavior: 'smooth' });
      }
    },
    async fetchCategories() {
      try {
        const response = await api.get('/category');
        this.categories = response.data.data || [];
      } catch (err) {
        console.error('Failed to fetch categories:', extractErrorMessage(err));
      }
    },
    async fetchProducts() {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.get('/product');
        this.products = response.data.data || [];
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    },
    handleSubscribe() {
      showToast({
        message: 'Welcome to the Forge. Subscribed successfully!',
        type: 'success',
        title: 'Newsletter Verified'
      });
    },
    initParticles() {
      const canvas = this.$refs.particleCanvas;
      if (!canvas) return;
      const ctx = canvas.getContext('2d');

      this.resizeListener = () => {
        if (canvas) {
          canvas.width = canvas.parentElement.clientWidth || window.innerWidth;
          canvas.height = canvas.parentElement.clientHeight || 550;
        }
      };

      this.resizeListener();
      window.addEventListener('resize', this.resizeListener);

      const particles = [];
      const particleCount = 45;

      for (let i = 0; i < particleCount; i++) {
        particles.push({
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height,
          vx: (Math.random() - 0.5) * 0.4,
          vy: (Math.random() - 0.5) * 0.4,
          radius: Math.random() * 2 + 1
        });
      }

      const animate = () => {
        if (!canvas) return;
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = 'rgba(59, 130, 246, 0.15)';
        ctx.strokeStyle = 'rgba(124, 58, 237, 0.04)';

        particles.forEach((p, idx) => {
          p.x += p.vx;
          p.y += p.vy;

          if (p.x < 0 || p.x > canvas.width) p.vx *= -1;
          if (p.y < 0 || p.y > canvas.height) p.vy *= -1;

          ctx.beginPath();
          ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
          ctx.fill();

          // Connect points
          for (let j = idx + 1; j < particles.length; j++) {
            const p2 = particles[j];
            const dist = Math.hypot(p2.x - p.x, p2.y - p.y);
            if (dist < 100) {
              ctx.beginPath();
              ctx.moveTo(p.x, p.y);
              ctx.lineTo(p2.x, p2.y);
              ctx.stroke();
            }
          }
        });

        this.animationFrameId = requestAnimationFrame(animate);
      };

      animate();
    }
  },
  mounted() {
    this.fetchCategories();
    this.fetchProducts();

    // Slides rotation interval
    this.slidesInterval = setInterval(() => {
      this.activeSlide = (this.activeSlide + 1) % this.slides.length;
    }, 6000);

    if (this.isHomeRoute) {
      this.$nextTick(() => {
        this.initParticles();
      });
    }
  },
  beforeUnmount() {
    if (this.slidesInterval) {
      clearInterval(this.slidesInterval);
    }
    if (this.resizeListener) {
      window.removeEventListener('resize', this.resizeListener);
    }
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
    }
  }
};
</script>

<style scoped>
/* ─── Shared Theme ─── */
.pulse-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  background: var(--bf-success);
  border-radius: 50%;
  margin-right: 6px;
  box-shadow: 0 0 8px var(--bf-success);
  animation: bf-pulse 1.5s infinite;
}

/* ─── Layout A: Homepage styles ─── */
.bf-particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 550px;
  pointer-events: none;
  z-index: 1;
}

.hero-row {
  min-height: 480px;
}

.bf-hero-badge-tag {
  background: rgba(59, 130, 246, 0.15) !important;
  color: var(--bf-primary) !important;
  border: 1px solid rgba(59, 130, 246, 0.25);
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  padding: 6px 14px;
}

.bf-hero-title-main {
  font-size: clamp(2.25rem, 5vw, 3.75rem);
  font-weight: 900;
  letter-spacing: -0.03em;
  line-height: 1.1;
}

.bf-hero-subtitle-main {
  max-width: 580px;
  line-height: 1.6;
}

.bf-slides-dots {
  display: flex;
  gap: 8px;
}

.bf-slide-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all var(--bf-transition-fast);
}

.bf-slide-dot.active {
  width: 24px;
  border-radius: 4px;
  background: var(--bf-primary);
  box-shadow: 0 0 10px var(--bf-primary-glow);
}

.hero-tech-img {
  position: relative;
  z-index: 2;
  border: 1.5px solid rgba(255, 255, 255, 0.08);
  max-height: 380px;
  object-fit: cover;
}

.hero-graphic-glow {
  position: absolute;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.15) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  pointer-events: none;
}

.stat-card {
  border-radius: var(--bf-radius-lg);
  border: 1px solid rgba(255, 255, 255, 0.04);
}

.stat-icon-badge {
  background: rgba(255, 255, 255, 0.03);
  width: 50px;
  height: 50px;
  border-radius: var(--bf-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.brand-grid .col {
  transition: transform var(--bf-transition-fast);
}

.brand-grid .col:hover {
  transform: translateY(-4px);
}

.brand-item-card {
  font-family: var(--bf-font-mono);
  font-weight: 700;
  font-size: 0.875rem;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05) !important;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  transition: all var(--bf-transition-fast);
}

.brand-item-card:hover {
  color: white !important;
  border-color: var(--bf-primary) !important;
  background: rgba(59, 130, 246, 0.08);
  box-shadow: 0 0 15px rgba(59, 130, 246, 0.15);
}

.testimonial-card {
  border: 1px solid rgba(255, 255, 255, 0.04) !important;
  transition: all var(--bf-transition-base);
}

.testimonial-card:hover {
  border-color: rgba(124, 58, 237, 0.25) !important;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.08);
}

.testimonial-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bf-gradient-accent);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.85rem;
}

.bf-newsletter-banner {
  background: var(--bf-gradient-dark);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.newsletter-glow {
  position: absolute;
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%, transparent 75%);
  bottom: -100px;
  right: -50px;
  pointer-events: none;
}

/* ─── Layout B: Catalog Explorer styles ─── */
.sticky-sidebar {
  position: sticky;
  top: 100px;
  z-index: 10;
}

.max-height-chips {
  max-height: 250px;
  overflow-y: auto;
  padding-right: 4px;
}

.filter-chip {
  padding: 8px 12px;
  font-size: 0.8125rem;
  border-radius: var(--bf-radius-md);
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.04);
  color: var(--bf-text-secondary);
  cursor: pointer;
  transition: all var(--bf-transition-fast);
}

.filter-chip:hover {
  color: white;
  background: rgba(255, 255, 255, 0.06);
}

.filter-chip.active {
  background: var(--bf-primary-light);
  border-color: var(--bf-primary);
  color: white;
  font-weight: 600;
}

.pagination-cyber .page-link {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  color: var(--bf-text-secondary);
  border-radius: var(--bf-radius-md);
  padding: 8px 16px;
  transition: all var(--bf-transition-fast);
}

.pagination-cyber .page-item.active .page-link {
  background: var(--bf-primary);
  border-color: var(--bf-primary);
  color: white;
  box-shadow: 0 0 10px var(--bf-primary-glow);
}

.pagination-cyber .page-link:hover {
  background: rgba(255, 255, 255, 0.08);
  color: white;
}

.pagination-cyber .page-item.disabled .page-link {
  background: transparent;
  border-color: rgba(255, 255, 255, 0.02);
  color: var(--bf-text-muted);
  opacity: 0.5;
}

/* List card styles */
.list-card {
  border: 1px solid rgba(255, 255, 255, 0.06) !important;
  transition: all var(--bf-transition-base);
}

.list-card:hover {
  border-color: var(--bf-primary) !important;
  transform: scale(1.01);
  box-shadow: var(--bf-shadow-lg);
}

.list-card-img {
  width: 90px;
  height: 90px;
  object-fit: cover;
  background: var(--bf-bg-tertiary);
  flex-shrink: 0;
}

.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.min-width-card {
  min-width: 140px;
}
</style>
