<template>
  <div class="ebv-page bf-fade-in">

    <!-- ═══ LAYOUT A: HOMEPAGE (/) — Ultimate Gaming Theme ═══ -->
    <div v-if="isHomeRoute" class="ebv-home">

      <!-- Premium Technology Hero Banner -->
      <div class="ebv-hero position-relative overflow-hidden">
        <!-- Parallax Background Layer & Glow Effects -->
        <div class="hero-bg-glow glow-1 animate-float"></div>
        <div class="hero-bg-glow glow-2 animate-float-delayed"></div>
        
        <div class="container position-relative z-3 py-5">
          <span class="badge bg-primary bg-opacity-25 text-cyan px-3 py-2 mb-3 rounded-pill text-uppercase tracking-wider">
            ⚡ Welcome to the Next-Gen PC Marketplace
          </span>
          <h1 class="ebv-hero-title text-white mb-3">
            Forge Your Ultimate <span class="text-gradient-primary">Gaming Rig</span>
          </h1>
          <p class="ebv-hero-subtitle text-muted fs-5 max-w-xl mx-auto mb-4">
            Buy and sell premium components with absolute security. Discover {{ products.length }}+ listings in our decentralized PC catalog.
          </p>

          <!-- Glassmorphic Large Search -->
          <form class="ebv-hero-search bf-glass-strong" @submit.prevent="goToSearchResults">
            <div class="search-cat-wrapper">
              <select class="ebv-hero-search-cat" v-model="heroCategory">
                <option value="all">⚡ All Categories</option>
                <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">
                  🔌 {{ cat.categoryName }}
                </option>
              </select>
            </div>
            <div class="search-input-wrapper flex-grow-1">
              <i class="bi bi-search search-icon"></i>
              <input
                type="text"
                class="ebv-hero-search-input"
                v-model="heroQuery"
                placeholder="Search RTX 4080, Mechanical Keyboards, DDR5 RAM..."
              />
            </div>
            <button type="submit" class="bf-btn-premium border-0 py-2.5 px-4 me-2">
              Search
            </button>
          </form>

          <!-- Trending Brands Chips -->
          <div class="ebv-hero-trending mt-4" v-if="brandList.length">
            <span class="ebv-hero-trending-label text-muted small me-2"><i class="bi bi-fire text-warning"></i> Trending Brands:</span>
            <button
              v-for="brand in brandList.slice(0, 7)"
              :key="brand"
              class="ebv-hero-trending-chip bf-btn-ghost py-1 px-3"
              @click="quickBrandSearch(brand)"
            >
              {{ brand }}
            </button>
          </div>
        </div>
      </div>

      <div class="container py-5">

        <!-- Category Grid Section -->
        <div class="ebv-section-header mb-4">
          <div>
            <h2 class="ebv-section-title">Shop by Category</h2>
            <p class="ebv-section-subtitle">Browse customized gaming & workstation categories</p>
          </div>
        </div>
        <div class="ebv-category-grid mb-5">
          <div
            v-for="cat in categories.slice(0, 10)"
            :key="cat.categoryId"
            class="ebv-category-tile bf-glass-card"
            @click="goToCategory(cat.categoryId)"
          >
            <div class="ebv-category-tile-image-wrapper mb-2">
              <img :src="getCategoryImage(cat)" class="ebv-category-tile-img" alt="Category Image" />
            </div>
            <span>{{ cat.categoryName }}</span>
          </div>
          <div class="ebv-category-tile ebv-category-tile-all bf-glass-card" @click="goToCategory('all')">
            <div class="ebv-category-tile-image-wrapper mb-2">
              <img src="https://images.unsplash.com/photo-1600541519463-ee3745d0485f?auto=format&fit=crop&w=150&q=80" class="ebv-category-tile-img" alt="View All" />
            </div>
            <span>View All</span>
          </div>
        </div>

        <!-- Premium Statistics Strip -->
        <div class="ebv-stats-strip bf-glass mb-5 p-4">
          <div class="ebv-stat d-flex align-items-center gap-3" v-for="(stat, idx) in homeStats" :key="idx">
            <div class="ebv-stat-icon-wrapper text-gradient-primary">
              <span class="fs-2">{{ stat.icon }}</span>
            </div>
            <div>
              <strong class="fs-4 text-white">{{ stat.value }}</strong>
              <small class="text-muted text-uppercase tracking-wider font-semibold">{{ stat.label }}</small>
            </div>
          </div>
        </div>

        <!-- Featured / Trending Listings -->
        <div class="ebv-section-header d-flex justify-content-between align-items-end mb-4" id="featured-section">
          <div>
            <h2 class="ebv-section-title">Trending Hardware Drops</h2>
            <p class="ebv-section-subtitle">Hottest hardware, peripherals, and cases verified by our team</p>
          </div>
          <router-link to="/product" class="bf-btn-ghost text-cyan text-decoration-none">
            See All Listings <i class="bi bi-arrow-right ms-1"></i>
          </router-link>
        </div>

        <!-- Skeletons / Loading -->
        <div v-if="loading" class="row g-4 mb-5">
          <div v-for="n in 4" :key="n" class="col-xl-3 col-md-6">
            <div class="bf-skeleton bf-skeleton-card"></div>
          </div>
        </div>
        <!-- Product Grid -->
        <div v-else class="row g-4 mb-5">
          <div v-for="product in homeFeaturedProducts" :key="product.productId" class="col-xl-3 col-md-6 d-flex">
            <ProductBox :product="product" />
          </div>
        </div>

        <!-- Testimonials -->
        <div class="ebv-section-header mb-4">
          <div>
            <h2 class="ebv-section-title">Enthusiast Reviews</h2>
            <p class="ebv-section-subtitle">What buyers and sellers are saying about ByteForge</p>
          </div>
        </div>
        <div class="row g-4 mb-5">
          <div class="col-md-4" v-for="(t, index) in testimonials" :key="index">
            <div class="ebv-testimonial-card bf-glass">
              <div class="ebv-testimonial-stars text-warning mb-2">
                <i class="bi bi-star-fill" v-for="n in 5" :key="n"></i>
              </div>
              <p class="text-secondary italic mb-4">"{{ t.quote }}"</p>
              <div class="ebv-testimonial-author d-flex align-items-center gap-3">
                <div class="ebv-testimonial-avatar bf-btn-premium border-0">
                  {{ t.author.charAt(0) }}
                </div>
                <div>
                  <strong class="text-white small">{{ t.author }}</strong>
                  <small class="text-muted d-block" style="font-size: 0.75rem;">{{ t.role }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Cyber Newsletter Signup -->
        <div class="ebv-newsletter bf-glass-strong p-5 position-relative overflow-hidden">
          <div class="glow-newsletter animate-float"></div>
          <div class="position-relative z-2">
            <h3 class="text-white mb-2">Get Hardware Drop Alerts</h3>
            <p class="text-muted mb-4 max-w-md mx-auto">
              Get restock notifications, flash deals, and weekly GPU stock summaries directly to your inbox.
            </p>
            <div class="ebv-newsletter-form d-flex gap-2 justify-content-center max-w-sm mx-auto flex-column flex-sm-row">
              <input type="email" placeholder="Enter your email address" class="bf-search-input py-2 px-3 flex-grow-1" />
              <button @click="handleSubscribe" class="bf-btn-premium border-0 py-2.5 px-4">
                Subscribe
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ LAYOUT B: CATALOG PAGE (/product) — Cyber Filter Grid ═══ -->
    <div v-else class="ebv-catalog py-5">
      <div class="container">

        <!-- Search & Control Toolbar -->
        <div class="ebv-catalog-toolbar bf-glass mb-4 p-3 d-flex flex-wrap align-items-center justify-content-between gap-3">
          <div class="ebv-catalog-search flex-grow-1">
            <i class="bi bi-search text-cyan me-2"></i>
            <input type="text" v-model="searchQuery" placeholder="Filter active hardware listings..." class="bg-transparent border-0 text-white w-100" />
          </div>
          <div class="ebv-catalog-toolbar-right d-flex align-items-center gap-3">
            <span class="ebv-results-count text-muted">
              Found <strong class="text-white">{{ filteredProducts.length }}</strong> active listings
            </span>
            <select class="ebv-sort-select bf-glass" v-model="sortOrder">
              <option value="default">Best Match</option>
              <option value="name-asc">Name (A-Z)</option>
              <option value="price-asc">Price: Low to High</option>
              <option value="price-desc">Price: High to Low</option>
            </select>
            <div class="ebv-view-toggle d-flex bf-glass">
              <button :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'" class="btn py-1 px-2 text-white border-0"><i class="bi bi-grid-3x3-gap-fill"></i></button>
              <button :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'" class="btn py-1 px-2 text-white border-0"><i class="bi bi-list-task"></i></button>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <!-- Cyber filter sidebar -->
          <div class="col-lg-3">
            <div class="ebv-filter-rail bf-glass p-4">
              <div class="ebv-filter-rail-header d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom border-secondary border-opacity-25">
                <h5 class="text-white m-0">Filters</h5>
                <button class="ebv-reset-link text-cyan bg-transparent border-0 small" @click="resetFilters">Reset All</button>
              </div>

              <!-- Categories -->
              <div class="ebv-filter-group mb-4">
                <h6 class="text-muted text-uppercase tracking-wider mb-2 font-bold small">Category</h6>
                <div class="ebv-filter-list d-flex flex-column gap-2">
                  <label class="ebv-filter-radio d-flex align-items-center gap-2 text-muted cursor-pointer" :class="{ active: selectedCategoryId === 'all' }">
                    <input type="radio" name="cat" :checked="selectedCategoryId === 'all'" @change="selectedCategoryId = 'all'" class="form-check-input" />
                    All Categories
                  </label>
                  <label
                    v-for="cat in categories"
                    :key="cat.categoryId"
                    class="ebv-filter-radio d-flex align-items-center gap-2 text-muted cursor-pointer"
                    :class="{ active: selectedCategoryId === cat.categoryId }"
                  >
                    <input type="radio" name="cat" :checked="selectedCategoryId === cat.categoryId" @change="selectedCategoryId = cat.categoryId" class="form-check-input" />
                    {{ cat.categoryName }}
                  </label>
                </div>
              </div>

              <!-- Price Slider -->
              <div class="ebv-filter-group mb-4">
                <h6 class="text-muted text-uppercase tracking-wider mb-2 font-bold small">Price: Up to LKR {{ priceRange.toLocaleString() }}</h6>
                <input type="range" min="0" max="1000000" step="5000" v-model.number="priceRange" class="w-100 form-range accent-cyan" />
                <div class="d-flex justify-content-between text-muted" style="font-size: 0.75rem;">
                  <span>LKR 0</span>
                  <span>1,000,000+</span>
                </div>
              </div>

              <!-- Brands -->
              <div class="ebv-filter-group mb-4">
                <h6 class="text-muted text-uppercase tracking-wider mb-2 font-bold small">Brand / Brand keyword</h6>
                <select class="ebv-filter-select bf-glass p-2 w-100" v-model="selectedBrand">
                  <option value="all">All Brands</option>
                  <option v-for="b in brandList" :key="b" :value="b">{{ b }}</option>
                </select>
              </div>

              <!-- Listing Type -->
              <div class="ebv-filter-group mb-4">
                <h6 class="text-muted text-uppercase tracking-wider mb-2 font-bold small">Listing Type</h6>
                <div class="ebv-filter-list d-flex flex-column gap-2">
                  <label class="ebv-filter-radio d-flex align-items-center gap-2 text-muted cursor-pointer" :class="{ active: selectedListingType === 'all' }">
                    <input type="radio" name="listingType" :checked="selectedListingType === 'all'" @change="selectedListingType = 'all'" class="form-check-input" />
                    All Listings
                  </label>
                  <label class="ebv-filter-radio d-flex align-items-center gap-2 text-muted cursor-pointer" :class="{ active: selectedListingType === 'buyitnow' }">
                    <input type="radio" name="listingType" :checked="selectedListingType === 'buyitnow'" @change="selectedListingType = 'buyitnow'" class="form-check-input" />
                    Buy It Now (Fixed)
                  </label>
                  <label class="ebv-filter-radio d-flex align-items-center gap-2 text-muted cursor-pointer" :class="{ active: selectedListingType === 'auction' }">
                    <input type="radio" name="listingType" :checked="selectedListingType === 'auction'" @change="selectedListingType = 'auction'" class="form-check-input" />
                    🔨 Auctions (Bids)
                  </label>
                </div>
              </div>

              <!-- Stock Options -->
              <div class="ebv-filter-group">
                <label class="d-flex align-items-center gap-2 text-muted cursor-pointer">
                  <input type="checkbox" v-model="inStockOnly" class="form-check-input" />
                  <span>In Stock Only</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Product Grid/List Results -->
          <div class="col-lg-9">
            <!-- Loading Skeletons -->
            <div v-if="loading" class="row g-4">
              <div v-for="n in 6" :key="n" class="col-xl-4 col-md-6">
                <div class="bf-skeleton bf-skeleton-card" style="height: 350px;"></div>
              </div>
            </div>

            <!-- Error State -->
            <div v-else-if="error" class="ebv-empty-state bf-glass p-5 text-center">
              <div class="ebv-empty-icon text-danger fs-1"><i class="bi bi-exclamation-triangle"></i></div>
              <h5 class="text-white mt-3">Failed to load PC Catalog</h5>
              <p class="text-muted">{{ error }}</p>
              <button class="bf-btn-premium border-0 mt-3" @click="fetchProducts">Retry Connection</button>
            </div>

            <!-- Empty State -->
            <div v-else-if="filteredProducts.length === 0" class="ebv-empty-state bf-glass p-5 text-center">
              <div class="ebv-empty-icon text-cyan fs-1"><i class="bi bi-emoji-frown"></i></div>
              <h5 class="text-white mt-3">No gear matches your specifications</h5>
              <p class="text-muted">Widening your filter price or clearing search terms could help.</p>
              <button class="bf-btn-premium border-0 mt-3" @click="resetFilters">Reset Filters</button>
            </div>

            <!-- Main Listing Display -->
            <div v-else>
              <!-- GRID VIEW -->
              <div v-if="viewMode === 'grid'" class="row g-4">
                <div
                  v-for="(product, idx) in paginatedProducts"
                  :key="product.productId"
                  class="col-xl-4 col-md-6 d-flex bf-fade-in"
                  :style="{ 'animation-delay': (idx * 0.04) + 's' }"
                >
                  <ProductBox :product="product" />
                </div>
              </div>

              <!-- LIST VIEW -->
              <div v-else class="d-flex flex-column gap-3">
                <div 
                  v-for="product in paginatedProducts" 
                  :key="product.productId" 
                  class="ebv-list-row bf-glass-card p-3 d-flex flex-column flex-md-row align-items-start align-items-md-center gap-4"
                >
                  <img :src="product.imageUrl" class="ebv-list-row-img img-fluid rounded-3" alt="Hardware Image" @error="handleImageError" />
                  
                  <div class="ebv-list-row-body flex-grow-1 text-start">
                    <span v-if="product.categoryName" class="ebv-list-row-cat text-gradient-primary text-uppercase font-bold small tracking-wider">{{ product.categoryName }}</span>
                    <h5 class="text-white mt-1 mb-2">{{ product.productName }}</h5>
                    <p class="text-muted small mb-3">{{ product.description }}</p>
                    <div class="ebv-list-row-seller text-muted d-flex align-items-center gap-1">
                      <i class="bi bi-shield-check text-cyan" v-if="product.storeVerified"></i>
                      <span>{{ product.storeName || 'Enthusiast Seller' }}</span>
                    </div>
                  </div>
                  
                  <div class="ebv-list-row-actions text-end d-flex flex-column gap-2 align-self-stretch align-self-md-auto justify-content-between align-items-md-end">
                    <span class="ebv-list-row-price text-gradient fs-4 font-bold">{{ formatCurrency(product.price) }}</span>
                    <router-link :to="'/product/detail/' + product.productId" class="bf-btn-premium border-0 py-2 text-decoration-none">
                      Inspect Gear
                    </router-link>
                  </div>
                </div>
              </div>

              <!-- Cybernetic Pagination -->
              <div class="ebv-pagination d-flex justify-content-center align-items-center gap-2 mt-5" v-if="totalPages > 1">
                <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)" class="bf-btn-ghost py-1 px-3">
                  <i class="bi bi-arrow-left"></i>
                </button>
                <button
                  v-for="p in totalPages"
                  :key="p"
                  :class="{ active: currentPage === p }"
                  @click="changePage(p)"
                  class="bf-btn-ghost py-1 px-3"
                >
                  {{ p }}
                </button>
                <button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)" class="bf-btn-ghost py-1 px-3">
                  <i class="bi bi-arrow-right"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import api, { formatPrice, extractErrorMessage } from '../../utils/api';
