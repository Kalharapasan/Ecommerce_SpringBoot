<template>
  <div class="ebv-page bf-fade-in">

    <!-- ═══ LAYOUT A: HOMEPAGE (/) — eBay style ═══ -->
    <div v-if="isHomeRoute" class="ebv-home">

      <!-- Hero: big centered search, like eBay's homepage banner -->
      <div class="ebv-hero">
        <div class="container">
          <h1 class="ebv-hero-title">Find PC hardware from trusted sellers</h1>
          <p class="ebv-hero-subtitle">Search {{ products.length }}+ components across {{ categories.length }} categories</p>

          <form class="ebv-hero-search" @submit.prevent="goToSearchResults">
            <select class="ebv-hero-search-cat" v-model="heroCategory">
              <option value="all">All Categories</option>
              <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">
                {{ cat.categoryName }}
              </option>
            </select>
            <input
              type="text"
              class="ebv-hero-search-input"
              v-model="heroQuery"
              placeholder="Search for graphics cards, CPUs, keyboards…"
            />
            <button type="submit" class="ebv-hero-search-btn">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16"><path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/></svg>
              Search
            </button>
          </form>

          <div class="ebv-hero-trending" v-if="brandList.length">
            <span class="ebv-hero-trending-label">Trending:</span>
            <button
              v-for="brand in brandList.slice(0, 6)"
              :key="brand"
              class="ebv-hero-trending-chip"
              @click="quickBrandSearch(brand)"
            >
              {{ brand }}
            </button>
          </div>
        </div>
      </div>

      <div class="container py-5">

        <!-- Shop by category tiles -->
        <div class="ebv-section-header">
          <h2 class="ebv-section-title">Shop by Category</h2>
        </div>
        <div class="ebv-category-grid mb-5">
          <div
            v-for="cat in categories.slice(0, 10)"
            :key="cat.categoryId"
            class="ebv-category-tile"
            @click="goToCategory(cat.categoryId)"
          >
            <div class="ebv-category-tile-icon">🖥️</div>
            <span>{{ cat.categoryName }}</span>
          </div>
          <div class="ebv-category-tile ebv-category-tile-all" @click="goToCategory('all')">
            <div class="ebv-category-tile-icon">＋</div>
            <span>Shop All</span>
          </div>
        </div>

        <!-- Stats strip -->
        <div class="ebv-stats-strip mb-5">
          <div class="ebv-stat" v-for="(stat, idx) in homeStats" :key="idx">
            <span class="ebv-stat-icon">{{ stat.icon }}</span>
            <div>
              <strong>{{ stat.value }}</strong>
              <small>{{ stat.label }}</small>
            </div>
          </div>
        </div>

        <!-- Trending listings -->
        <div class="ebv-section-header" id="featured-section">
          <div>
            <h2 class="ebv-section-title">Trending Listings</h2>
            <p class="ebv-section-subtitle">Popular picks from verified sellers this week</p>
          </div>
          <router-link to="/product" class="ebv-see-all">See all &rarr;</router-link>
        </div>

        <div v-if="loading" class="row g-4 mb-5">
          <div v-for="n in 4" :key="n" class="col-xl-3 col-md-6">
            <LoadingSkeleton type="card" />
          </div>
        </div>
        <div v-else class="row g-4 mb-5">
          <div v-for="product in homeFeaturedProducts" :key="product.productId" class="col-xl-3 col-md-6 d-flex">
            <ProductBox :product="product" />
          </div>
        </div>

        <!-- Testimonials -->
        <div class="ebv-section-header">
          <h2 class="ebv-section-title">What Buyers Are Saying</h2>
        </div>
        <div class="row g-4 mb-5">
          <div class="col-md-4" v-for="(t, index) in testimonials" :key="index">
            <div class="ebv-testimonial-card">
              <div class="ebv-testimonial-stars">★★★★★</div>
              <p>"{{ t.quote }}"</p>
              <div class="ebv-testimonial-author">
                <div class="ebv-testimonial-avatar">{{ t.author.charAt(0) }}</div>
                <div>
                  <strong>{{ t.author }}</strong>
                  <small>{{ t.role }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Newsletter -->
        <div class="ebv-newsletter">
          <h3>Get deal alerts before everyone else</h3>
          <p>Restock alerts, flash sales, and new hardware drops — straight to your inbox.</p>
          <div class="ebv-newsletter-form">
            <input type="email" placeholder="Enter your email address" />
            <button @click="handleSubscribe">Subscribe</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ LAYOUT B: CATALOG PAGE (/product) — eBay style ═══ -->
    <div v-else class="ebv-catalog py-4">
      <div class="container">

        <!-- Search + toolbar -->
        <div class="ebv-catalog-toolbar mb-4">
          <div class="ebv-catalog-search">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16"><path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/></svg>
            <input type="text" v-model="searchQuery" placeholder="Search listings…" />
          </div>
          <div class="ebv-catalog-toolbar-right">
            <span class="ebv-results-count">
              <strong>{{ filteredProducts.length }}</strong> results
            </span>
            <select class="ebv-sort-select" v-model="sortOrder">
              <option value="default">Best Match</option>
              <option value="name-asc">Name (A-Z)</option>
              <option value="price-asc">Price: Low to High</option>
              <option value="price-desc">Price: High to Low</option>
            </select>
            <div class="ebv-view-toggle">
              <button :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'" title="Grid view">▦</button>
              <button :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'" title="List view">☰</button>
            </div>
          </div>
        </div>

        <div class="row g-4">
          <!-- Filter rail -->
          <div class="col-lg-3">
            <div class="ebv-filter-rail">
              <div class="ebv-filter-rail-header">
                <h5>Filter</h5>
                <button class="ebv-reset-link" @click="resetFilters">Reset</button>
              </div>

              <div class="ebv-filter-group">
                <h6>Category</h6>
                <div class="ebv-filter-list">
                  <label class="ebv-filter-radio" :class="{ active: selectedCategoryId === 'all' }">
                    <input type="radio" name="cat" :checked="selectedCategoryId === 'all'" @change="selectedCategoryId = 'all'" />
                    All Categories
                  </label>
                  <label
                    v-for="cat in categories"
                    :key="cat.categoryId"
                    class="ebv-filter-radio"
                    :class="{ active: selectedCategoryId === cat.categoryId }"
                  >
                    <input type="radio" name="cat" :checked="selectedCategoryId === cat.categoryId" @change="selectedCategoryId = cat.categoryId" />
                    {{ cat.categoryName }}
                  </label>
                </div>
              </div>

              <div class="ebv-filter-group">
                <h6>Price: up to LKR {{ priceRange.toLocaleString() }}</h6>
                <input type="range" min="0" max="1000000" step="5000" v-model.number="priceRange" class="ebv-price-slider" />
                <div class="ebv-price-range-labels">
                  <span>LKR 0</span>
                  <span>LKR 1,000,000</span>
                </div>
              </div>

              <div class="ebv-filter-group">
                <h6>Manufacturer</h6>
                <select class="ebv-filter-select" v-model="selectedBrand">
                  <option value="all">All Manufacturers</option>
                  <option v-for="b in brandList" :key="b" :value="b">{{ b }}</option>
                </select>
              </div>

              <div class="ebv-filter-group">
                <label class="ebv-filter-switch">
                  <input type="checkbox" v-model="inStockOnly" />
                  <span>In Stock Only</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Results -->
          <div class="col-lg-9">
            <div v-if="loading" class="row g-4">
              <div v-for="n in 6" :key="n" class="col-xl-4 col-md-6">
                <LoadingSkeleton type="card" />
              </div>
            </div>

            <div v-else-if="error" class="ebv-empty-state">
              <div class="ebv-empty-icon">⚠</div>
              <h5>Couldn't load listings</h5>
              <p>{{ error }}</p>
              <button class="ebv-btn-primary" @click="fetchProducts">Retry</button>
            </div>

            <div v-else-if="filteredProducts.length === 0" class="ebv-empty-state">
              <div class="ebv-empty-icon">🔍</div>
              <h5>No listings match your filters</h5>
              <p>Try widening your price range or clearing a filter.</p>
              <button class="ebv-btn-primary" @click="resetFilters">Clear All Filters</button>
            </div>

            <div v-else>
              <div v-if="viewMode === 'grid'" class="row g-4">
                <div
                  v-for="(product, idx) in paginatedProducts"
                  :key="product.productId"
                  class="col-xl-4 col-md-6 d-flex bf-fade-in"
                  :style="{ 'animation-delay': (idx * 0.05) + 's' }"
                >
                  <ProductBox :product="product" />
                </div>
              </div>

              <div v-else class="d-flex flex-column gap-3">
                <div v-for="product in paginatedProducts" :key="product.productId" class="ebv-list-row">
                  <img :src="product.imageUrl" class="ebv-list-row-img" alt="Item Image" />
                  <div class="ebv-list-row-body">
                    <span v-if="product.category" class="ebv-list-row-cat">{{ product.category.categoryName }}</span>
                    <h5>{{ product.productName }}</h5>
                    <p>{{ product.description }}</p>
                    <div class="ebv-list-row-seller" v-if="product.store">
                      {{ product.store.storeName }}
                      <span v-if="product.store.isVerified" class="ebv-verified-dot" title="Verified Seller">✓</span>
                    </div>
                  </div>
                  <div class="ebv-list-row-actions">
                    <span class="ebv-list-row-price">{{ formatCurrency(product.price) }}</span>
                    <router-link :to="'/product/detail/' + product.productId" class="ebv-btn-primary">
                      View Listing
                    </router-link>
                  </div>
                </div>
              </div>

              <!-- Pagination -->
              <div class="ebv-pagination" v-if="totalPages > 1">
                <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)">Previous</button>
                <button
                  v-for="p in totalPages"
                  :key="p"
                  :class="{ active: currentPage === p }"
                  @click="changePage(p)"
                >
                  {{ p }}
                </button>
                <button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">Next</button>
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

      // Catalog layout state
      viewMode: 'grid',
      priceRange: 1000000,
      selectedBrand: 'all',
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
    inStockOnly() { this.currentPage = 1; }
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
    }
  },
  mounted() {
    this.fetchCategories();
    this.fetchProducts();
  }
};
</script>

