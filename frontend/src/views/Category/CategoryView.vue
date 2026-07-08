<template>
  <div class="container py-5 bf-fade-in">
    <div class="row align-items-center mb-5">
      <div class="col-md-6 text-start">
        <h1 class="text-white font-bold mb-2">Manage Categories</h1>
        <p class="text-muted small">Configure hardware categories, upload icons, and edit system descriptions.</p>
      </div>
      <div class="col-md-6 text-md-end text-start mt-3 mt-md-0">
        <router-link :to="{ name: 'AddCategory' }">
          <button class="bf-btn-premium border-0 py-2.5 px-4">
            <i class="bi bi-plus-circle me-1"></i> Add New Category
          </button>
        </router-link>
      </div>
    </div>

    <!-- Category Grid -->
    <div class="row g-4">
      <div 
        v-for="category of categories" 
        :key="category.categoryId" 
        class="col-xl-3 col-lg-4 col-md-6 d-flex mb-3"
      >
        <CategoryBox :category="category"></CategoryBox>
      </div>
    </div>
  </div>
</template>

<script>
import CategoryBox from '../../components/Category/CategoryBox.vue';
import api, { extractErrorMessage } from '../../utils/api';

export default {
  name: "CategoryView",
  components: { CategoryBox },
  data() {
    return {
      categories: [],
    };
  },
  methods: {
    async getCategories() {
      try {
        const response = await api.get('/category');
        this.categories = response.data.data || [];
      } catch (err) {
        console.error('Failed to get categories:', extractErrorMessage(err));
      }
    },
  },
  mounted() {
    this.getCategories();
  }
}
</script>

<style scoped>
</style>