import ProductBox from '../../components/Product/ProductBox.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'ProductView',
  components: { ProductBox },
  data() {
    return {
      products: [],
      categories: [],
      selectedCategoryId: 'all',
      searchQuery: '',
      sortOrder: 'default',
      loading: true,
      error: null,

      // Catalog layout state
      viewMode: 'grid',
      priceRange: 1000000,
      selectedBrand: 'all',
      selectedListingType: 'all',
      inStockOnly: false,
      currentPage: 1,
      pageSize: 9,

      // Homepage hero search
      heroQuery: '',
      heroCategory: 'all',

      homeStats: [
        { label: 'Listings Sold', value: '4,280+', icon: '📦' },
        { label: 'Buyer Satisfaction', value: '98.6%', icon: '⭐' },
        { label: 'Verified Sellers', value: '18', icon: '🛡️' },
        { label: 'Active Listings', value: '250+', icon: '🔌' }
      ],
      brandList: ['ASUS', 'MSI', 'Corsair', 'NZXT', 'Razer', 'AMD', 'Intel', 'Gigabyte', 'NVIDIA'],
      testimonials: [
        { quote: 'Genuine parts, verified seller, exactly as described. Fast shipping too.', author: 'Avantha L.', role: 'Verified Buyer' },
        { quote: 'Ordered on Friday, delivered securely by Sunday. Will buy again.', author: 'Ruwan K.', role: 'Verified Buyer' },
        { quote: 'Great seller communication and the item matched the listing perfectly.', author: 'Pasan M.', role: 'Verified Buyer' }
      ]
    };
  },
  computed: {
    isHomeRoute() {
      return this.$route.path === '/';
    },
    homeFeaturedProducts() {
      return this.products.slice(0, 4);
    },
    filteredProducts() {
      let results = [...this.products];

      if (this.selectedCategoryId !== 'all') {
        results = results.filter(p => p.categoryId === this.selectedCategoryId || (p.category && p.category.categoryId === this.selectedCategoryId));
      }

      const query = this.searchQuery.trim().toLowerCase();
      if (query) {
        results = results.filter(p =>
          p.productName.toLowerCase().includes(query) ||
          p.description.toLowerCase().includes(query)
        );
      }

      results = results.filter(p => Number(p.price) <= this.priceRange);

      if (this.selectedBrand !== 'all') {
        results = results.filter(p => p.productName.toLowerCase().includes(this.selectedBrand.toLowerCase()));
      }

      if (this.selectedListingType === 'auction') {
        results = results.filter(p => p.isAuction);
      } else if (this.selectedListingType === 'buyitnow') {
        results = results.filter(p => !p.isAuction);
      }

      if (this.inStockOnly) {
        results = results.filter(p => p.stock && p.stock > 0);
      }

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
    selectedCategoryId() { this.currentPage = 1; },
    searchQuery() { this.currentPage = 1; },
    priceRange() { this.currentPage = 1; },
    selectedBrand() { this.currentPage = 1; },
    selectedListingType() { this.currentPage = 1; },
    inStockOnly() { this.currentPage = 1; },
    '$route.query.q': {
      immediate: true,
      handler(newQuery) {
        if (newQuery) {
          this.searchQuery = newQuery;
        }
      }
    }
  },
  methods: {
    formatCurrency(v) {
      return formatPrice(v);
    },
    resetFilters() {
      this.selectedCategoryId = 'all';
      this.searchQuery = '';
      this.priceRange = 1000000;
      this.selectedBrand = 'all';
      this.selectedListingType = 'all';
      this.inStockOnly = false;
      this.currentPage = 1;
    },
    changePage(p) {
      if (p >= 1 && p <= this.totalPages) {
        this.currentPage = p;
        window.scrollTo({ top: 120, behavior: 'smooth' });
      }
    },
    goToSearchResults() {
      this.searchQuery = this.heroQuery;
      this.selectedCategoryId = this.heroCategory;
      this.$router.push('/product');
    },
    quickBrandSearch(brand) {
      this.selectedBrand = brand;
      this.searchQuery = '';
      this.$router.push('/product');
    },
    goToCategory(categoryId) {
      this.selectedCategoryId = categoryId;
      this.$router.push('/product');
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
        message: 'Subscribed successfully! Watch your inbox for deals.',
        type: 'success',
        title: 'Newsletter'
      });
    },
    handleImageError(event) {
      event.target.src = 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?auto=format&fit=crop&w=500&q=80';
    },
    getCategoryImage(cat) {
      if (cat.imageUrl && cat.imageUrl.trim() !== '') {
        return cat.imageUrl;
      }
      const name = cat.categoryName.toLowerCase();
      if (name.includes('cpu') || name.includes('process')) {
        return 'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?auto=format&fit=crop&w=150&q=80';
      }
      if (name.includes('gpu') || name.includes('graph') || name.includes('card')) {
        return 'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=150&q=80';
      }
      if (name.includes('ssd') || name.includes('stor') || name.includes('drive')) {
        return 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=150&q=80';
      }
      if (name.includes('ram') || name.includes('memo')) {
        return 'https://images.unsplash.com/photo-1562976540-1502c2145186?auto=format&fit=crop&w=150&q=80';
      }
      if (name.includes('case') || name.includes('chass') || name.includes('build') || name.includes('pc')) {
        return 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?auto=format&fit=crop&w=150&q=80';
      }
      if (name.includes('key') || name.includes('mous') || name.includes('board') || name.includes('periph')) {
        return 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?auto=format&fit=crop&w=150&q=80';
      }
      if (name.includes('screen') || name.includes('monit') || name.includes('display')) {
        return 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=150&q=80';
      }
      return 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=150&q=80';
    }
  },
  mounted() {
    this.fetchCategories();
    this.fetchProducts();
  }
};
</script>