<style scoped>
/* ═══ eBay-inspired palette (scoped overrides, additive only) ═══ */
.ebv-page {
  --eb-blue: #0654ba;
  --eb-blue-dark: #044289;
  --eb-yellow: #f5af02;
  --eb-green: #2e7d32;
}

/* ─── Hero ─── */
.ebv-hero {
  background: linear-gradient(135deg, var(--eb-blue) 0%, #3665d6 100%);
  padding: 56px 0 40px;
  color: #fff;
  text-align: center;
}
.ebv-hero-title {
  font-size: clamp(1.75rem, 4vw, 2.75rem);
  font-weight: 800;
  margin-bottom: 8px;
}
.ebv-hero-subtitle {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 24px;
}
.ebv-hero-search {
  display: flex;
  max-width: 760px;
  margin: 0 auto;
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  box-shadow: var(--bf-shadow-xl);
}
.ebv-hero-search-cat {
  border: none;
  background: #eef2f7;
  color: #1f2937;
  padding: 0 12px;
  font-size: 0.85rem;
  font-weight: 600;
  max-width: 160px;
}
.ebv-hero-search-input {
  flex: 1;
  border: none;
  padding: 14px 16px;
  font-size: 1rem;
  color: #1f2937;
}
.ebv-hero-search-input:focus {
  outline: none;
}
.ebv-hero-search-btn {
  border: none;
  background: var(--eb-yellow);
  color: #1f2937;
  font-weight: 800;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}
.ebv-hero-search-btn:hover {
  background: #e09e00;
}
.ebv-hero-trending {
  margin-top: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.ebv-hero-trending-label {
  color: rgba(255, 255, 255, 0.75);
  font-size: 0.8rem;
}
.ebv-hero-trending-chip {
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  padding: 4px 12px;
  border-radius: var(--bf-radius-full);
  font-size: 0.78rem;
  cursor: pointer;
  transition: all 0.2s ease;
}
.ebv-hero-trending-chip:hover {
  background: #fff;
  color: var(--eb-blue);
}

/* ─── Section headers ─── */
.ebv-section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 8px;
}
.ebv-section-title {
  font-size: 1.5rem;
  font-weight: 800;
  margin: 0;
}
.ebv-section-subtitle {
  color: var(--bf-text-muted);
  font-size: 0.875rem;
  margin: 4px 0 0;
}
.ebv-see-all {
  font-weight: 700;
  color: var(--eb-blue);
  text-decoration: none;
}

/* ─── Category tiles ─── */
.ebv-category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 14px;
}
.ebv-category-tile {
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 18px 10px;
  text-align: center;
  cursor: pointer;
  transition: all var(--bf-transition-fast);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-size: 0.8rem;
  font-weight: 600;
}
.ebv-category-tile:hover {
  border-color: var(--eb-blue);
  box-shadow: var(--bf-shadow-md);
  transform: translateY(-3px);
}
.ebv-category-tile-icon {
  font-size: 1.5rem;
}
.ebv-category-tile-all {
  background: var(--bf-primary-light);
  color: var(--bf-primary);
}

