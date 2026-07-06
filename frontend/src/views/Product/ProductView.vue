<template>
  <div class="container my-4">
    <!-- Split Banner Section (eBay Style Promo Grid) -->
    <div class="row g-4 mb-4">
      <!-- Main Dynamic Slideshow (Col 8) -->
      <div class="col-lg-8">
        <div class="card border-0 rounded-4 overflow-hidden h-100 shadow-sm text-white position-relative text-start p-4 p-md-5 d-flex flex-column justify-content-between" style="min-height: 380px; background: linear-gradient(135deg, rgba(30, 27, 75, 0.85) 0%, rgba(15, 23, 42, 0.95) 100%), url('https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=1200&auto=format&fit=crop') no-repeat center/cover;">
          <!-- Dynamic Slides -->
          <div 
            v-if="slides && slides.length > 0"
            class="h-100 d-flex flex-column justify-content-between z-index-2 position-relative"
          >
            <div>
              <span class="badge bg-warning text-dark px-3 py-2 rounded-pill font-weight-bold mb-3 text-uppercase">
                {{ slides[activeSlide].badge }}
              </span>
              <h1 class="display-5 font-weight-bold mb-2">{{ slides[activeSlide].title }}</h1>
              <p class="lead mb-4 opacity-75">{{ slides[activeSlide].subtitle }}</p>
            </div>
            <div>
              <button class="btn btn-warning btn-lg font-weight-bold px-4 py-2 text-dark shadow" @click="scrollToProducts">
                Shop Now
              </button>
            </div>
          </div>
          
          <!-- Slide indicator dots -->
          <div class="position-absolute bottom-3 start-50 translate-middle-x z-index-2 d-flex gap-2 mb-3">
            <span 
              v-for="(slide, index) in slides" 
              :key="index" 
              class="slide-dot cursor-pointer" 
              :class="{'active-dot bg-warning': activeSlide === index, 'bg-light opacity-50': activeSlide !== index}" 
              @click="activeSlide = index"
            ></span>
          </div>

          <!-- Decorative background grid -->
          <div class="hero-pattern position-absolute top-50 start-50 translate-middle w-100 h-100 opacity-10" style="background-image: radial-gradient(circle, #ffffff 1px, transparent 1px); background-size: 20px 20px;"></div>
        </div>
      </div>

      <!-- Side Deal Banners (Col 4) -->
      <div class="col-lg-4 d-flex flex-column gap-3">
        <!-- Prebuilt Desktops Promo Card -->
        <div class="card border-0 rounded-4 overflow-hidden flex-fill text-white p-4 shadow-sm text-start position-relative d-flex flex-column justify-content-center" style="background: linear-gradient(135deg, rgba(9, 9, 11, 0.8) 0%, rgba(39, 39, 42, 0.9) 100%), url('https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=600&auto=format&fit=crop') no-repeat center/cover; min-height: 180px;">
          <div>
            <span class="badge bg-primary text-white px-2 py-1 rounded font-weight-bold mb-2 small uppercase-badge">
              Featured Build
            </span>
            <h5 class="font-weight-bold mb-1">Prebuilt Gaming PCs</h5>
            <p class="small text-white-50 mb-3">High-fps liquid-cooled rigs benchmarked and ready to play.</p>
          </div>
          <button class="btn btn-outline-light btn-sm font-weight-bold align-self-start" @click="selectCategoryByName('Desktop Systems')">
            Explore Systems
          </button>
        </div>

        <!-- GPU Restock Promo Card -->
        <div class="card border-0 rounded-4 overflow-hidden flex-fill text-white p-4 shadow-sm text-start position-relative d-flex flex-column justify-content-center" style="background: linear-gradient(135deg, rgba(12, 74, 110, 0.8) 0%, rgba(3, 105, 161, 0.9) 100%), url('https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=600&auto=format&fit=crop') no-repeat center/cover; min-height: 180px;">
          <div>
            <span class="badge bg-warning text-dark px-2 py-1 rounded font-weight-bold mb-2 small uppercase-badge">
              Hardware Drop
            </span>
            <h5 class="font-weight-bold mb-1">RTX 40-Series In Stock</h5>
            <p class="small text-white-50 mb-3">Get unmatched ray-tracing output and DLSS 3 support.</p>
          </div>
          <button class="btn btn-outline-warning btn-sm font-weight-bold align-self-start text-warning" @click="selectCategoryByName('Graphics Cards (GPUs)')">
            Shop GPUs
          </button>
        </div>
      </div>
    </div>

    <!-- Shop By Category (eBay Style Circular Navigation) -->
    <div class="mb-5 text-start bg-light p-4 rounded-4 shadow-sm">
      <h4 class="font-weight-bold text-dark mb-4">Shop by Category</h4>
      <!-- Horizontal scroll circle row -->
      <div class="d-flex gap-4 overflow-auto pb-2 custom-horizontal-scroll">
        <!-- "All Categories" circle -->
        <div class="category-circle-item text-center cursor-pointer flex-shrink-0" @click="selectCategory('all')">
          <div 
            class="circle-image-wrapper mx-auto mb-2 shadow-sm rounded-circle d-flex align-items-center justify-content-center border bg-white" 
            :class="{'active-circle border-primary border-3': selectedCategoryId === 'all'}" 
            style="width: 80px; height: 80px;"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-grid-fill text-primary" viewBox="0 0 16 16">
              <path d="M1 2.5A1.5 1.5 0 0 1 2.5 1h3A1.5 1.5 0 0 1 7 2.5v3A1.5 1.5 0 0 1 5.5 7h-3A1.5 1.5 0 0 1 1 5.5zm8 0A1.5 1.5 0 0 1 10.5 1h3A1.5 1.5 0 0 1 15 2.5v3A1.5 1.5 0 0 1 13.5 7h-3A1.5 1.5 0 0 1 9 5.5zm-8 8A1.5 1.5 0 0 1 2.5 9h-3A1.5 1.5 0 0 1 5.5 11v3A1.5 1.5 0 0 1 3.5 15h-3A1.5 1.5 0 0 1 1 13.5zm8 0A1.5 1.5 0 0 1 10.5 9h3a1.5 1.5 0 0 1 1.5 1.5v3a1.5 1.5 0 0 1-1.5 1.5h-3a1.5 1.5 0 0 1-1.5-1.5z"/>
            </svg>
          </div>
          <small class="font-weight-bold text-dark d-block">All Gears</small>
        </div>

        <!-- Dynamic Category circles -->
        <div 
          v-for="cat in categories" 
          :key="cat.categoryId" 
          class="category-circle-item text-center cursor-pointer flex-shrink-0"
          @click="selectCategory(cat.categoryId)"
        >
          <div 
            class="circle-image-wrapper mx-auto mb-2 shadow-sm rounded-circle border overflow-hidden" 
            :class="{'active-circle border-primary border-3': selectedCategoryId === cat.categoryId}"
            style="width: 80px; height: 80px;"
          >
            <img :src="cat.imageUrl" class="w-100 h-100" style="object-fit: cover;" alt="Category Thumbnail">
          </div>
          <small class="font-weight-bold text-dark d-block text-truncate" style="max-width: 90px;" :title="cat.categoryName">
            {{ cat.categoryName }}
          </small>
        </div>
      </div>
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
              placeholder="Search components or accessories..." 
              v-model="searchQuery"
            />
          </div>
        </div>

        <!-- Spacer -->
        <div class="col-md-5"></div>

        <!-- Sorting dropdown -->
        <div class="col-md-3 text-end">
          <div class="d-flex align-items-center justify-content-end gap-2">
            <span class="text-muted small text-nowrap">Sort by:</span>
            <select class="form-select bg-light border" v-model="sortOrder" style="max-width: 180px;">
              <option value="default">Relevance</option>
              <option value="name-asc">Name (A-Z)</option>
              <option value="price-asc">Price (Low to High)</option>
              <option value="price-desc">Price (High to Low)</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading spinner -->
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
.custom-horizontal-scroll {
  white-space: nowrap;
}
.category-circle-item:hover .circle-image-wrapper {
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}
.slide-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  transition: all 0.2s ease;
}
.active-dot {
  width: 25px;
  border-radius: 6px;
}
.circle-image-wrapper {
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 3px;
}
.active-circle {
  border-color: #0d6efd !important;
}
.uppercase-badge {
  font-size: 0.7rem;
  letter-spacing: 1px;
}
</style>