<style scoped>
/* ─── Premium Hero Section ─── */
.ebv-hero {
  padding: 100px 0 80px;
  background: radial-gradient(circle at top, rgba(59, 130, 246, 0.12) 0%, rgba(11, 15, 25, 0.9) 80%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.hero-bg-glow {
  position: absolute;
  width: 400px; height: 400px;
  border-radius: 50%;
  filter: blur(100px);
  z-index: 1;
}
.glow-1 { top: -10%; left: 10%; background: rgba(124, 58, 237, 0.15); }
.glow-2 { bottom: -10%; right: 10%; background: rgba(6, 182, 212, 0.15); }

.ebv-hero-title {
  font-size: clamp(2rem, 5vw, 3.5rem);
  font-weight: 800;
  letter-spacing: -1px;
}

/* Large Search */
.ebv-hero-search {
  display: flex;
  max-width: 800px;
  margin: 0 auto;
  border-radius: 30px;
  padding: 6px;
  align-items: center;
}

.search-cat-wrapper {
  padding-left: 12px;
}

.ebv-hero-search-cat {
  border: none;
  background: transparent;
  color: var(--bf-text-secondary);
  font-size: 0.9rem;
  font-weight: 600;
  outline: none;
  cursor: pointer;
  padding: 8px;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  padding-left: 16px;
}

.search-icon {
  color: var(--bf-text-muted);
  margin-right: 10px;
}

.ebv-hero-search-input {
  border: none;
  background: transparent;
  color: #fff;
  font-size: 1rem;
  width: 100%;
}
.ebv-hero-search-input:focus {
  outline: none;
}

.ebv-hero-trending {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.ebv-hero-trending-chip {
  cursor: pointer;
  font-weight: 500;
  font-size: 0.85rem;
}
.ebv-hero-trending-chip:hover {
  background: var(--bf-primary);
  border-color: var(--bf-primary);
  color: white;
}

/* Category Grid */
.ebv-category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 16px;
}

.ebv-category-tile {
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.88rem;
  text-align: center;
  color: var(--bf-text-primary);
}

.ebv-category-tile-image-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--bf-border);
  box-shadow: var(--bf-shadow-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ebv-category-tile-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--bf-transition-base);
}