/* ─── Stats strip ─── */
.ebv-stats-strip {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 14px;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 20px;
}
.ebv-stat {
  display: flex;
  align-items: center;
  gap: 12px;
}
.ebv-stat-icon {
  font-size: 1.75rem;
}
.ebv-stat strong {
  display: block;
  font-size: 1.1rem;
}
.ebv-stat small {
  color: var(--bf-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  font-size: 0.65rem;
}

/* ─── Testimonials ─── */
.ebv-testimonial-card {
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 20px;
  height: 100%;
}
.ebv-testimonial-stars {
  color: var(--eb-yellow);
  letter-spacing: 2px;
  margin-bottom: 10px;
}
.ebv-testimonial-card p {
  color: var(--bf-text-secondary);
  font-size: 0.9rem;
  font-style: italic;
}
.ebv-testimonial-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid var(--bf-border-light);
}
.ebv-testimonial-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--eb-blue);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}
.ebv-testimonial-author strong {
  display: block;
  font-size: 0.85rem;
}
.ebv-testimonial-author small {
  color: var(--bf-text-muted);
}

/* ─── Newsletter ─── */
.ebv-newsletter {
  background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  color: #fff;
  border-radius: var(--bf-radius-xl);
  padding: 40px;
  text-align: center;
}
.ebv-newsletter h3 {
  font-weight: 800;
  margin-bottom: 8px;
}
.ebv-newsletter p {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
}
.ebv-newsletter-form {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}
.ebv-newsletter-form input {
  padding: 12px 16px;
  border-radius: var(--bf-radius-md);
  border: none;
  min-width: 280px;
}
.ebv-newsletter-form button {
  padding: 12px 24px;
  border-radius: var(--bf-radius-md);
  border: none;
  background: var(--eb-yellow);
  color: #1f2937;
  font-weight: 800;
  cursor: pointer;
}

