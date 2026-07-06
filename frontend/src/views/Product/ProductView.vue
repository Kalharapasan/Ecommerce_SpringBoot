<template>
  <div class="container my-4">
    <!-- Header Title -->
    <div class="row mb-4 align-items-center">
      <div class="col-md-6 text-start">
        <h3>Our Products</h3>
      </div>
      <div class="col-md-6 text-md-end mt-2 mt-md-0" v-if="isAdmin">
        <router-link :to="{ name: 'AddProduct' }">
          <button class="btn btn-primary">Add Product</button>
        </router-link>
      </div>
    </div>

    <!-- Hero Banner Section -->
    <div class="hero-section rounded-4 text-white p-5 mb-4 position-relative overflow-hidden shadow-lg border border-dark" style="background: linear-gradient(135deg, #1e1b4b 0%, #0f172a 100%);">
      <div class="position-relative z-index-2 text-start">
        <span class="badge bg-primary text-white px-3 py-2 rounded-pill font-weight-bold mb-3">GAMING GEAR & CUSTOM PC BUILDER</span>
        <h1 class="display-4 font-weight-bold mb-2">ByteForge PC Store</h1>
        <p class="lead mb-4 opacity-75">Build your dream setup with high-end GPUs, desktop hardware, custom cases, and peripherals. Use coupon <strong class="text-warning">SAVE50</strong> at checkout!</p>
        <button class="btn btn-warning btn-lg font-weight-bold px-4 py-2 text-dark shadow-sm" @click="scrollToProducts">Shop Now</button>
      </div>
      <div class="hero-pattern position-absolute top-50 start-50 translate-middle w-100 h-100 opacity-10" style="background-image: radial-gradient(circle, #ffffff 1px, transparent 1px); background-size: 20px 20px;"></div>
    </div>

    <!-- Highlight features row -->
    <div class="row g-4 mb-5 text-start">
      <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm p-3 bg-white rounded d-flex flex-row align-items-center gap-3">
          <div class="bg-light p-3 rounded-circle text-primary">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-truck" viewBox="0 0 16 16">
              <path d="M0 3.5A1.5 1.5 0 0 1 1.5 2h9A1.5 1.5 0 0 1 12 3.5V5h1.02a1.5 1.5 0 0 1 1.17.563l1.481 1.85a1.5 1.5 0 0 1 .329.938V10.5a1.5 1.5 0 0 1-1.5 1.5H14a2 2 0 1 1-4 0H5a2 2 0 1 1-3.998-.085A1.5 1.5 0 0 1 0 10.5zm1.294 7.456A2 2 0 0 1 4.737 11H9.27a2 2 0 0 1 3.877-.274H14.5a.5.5 0 0 0 .5-.5V8.35a.5.5 0 0 0-.11-.312l-1.48-1.85A.5.5 0 0 0 13.02 6H12v4.5h-1.258a2 2 0 0 1-3.518.266zM4 11a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm9 0a1 1 0 1 0 0 2 1 1 0 0 0 0-2z"/>
            </svg>
          </div>
          <div>
            <h6 class="font-weight-bold mb-0">Free Shipping</h6>
            <small class="text-muted">On all orders over LKR 10,000</small>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm p-3 bg-white rounded d-flex flex-row align-items-center gap-3">
          <div class="bg-light p-3 rounded-circle text-success">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-shield-check" viewBox="0 0 16 16">
              <path d="M5.338 1.59a6.1 6.1 0 0 0-2.837.856.48.48 0 0 0-.228.389v4.287c0 3.29 1.825 6.4 4.732 7.69a.48.48 0 0 0 .39 0c2.907-1.29 4.732-4.4 4.732-7.69V2.835a.48.48 0 0 0-.228-.389 6.1 6.1 0 0 0-2.837-.855 4.8 4.8 0 0 0-3.722 0"/>
              <path d="M10.854 5.854a.5.5 0 0 0-.708-.708L7.5 7.793 6.354 6.646a.5.5 0 1 0-.708.708l1.5 1.5a.5.5 0 0 0 .708 0z"/>
            </svg>
          </div>
          <div>
            <h6 class="font-weight-bold mb-0">Secure Payments</h6>
            <small class="text-muted">100% SSL protected payments</small>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100 border-0 shadow-sm p-3 bg-white rounded d-flex flex-row align-items-center gap-3">
          <div class="bg-light p-3 rounded-circle text-warning">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-headset" viewBox="0 0 16 16">
              <path d="M8 1a5 5 0 0 0-5 5v1h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V6a6 6 0 1 1 12 0v5a1 1 0 0 1-1 1h-1a1 1 0 0 1-1-1V8a1 1 0 0 1 1-1h1V6a5 5 0 0 0-5-5"/>
            </svg>
          </div>
          <div>
            <h6 class="font-weight-bold mb-0">24/7 Support</h6>
            <small class="text-muted">Dedicated customer helpline</small>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters & Search Toolbar -->
    <div id="products-section" class="card p-3 shadow-sm border-0 mb-4 bg-white rounded">
      <div class="row g-3 align-items-center">
        <!-- Search bar -->
        <div class="col-md-4 text-start">
          <div class="input-group">
            <span class="input-group-text bg-light border-end-0">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
                <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
              </svg>
            </span>
            <input 
              type="text" 
              class="form-control border-start-0 bg-light" 
              placeholder="Search products..." 
              v-model="searchQuery"
            />
          </div>
        </div>

        <!-- Category selector -->
        <div class="col-md-4 d-flex align-items-center gap-2 text-start">
          <label for="categoryFilter" class="form-label mb-0 text-nowrap text-secondary small font-weight-bold">Category:</label>
          <select 
            id="categoryFilter" 
            class="form-select" 
            v-model="selectedCategoryId" 
            @change="handleCategoryChange"
          >
            <option value="all">All Products</option>
            <option 
              v-for="cat in categories" 
              :key="cat.categoryId" 
              :value="cat.categoryId"
            >
              {{ cat.categoryName }}
            </option>
          </select>
        </div>

        <!-- Sorting selector -->
        <div class="col-md-4 d-flex align-items-center gap-2 text-start">
          <label for="sortOrder" class="form-label mb-0 text-nowrap text-secondary small font-weight-bold">Sort By:</label>
          <select 
            id="sortOrder" 
            class="form-select" 
            v-model="sortOrder"
          >
            <option value="default">Default</option>
            <option value="name-asc">Name (A-Z)</option>
            <option value="price-asc">Price (Low to High)</option>
            <option value="price-desc">Price (High to Low)</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Loading Spinner -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading products...</span>
      </div>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="alert alert-danger text-start" role="alert">
      {{ error }}
    </div>

    <!-- Empty State -->
    <div v-else-if="processedProducts.length === 0" class="text-center my-5 py-5 bg-white rounded shadow-sm border border-light">
      <h5 class="text-muted mb-2">No products match your criteria.</h5>
      <p class="text-secondary small mb-0">Try adjusting your search filters or browse another category.</p>
    </div>

    <!-- Products Grid -->
    <div v-else class="row">
      <div 
        v-for="product in processedProducts" 
        :key="product.productId" 
        class="col-xl-3 col-md-6 d-flex mb-4"
      >
        <ProductBox :product="product" />
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage } from '../../utils/api';
import ProductBox from '../../components/Product/ProductBox.vue';
import { getCurrentUser } from '../../utils/auth';

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
      error: null
    };
  },
  computed: {
    isAdmin() {
      const user = getCurrentUser();
      return user && user.role === 'ADMIN';
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
    },
    handleCategoryChange() {
      this.fetchProducts();
    }
  },
  mounted() {
    this.fetchCategories();
    this.fetchProducts();
  }
};
</script>

<style scoped>
.hero-section {
  background-size: cover;
}
</style>
