<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow-sm border-0">
          <div class="card-body p-4 p-md-5 text-start">
            <h3 class="mb-4 text-center font-weight-bold">Edit Category</h3>

            <div v-if="loading" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading category...</span>
              </div>
            </div>

            <div v-else-if="error" class="alert alert-danger" role="alert">
              {{ error }}
            </div>

            <form v-else @submit.prevent="updateCategory">
              <div class="form-group mb-3">
                <label for="categoryName" class="form-label font-weight-bold">Category Name</label>
                <input type="text" id="categoryName" class="form-control" v-model="categoryName" required />
              </div>

              <div class="form-group mb-3">
                <label for="description" class="form-label font-weight-bold">Description</label>
                <textarea id="description" class="form-control" rows="3" v-model="description" required></textarea>
              </div>

              <!-- Image selector options -->
              <div class="form-group mb-3">
                <label class="form-label d-block font-weight-bold">Image Source</label>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" id="srcUrl" value="url" v-model="imageSource" />
                  <label class="form-check-label" for="srcUrl">URL</label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" id="srcUpload" value="upload" v-model="imageSource" />
                  <label class="form-check-label" for="srcUpload">Upload File</label>
                </div>
              </div>

              <div v-if="imageSource === 'url'" class="form-group mb-3">
                <label for="imageUrl" class="form-label font-weight-bold">Image URL</label>
                <input type="text" id="imageUrl" class="form-control" v-model="imageUrl" required />
              </div>
              <div v-else class="form-group mb-3">
                <label for="imageFile" class="form-label font-weight-bold">Upload Image</label>
                <input type="file" id="imageFile" class="form-control" @change="onFileChange" accept="image/*" />
                <div v-if="uploading" class="text-muted small mt-1">
                  <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                  Uploading image...
                </div>
              </div>

              <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-primary flex-grow-1 py-2 font-weight-bold" :disabled="uploading">
                  Save Changes
                </button>
                <button type="button" class="btn btn-outline-secondary px-4" @click="$router.push({ name: 'AdminDashboard' })">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage } from '../../utils/api';

export default {
  name: 'EditCategory',
  data() {
    return {
      categoryId: null,
      categoryName: '',
      description: '',
      imageUrl: '',
      imageSource: 'url',
      uploading: false,
      loading: true,
      error: null
    };
  },
  methods: {
    async fetchCategory() {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.get(`/category/${this.categoryId}`);
        const category = response.data.data;
        if (category) {
          this.categoryName = category.categoryName;
          this.description = category.description;
          this.imageUrl = category.imageUrl;
        } else {
          this.error = 'Category not found';
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
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
        this.imageUrl = response.data; // Save the raw text URL returned from backend
      } catch (err) {
        this.error = 'Failed to upload image: ' + extractErrorMessage(err);
      } finally {
        this.uploading = false;
      }
    },
    async updateCategory() {
      if (!this.imageUrl) {
        alert('Please specify an image URL or upload a file.');
        return;
      }

      this.error = null;
      const updatedCategory = {
        categoryId: this.categoryId,
        categoryName: this.categoryName,
        description: this.description,
        imageUrl: this.imageUrl
      };

      try {
        await api.put('/category', updatedCategory);
        alert('Category updated successfully!');
        this.$router.push({ name: 'AdminDashboard' });
      } catch (err) {
        this.error = extractErrorMessage(err);
      }
    }
  },
  mounted() {
    this.categoryId = this.$route.params.id;
    if (this.categoryId) {
      this.fetchCategory();
    } else {
      this.error = 'Category ID is missing.';
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.card {
  border-radius: 12px;
}
</style>
