<template>
    <div class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow-sm border-0">
                    <div class="card-body p-4 p-md-5">
                        <h3 class="mb-4 text-center font-weight-bold">Add Category</h3>

                        <div v-if="error" class="alert alert-danger text-start" role="alert">
                            {{ error }}
                        </div>

                        <form @submit.prevent="addCategory">
                            <div class="form-group mb-3 text-start">
                                <label for="categoryName" class="form-label">Category Name</label>
                                <input type="text" id="categoryName" class="form-control" v-model="categoryName" required>
                            </div>
                            <div class="form-group mb-3 text-start">
                                <label for="description" class="form-label">Description</label>
                                <textarea id="description" class="form-control" rows="3" v-model="description" required></textarea>
                            </div>
                            
                            <div class="form-group mb-3 text-start">
                                <label class="form-label d-block">Image Source</label>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" id="srcUrlCat" value="url" v-model="imageSource">
                                    <label class="form-check-label" for="srcUrlCat">URL</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" id="srcUploadCat" value="upload" v-model="imageSource">
                                    <label class="form-check-label" for="srcUploadCat">Upload File</label>
                                </div>
                            </div>

                            <div v-if="imageSource === 'url'" class="form-group mb-3 text-start">
                                <label for="imageUrl" class="form-label">Image URL</label>
                                <input type="text" id="imageUrl" class="form-control" v-model="imageUrl" required>
                            </div>
                            <div v-else class="form-group mb-3 text-start">
                                <label for="imageFileCat" class="form-label">Upload Image</label>
                                <input type="file" id="imageFileCat" class="form-control" @change="onFileChange" accept="image/*" required>
                                <div v-if="uploading" class="text-muted small mt-1">
                                    <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                                    Uploading image...
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary w-100 py-2 mt-4" :disabled="uploading">Submit</button>
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
    name: "AddCategory",
    data() {
        return {
            categoryName: "",
            description: "",
            imageUrl: "",
            imageSource: "url",
            uploading: false,
            error: null
        };
    },
    methods: {
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
            } catch (err) {
                this.error = 'Failed to upload image: ' + extractErrorMessage(err);
            } finally {
                this.uploading = false;
            }
        },
        async addCategory() {
            if (!this.imageUrl) {
                this.error = "Please provide an image URL or upload a file.";
                return;
            }

            this.error = null;
            const newCategory = {
                categoryName: this.categoryName,
                description: this.description,
                imageUrl: this.imageUrl
            };

            try {
                const response = await api.post('/category', newCategory);
                alert(response.data.message || 'Category created successfully!');
                this.$router.push({ name: 'CategoryView' });
            } catch (err) {
                this.error = extractErrorMessage(err);
            }
        }
    }
}
</script>

<style scoped>
.card {
    border-radius: 12px;
}
</style>