/* ─── Catalog toolbar ─── */
.ebv-catalog-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 12px 16px;
}
.ebv-catalog-search {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 220px;
  background: var(--bf-bg-secondary);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-md);
  padding: 8px 12px;
  color: var(--bf-text-muted);
}
.ebv-catalog-search input {
  border: none;
  background: transparent;
  flex: 1;
  color: var(--bf-text-primary);
  font-size: 0.9rem;
}
.ebv-catalog-search input:focus {
  outline: none;
}
.ebv-catalog-toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.ebv-results-count {
  font-size: 0.8rem;
  color: var(--bf-text-muted);
  white-space: nowrap;
}
.ebv-sort-select {
  border: 1px solid var(--bf-border);
  background: var(--bf-bg-secondary);
  color: var(--bf-text-primary);
  border-radius: var(--bf-radius-md);
  padding: 6px 10px;
  font-size: 0.8rem;
}
.ebv-view-toggle {
  display: flex;
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-md);
  overflow: hidden;
}
.ebv-view-toggle button {
  border: none;
  background: var(--bf-bg-secondary);
  color: var(--bf-text-muted);
  padding: 6px 12px;
  cursor: pointer;
}
.ebv-view-toggle button.active {
  background: var(--eb-blue);
  color: #fff;
}

/* ─── Filter rail ─── */
.ebv-filter-rail {
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 20px;
  position: sticky;
  top: 100px;
}
.ebv-filter-rail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--bf-border-light);
}
.ebv-filter-rail-header h5 {
  margin: 0;
  font-size: 1rem;
  font-weight: 800;
}
.ebv-reset-link {
  border: none;
  background: none;
  color: var(--eb-blue);
  font-size: 0.78rem;
  font-weight: 700;
  cursor: pointer;
}
.ebv-filter-group {
  margin-bottom: 20px;
}
.ebv-filter-group h6 {
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--bf-text-muted);
  font-weight: 700;
  margin-bottom: 10px;
}
.ebv-filter-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 220px;
  overflow-y: auto;
}
.ebv-filter-radio {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  color: var(--bf-text-secondary);
  cursor: pointer;
  padding: 4px 0;
}
.ebv-filter-radio.active {
  color: var(--eb-blue);
  font-weight: 700;
}
.ebv-price-slider {
  width: 100%;
}
.ebv-price-range-labels {
  display: flex;
  justify-content: space-between;
  font-size: 0.7rem;
  color: var(--bf-text-muted);
  margin-top: 4px;
}
.ebv-filter-select {
  width: 100%;
  border: 1px solid var(--bf-border);
  background: var(--bf-bg-secondary);
  color: var(--bf-text-primary);
  border-radius: var(--bf-radius-md);
  padding: 8px 10px;
  font-size: 0.85rem;
}
.ebv-filter-switch {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  cursor: pointer;
}

