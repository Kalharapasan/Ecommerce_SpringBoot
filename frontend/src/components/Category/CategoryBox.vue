<template>
  <div class="card bf-glass-card w-100 h-100 d-flex flex-column justify-content-between overflow-hidden">
    <div class="ebx-image-wrapper">
      <img class="ebx-image" :src="category.imageUrl" alt="Category Image" @error="handleImageError" />
      <div class="ebx-image-overlay"></div>
    </div>
    <div class="card-body d-flex flex-column text-start p-4">
      <h5 class="card-title text-white font-bold mb-2">{{ category.categoryName }}</h5>
      <p class="card-text text-muted small flex-grow-1 mb-4">{{ category.description }}</p>
      
      <router-link :to="{ name: 'EditCategory', params: { id: category.categoryId } }" class="bf-btn-premium text-center d-block py-2 text-decoration-none">
        <i class="bi bi-pencil-square me-1"></i> Edit Category
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "CategoryBox",
  props: ["category"],
  methods: {
    handleImageError(event) {
      event.target.src = 'https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=500&q=80';
    }
  }
}
</script>

<style scoped>
.ebx-image-wrapper {
  position: relative;
  overflow: hidden;
  aspect-ratio: 16/9;
  background: var(--bf-bg-secondary);
}

.ebx-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.bf-glass-card:hover .ebx-image {
  transform: scale(1.06);
}

.ebx-image-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(to top, rgba(11, 15, 25, 0.4) 0%, transparent 60%);
}
</style>