.ebv-category-tile:hover .ebv-category-tile-img {
  transform: scale(1.1);
}

.ebv-category-tile-all {
  border: 1px dashed var(--bf-cyan) !important;
}

/* Stats Strip */
.ebv-stats-strip {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

/* Testimonial Cards */
.ebv-testimonial-card {
  padding: 30px 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.ebv-testimonial-stars i {
  font-size: 0.95rem;
  margin-right: 2px;
}

.ebv-testimonial-avatar {
  width: 38px; height: 38px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-weight: bold;
}

/* Newsletter glow */
.glow-newsletter {
  position: absolute;
  top: -50%; left: -20%;
  width: 300px; height: 300px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.15) 0%, transparent 70%);
  filter: blur(40px);
}

/* Catalog Toolbar overrides */
.ebv-catalog-toolbar {
  border-radius: var(--bf-radius-lg);
}

.ebv-catalog-search {
  display: flex;
  align-items: center;
  background: rgba(0, 0, 0, 0.25);
  border-radius: var(--bf-radius-md);
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.ebv-sort-select {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: var(--bf-bg-secondary);
  color: var(--bf-text-primary);
  border-radius: var(--bf-radius-md);
  padding: 8px 16px;
  font-size: 0.85rem;
  outline: none;
}

.ebv-view-toggle {
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.ebv-view-toggle button {
  background: transparent;
  transition: all 0.2s ease;
}
.ebv-view-toggle button.active {
  background: var(--bf-primary);
}

/* Filter Rail */
.ebv-filter-rail {
  border-radius: var(--bf-radius-lg);
  position: sticky;
  top: 100px;
}

.ebv-filter-radio.active {
  color: var(--bf-cyan) !important;
  font-weight: 700;
}

/* List view row */
.ebv-list-row {
  border-radius: var(--bf-radius-lg);
}

.ebv-list-row-img {
  width: 140px;
  height: 110px;
  object-fit: cover;
  background: var(--bf-bg-secondary);
}

/* Pagination */
.ebv-pagination button.active {
  background: var(--bf-gradient-primary);
  border-color: transparent;
  color: #fff;
  font-weight: bold;
}

@media (max-width: 768px) {
  .ebv-hero-search {
    flex-direction: column;
    border-radius: var(--bf-radius-lg);
    padding: 16px;
    gap: 12px;
  }
  .search-cat-wrapper, .search-input-wrapper {
    width: 100%;
    padding: 0;
  }
  .ebv-hero-search-cat {
    width: 100%;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding-bottom: 8px;
  }
  .ebv-list-row-actions {
    flex-direction: row;
    align-items: center;
    width: 100%;
  }
  .ebv-list-row-img {
    width: 100%;
    height: 180px;
  }
}
</style>
