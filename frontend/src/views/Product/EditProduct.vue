<template>
  <div class="bf-page bf-fade-in p-4 text-start">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-primary mb-2">Catalogue Maintenance</span>
          <h2 class="text-white font-weight-bold mb-1">Modify Hardware</h2>
          <p class="text-muted small">Update pricing, description, stock bounds, or image profiles of listed components.</p>
        </div>
      </div>

      <!-- Loading Spinner -->
      <div v-if="loading" class="text-center py-5">
        <span class="spinner-border spinner-border-sm text-primary mb-2"></span>
        <p class="text-muted small">Synchronizing item configurations...</p>
      </div>

      <div v-else class="row text-white g-4">
        <div class="col-lg-8">
          <div class="bf-card bg-dark border-light p-4 text-start">
            <div v-if="error" class="alert alert-danger" role="alert">
              {{ error }}
            </div>

            <form @submit.prevent="handleSubmit">
              <div class="row g-3">
                <div class="col-12">
                  <label class="form-label small text-muted font-weight-bold">Product Name</label>
                  <input 
                    type="text" 
                    class="bf-input text-white bg-dark border-light" 
                    v-model="productName"
                    @blur="touched.productName = true"
                    required
                  />
                  <div v-if="touched.productName && !productName" class="text-danger mt-1 small">
                    Product Name is required.
                  </div>
                </div>

                <div class="col-12">
                  <label class="form-label small text-muted font-weight-bold">Description</label>
                  <textarea 
                    class="bf-input text-white bg-dark border-light" 
                    rows="4"
                    v-model="description"
                    @blur="touched.description = true"
                    required
                  ></textarea>
                  <div v-if="touched.description && !description" class="text-danger mt-1 small">
                    Description is required.
                  </div>
                </div>

                <div class="col-md-6">
                  <label class="form-label small text-muted font-weight-bold d-block">Image Source</label>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="srcUrlEdit" value="url" v-model="imageSource">
                    <label class="form-check-label text-white small" for="srcUrlEdit">URL Link</label>
                  </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="srcUploadEdit" value="upload" v-model="imageSource">
                    <label class="form-check-label text-white small" for="srcUploadEdit">Upload Local File</label>
                  </div>
                </div>

                <div class="col-md-6" v-if="imageSource === 'url'">
                  <label class="form-label small text-muted font-weight-bold">Image URL</label>
                  <input 
                    type="text" 
                    class="bf-input text-white bg-dark border-light" 
                    v-model="imageUrl"
                    @blur="touched.imageUrl = true"
                    required
                  />
                  <div v-if="touched.imageUrl && !imageUrl" class="text-danger mt-1 small">
                    Image URL is required.
                  </div>
                </div>
                <div class="col-md-6" v-else>
                  <label class="form-label small text-muted font-weight-bold">Upload Image File</label>
                  <input 
                    type="file" 
                    class="bf-input text-white bg-dark border-light py-1" 
                    @change="onFileChange"
                    accept="image/*"
                  />
                  <div v-if="uploading" class="text-muted small mt-1">
                    <span class="spinner-border spinner-border-sm text-primary me-1"></span>
                    Uploading image...
                  </div>
                  <div v-if="touched.imageUrl && !imageUrl && !uploading" class="text-danger mt-1 small">
                    Please upload an image file.
                  </div>
                </div>

                <div class="col-md-6">
                  <label class="form-label small text-muted font-weight-bold">Item Condition</label>
                  <select class="bf-input text-white bg-dark border-light" v-model="conditionType" required>
                    <option value="Brand New">Brand New (Sealed)</option>
                    <option value="Refurbished">Certified Refurbished</option>
                    <option value="Used - Like New">Used - Like New</option>
                    <option value="Used - Good">Used - Good (Tested)</option>
                    <option value="For Parts">For Parts or Not Working</option>
                  </select>
                </div>

                <div class="col-md-6 d-flex align-items-center mt-4">
                  <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" id="isAuctionSwitch" v-model="isAuction" @change="onAuctionToggle">
                    <label class="form-check-label text-white small" for="isAuctionSwitch">List as Auction (Bidding)</label>
                  </div>
                </div>

                <div class="col-md-6">
                  <label class="form-label small text-muted font-weight-bold">
                    {{ isAuction ? 'Starting Bid Price (LKR)' : 'Price Rate (LKR)' }}
                  </label>
                  <input 
                    type="number" 
                    class="bf-input text-white bg-dark border-light" 
                    v-model.number="price"
                    step="0.01"
                    min="0.01"
                    @blur="touched.price = true"
                    required
                  />
                  <div v-if="touched.price && (price === null || price === undefined || price <= 0)" class="text-danger mt-1 small">
                    Price/Bid is required and must be greater than 0.
                  </div>
                </div>

                <div class="col-md-6" v-if="!isAuction">
                  <label class="form-label small text-muted font-weight-bold">Stock Quantity</label>
                  <input 
                    type="number" 
                    class="bf-input text-white bg-dark border-light" 
                    v-model.number="stock"
                    min="0"
                    @blur="touched.stock = true"
                    required
                  />
                  <div v-if="touched.stock && (stock === null || stock === undefined || stock < 0)" class="text-danger mt-1 small">
                    Stock count is required and must be non-negative.
                  </div>
                </div>
                <div class="col-md-6" v-else>
                  <label class="form-label small text-muted font-weight-bold">Auction Duration (Days)</label>
                  <select class="bf-input text-white bg-dark border-light" v-model="auctionDuration">
                    <option :value="1">1 Day</option>
                    <option :value="3">3 Days</option>
                    <option :value="5">5 Days</option>
                    <option :value="7">7 Days (Default)</option>
                    <option :value="10">10 Days</option>
                  </select>
                </div>

                <div class="col-12">
                  <label class="form-label small text-muted font-weight-bold">Category Grouping</label>
                  <select 
                    class="bf-input text-white bg-dark border-light" 
                    v-model="categoryId"
                    @blur="touched.categoryId = true"
                    required
                  >
                    <option value="" disabled>Select hardware category...</option>
                    <option 
                      v-for="cat in categories" 
                      :key="cat.categoryId" 
                      :value="cat.categoryId"
                    >
                      {{ cat.categoryName }}
                    </option>
                  </select>
                  <div v-if="touched.categoryId && !categoryId" class="text-danger mt-1 small">
                    Category selection is required.
                  </div>
                </div>

                <div class="col-12 mt-4">
                  <div class="d-flex gap-3">
                    <button 
                      type="submit" 
                      class="bf-btn bf-btn-primary flex-grow-1 py-3 shadow-glow" 
                      :disabled="!isValid || submitting"
                    >
                      <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                      Save Changes
                    </button>
                    <button 
                      type="button" 
                      class="bf-btn bf-btn-danger px-4" 
                      @click="handleDelete"
                      :disabled="deleting"
                    >
                      <span v-if="deleting" class="spinner-border spinner-border-sm me-2"></span>
                      Delete Item
                    </button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>

        <!-- Left Column: Hologram Preview -->
        <div class="col-lg-4">
          <div class="bf-card bg-dark border-light p-4 text-center h-100 d-flex flex-column align-items-center justify-content-center">
            <span class="text-muted small font-weight-bold mb-3 uppercase tracking-wider">Hologram Image Deck</span>
            <div class="preview-deck border rounded d-flex align-items-center justify-content-center bg-secondary-dark" style="width: 100%; height: 260px; overflow: hidden; position: relative;">
              <div class="deck-glow"></div>
              <img 
                v-if="imageUrl" 
                :src="imageUrl" 
                @error="previewError = true" 
                @load="previewError = false" 
                v-show="!previewError"
                alt="Product Preview" 
                class="img-fluid position-relative"
                style="max-height: 100%; object-fit: contain; z-index: 2;"
              />
              <div v-if="!imageUrl" class="text-muted small text-center p-3 z-index-top">
                Enter an Image URL or upload a file to preview it here.
              </div>
              <div v-else-if="previewError" class="text-danger small text-center p-3 z-index-top">
                Unable to load preview image.<br/>Please check the URL.
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
import { getCurrentUser } from '../../utils/auth';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'EditProduct',
  data() {
    return {
      imageSource: 'url',
      uploading: false,
      productId: null,
      productName: '',
      description: '',
      imageUrl: '',
      price: null,
      stock: 0,
      conditionType: 'Brand New',
      isAuction: false,
      auctionDuration: 7,
      categoryId: '',
      storeId: null,
      categories: [],
      error: null,
      loading: true,
      submitting: false,
      deleting: false,
      previewError: false,
      touched: {
        productName: false,
        description: false,
        imageUrl: false,
        price: false,
        stock: false,
        categoryId: false
      }
    };
  },
  watch: {
    imageUrl() {
      this.previewError = false;
    }
  },
  computed: {
    isValid() {
      return (
        this.productName.trim() !== '' &&
        this.description.trim() !== '' &&
        this.imageUrl.trim() !== '' &&
        this.price !== null &&
        this.price !== undefined &&
        this.price > 0 &&
        (this.isAuction || (this.stock !== null && this.stock !== undefined && this.stock >= 0)) &&
        this.categoryId !== ''
      );
    }
  },
  methods: {
    async fetchCategories() {
      try {
        const response = await api.get('/category');
        this.categories = response.data.data || [];
      } catch (err) {
        console.error('Failed to load categories:', extractErrorMessage(err));
      }
    },
    async onFileChange(e) {
      const file = e.target.files[0];
      if (!file) return;

      this.uploading = true;
      this.error = null;
      const formData = new FormData();
      formData.append('file', file);

      try {
        const response = await api.post('/file/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        this.imageUrl = response.data.data;
        this.touched.imageUrl = true;
      } catch (err) {
        this.error = 'Failed to upload image: ' + extractErrorMessage(err);
      } finally {
        this.uploading = false;
      }
    },
    async fetchProduct() {
      this.loading = true;
      this.error = null;
      try {
        const id = this.$route.params.id;
        const response = await api.get(`/product/${id}`);
        const p = response.data.data;
        this.productId = p.productId;
        this.productName = p.productName;
        this.description = p.description;
        this.imageUrl = p.imageUrl;
        if (p.imageUrl && p.imageUrl.includes('/uploads/')) {
          this.imageSource = 'upload';
        } else {
          this.imageSource = 'url';
        }
        this.price = p.price;
        this.stock = p.stock || 0;
        this.categoryId = p.categoryId;
        this.storeId = p.storeId;
        this.isAuction = !!p.isAuction;
        this.conditionType = p.conditionType || 'Brand New';
        if (p.auctionEndDate) {
          // Approximate remaining days or keep default duration
          const diffTime = new Date(p.auctionEndDate) - new Date();
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          this.auctionDuration = diffDays > 0 ? diffDays : 7;
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    },
    onAuctionToggle() {
      if (this.isAuction) {
        this.stock = 1;
      }
    },
    async handleSubmit() {
      if (!this.isValid) return;
      this.submitting = true;
      this.error = null;
      try {
        let auctionEndDate = null;
        if (this.isAuction) {
          const endDate = new Date();
          endDate.setDate(endDate.getDate() + this.auctionDuration);
          auctionEndDate = endDate.toISOString();
        }
        const payload = {
          productId: this.productId,
          productName: this.productName,
          description: this.description,
          imageUrl: this.imageUrl,
          price: this.price,
          stock: this.isAuction ? 1 : this.stock,
          categoryId: this.categoryId,
          storeId: this.storeId,
          isAuction: this.isAuction,
          conditionType: this.conditionType,
          auctionEndDate: auctionEndDate
        };
        const response = await api.put('/product', payload);
        showToast({
          message: response.data.message || 'Product updated successfully!',
          type: 'success',
          title: 'Component Updated'
        });
        const user = getCurrentUser();
        if (user && user.role === 'SELLER') {
          this.$router.push('/seller/products');
        } else {
          this.$router.push('/admin/dashboard');
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.submitting = false;
      }
    },
    async handleDelete() {
      if (!confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
        return;
      }
      this.deleting = true;
      this.error = null;
      try {
        await api.delete(`/product/${this.productId}`);
        showToast({
          message: 'Product deleted successfully!',
          type: 'success',
          title: 'Component Deleted'
        });
        const user = getCurrentUser();
        if (user && user.role === 'SELLER') {
          this.$router.push('/seller/products');
        } else {
          this.$router.push('/admin/dashboard');
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.deleting = false;
      }
    }
  },
  async mounted() {
    await this.fetchCategories();
    await this.fetchProduct();
  }
};
</script>

<style scoped>
.bg-secondary-dark {
  background: rgba(255, 255, 255, 0.03);
}

.preview-deck {
  border-color: rgba(255, 255, 255, 0.08) !important;
}

.deck-glow {
  position: absolute;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.12) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  pointer-events: none;
}

.z-index-top {
  z-index: 3;
}
</style>