/* ─── Empty / error state ─── */
.ebv-empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--bf-text-muted);
}
.ebv-empty-icon {
  font-size: 2.5rem;
  margin-bottom: 12px;
}
.ebv-btn-primary {
  display: inline-block;
  margin-top: 12px;
  padding: 10px 20px;
  border-radius: var(--bf-radius-md);
  background: var(--eb-blue);
  color: #fff;
  font-weight: 700;
  text-decoration: none;
  border: none;
  cursor: pointer;
}

/* ─── List view rows ─── */
.ebv-list-row {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--bf-bg-card);
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 14px;
  flex-wrap: wrap;
  transition: all var(--bf-transition-fast);
}
.ebv-list-row:hover {
  border-color: var(--eb-blue);
  box-shadow: var(--bf-shadow-md);
}
.ebv-list-row-img {
  width: 90px;
  height: 90px;
  object-fit: cover;
  border-radius: var(--bf-radius-md);
  background: var(--bf-bg-tertiary);
  flex-shrink: 0;
}
.ebv-list-row-body {
  flex: 1;
  min-width: 200px;
}
.ebv-list-row-cat {
  font-size: 0.7rem;
  text-transform: uppercase;
  color: var(--eb-blue);
  font-weight: 700;
}
.ebv-list-row-body h5 {
  margin: 4px 0;
  font-weight: 700;
}
.ebv-list-row-body p {
  color: var(--bf-text-muted);
  font-size: 0.85rem;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.ebv-list-row-seller {
  font-size: 0.75rem;
  color: var(--bf-text-secondary);
  margin-top: 6px;
}
.ebv-verified-dot {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: var(--eb-blue);
  color: #fff;
  font-size: 0.55rem;
  margin-left: 4px;
}
.ebv-list-row-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
  min-width: 140px;
}
.ebv-list-row-price {
  font-size: 1.2rem;
  font-weight: 800;
}

/* ─── Pagination ─── */
.ebv-pagination {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 40px;
  flex-wrap: wrap;
}
.ebv-pagination button {
  border: 1px solid var(--bf-border);
  background: var(--bf-bg-card);
  color: var(--bf-text-secondary);
  border-radius: var(--bf-radius-md);
  padding: 8px 14px;
  cursor: pointer;
  font-size: 0.85rem;
}
.ebv-pagination button.active {
  background: var(--eb-blue);
  border-color: var(--eb-blue);
  color: #fff;
  font-weight: 700;
}
.ebv-pagination button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ─── Responsive ─── */
@media (max-width: 768px) {
  .ebv-hero-search {
    flex-direction: column;
    border-radius: var(--bf-radius-md);
  }
  .ebv-hero-search-cat {
    max-width: none;
    padding: 10px 12px;
  }
  .ebv-catalog-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  .ebv-catalog-toolbar-right {
    justify-content: space-between;
  }
  .ebv-filter-rail {
    position: static;
  }
  .ebv-list-row-actions {
    align-items: flex-start;
    flex-direction: row;
    width: 100%;
    justify-content: space-between;
  }
}
</style>
