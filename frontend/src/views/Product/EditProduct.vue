<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-10">
        <div class="card shadow-sm border-0">
          <div class="card-body p-4 p-md-5">
            <h3 class="mb-4 text-center font-weight-bold">Edit Product</h3>

            <!-- Loading Spinner -->
            <div v-if="loading" class="text-center my-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading product details...</span>
              </div>
            </div>

            <template v-else>
              <div v-if="error" class="alert alert-danger" role="alert">
                {{ error }}
              </div>

              <form @submit.prevent="handleSubmit">
                <div class="row">
                  <!-- Form Inputs Column -->
                  <div class="col-md-7 text-start">
                    <div class="form-group mb-3">
                      <label for="productName" class="form-label">Product Name</label>
                      <input 
                        type="text" 
                        id="productName" 
                        class="form-control" 
                        v-model="productName"
                        @blur="touched.productName = true"
                        required
                      />
                      <div v-if="touched.productName && !productName" class="text-danger mt-1 small">
                        Product Name is required.
                      </div>
                    </div>

                    <div class="form-group mb-3">
                      <label for="description" class="form-label">Description</label>
                      <textarea 
                        id="description" 
                        class="form-control" 
                        rows="3"
                        v-model="description"
                        @blur="touched.description = true"
                        required
                      ></textarea>
                      <div v-if="touched.description && !description" class="text-danger mt-1 small">
                        Description is required.
                      </div>
                    </div>

                    <div class="form-group mb-3">
                      <label for="imageUrl" class="form-label">Image URL</label>
                      <input 
                        type="text" 
                        id="imageUrl" 
                        class="form-control" 
                        v-model="imageUrl"
                        @blur="touched.imageUrl = true"
                        required
                      />
                      <div v-if="touched.imageUrl && !imageUrl" class="text-danger mt-1 small">
                        Image URL is required.
                      </div>
                    </div>

                    <div class="form-group mb-3">
                      <label for="price" class="form-label">Price (LKR)</label>
                      <input 
                        type="number" 
                        id="price" 
                        class="form-control" 
                        v-model.number="price"
                        step="0.01"
                        min="0.01"
                        @blur="touched.price = true"
                        required
                      />
                      <div v-if="touched.price && (price === null || price === undefined || price <= 0)" class="text-danger mt-1 small">
                        Price is required and must be greater than 0.
                      </div>
                    </div>

                    <div class="form-group mb-3">
                      <label for="stock" class="form-label">Stock Count</label>
                      <input 
                        type="number" 
                        id="stock" 
                        class="form-control" 
                        v-model.number="stock"
                        min="0"
                        @blur="touched.stock = true"
                        required
                      />
                      <div v-if="touched.stock && (stock === null || stock === undefined || stock < 0)" class="text-danger mt-1 small">
                        Stock Count is required and must be non-negative.
                      </div>
                    </div>

                    <div class="form-group mb-4">
                      <label for="categoryId" class="form-label">Category</label>
                      <select 
                        id="categoryId" 
                        class="form-select" 
                        v-model="categoryId"
                        @blur="touched.categoryId = true"
                        required
                      >
                        <option value="" disabled>Select a Category</option>
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

                    <div class="d-flex gap-3">
                      <button 
                        type="submit" 
                        class="btn btn-primary flex-grow-1 py-2" 
                        :disabled="!isValid || submitting"
                      >
                        <span v-if="submitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Save Changes
                      </button>
                      <button 
                        type="button" 
                        class="btn btn-danger py-2" 
                        @click="handleDelete"
                        :disabled="deleting"
                      >
                        <span v-if="deleting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Delete
                      </button>
                    </div>
                  </div>

                  <!-- Image Preview Column -->
                  <div class="col-md-5 d-flex flex-column align-items-center justify-content-center border-start ps-md-4 mt-4 mt-md-0">
                    <span class="text-secondary small font-weight-bold mb-2">Live Image Preview</span>
                    <div class="preview-container d-flex align-items-center justify-content-center bg-light border rounded" style="width: 100%; height: 250px; overflow: hidden;">
                      <img 
                        v-if="imageUrl" 
                        :src="imageUrl" 
                        @error="previewError = true" 
                        @load="previewError = false" 
                        v-show="!previewError"
                        alt="Product Preview" 
                        class="img-fluid"
                        style="max-height: 100%; object-fit: contain;"
                      />
                      <div v-if="!imageUrl" class="text-muted small text-center p-3">
                        Enter an Image URL to see preview here.
                      </div>
                      <div v-else-if="previewError" class="text-danger small text-center p-3">
                        Unable to load preview image.<br/>Please check the URL.
                      </div>
                    </div>
                  </div>
                </div>
              </form>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage } from '../../utils/api';

export default {
  name: 'EditProduct',
  data() {
    return {
      productId: null,
      productName: '',
      description: '',
      imageUrl: '',
      price: null,
      stock: 0,
      categoryId: '',
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
        this.stock !== null &&
        this.stock !== undefined &&
        this.stock >= 0 &&
        this.categoryId !== ''
      );
    }
  },
  methods: {
    async fetchCategories() {
      try {
        const response = await api.get('/category');
        this.categories = response.data.data;
      } catch (err) {
        console.error('Failed to load categories:', extractErrorMessage(err));
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
        this.price = p.price;
        this.stock = p.stock || 0;
        this.categoryId = p.categoryId;
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    },
    async handleSubmit() {
      if (!this.isValid) return;
      this.submitting = true;
      this.error = null;
      try {
        const payload = {
          productId: this.productId,
          productName: this.productName,
          description: this.description,
          imageUrl: this.imageUrl,
          price: this.price,
          stock: this.stock,
          categoryId: this.categoryId
        };
        const response = await api.put('/product', payload);
        alert(response.data.message || 'Product updated successfully!');
        this.$router.push({ name: 'ProductView' });
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
        const response = await api.delete(`/product/${this.productId}`);
        alert(response.data || 'Product deleted successfully!');
        this.$router.push({ name: 'ProductView' });
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
.card {
  border-radius: 12px;
}
</style>
