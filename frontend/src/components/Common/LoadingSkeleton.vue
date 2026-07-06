<template>
  <div class="bf-skeleton-wrapper" :class="wrapperClass">
    <!-- Card Skeleton -->
    <div v-if="type === 'card'" class="bf-skeleton-card-wrapper">
      <div class="bf-skeleton bf-skeleton-image"></div>
      <div style="padding: 16px;">
        <div class="bf-skeleton bf-skeleton-title" style="width: 75%;"></div>
        <div class="bf-skeleton bf-skeleton-text" style="width: 100%;"></div>
        <div class="bf-skeleton bf-skeleton-text" style="width: 60%;"></div>
        <div style="display: flex; justify-content: space-between; margin-top: 12px;">
          <div class="bf-skeleton" style="width: 80px; height: 28px; border-radius: 6px;"></div>
          <div class="bf-skeleton" style="width: 60px; height: 28px; border-radius: 6px;"></div>
        </div>
      </div>
    </div>

    <!-- Table Row Skeleton -->
    <div v-else-if="type === 'table'" class="bf-skeleton-table-row">
      <div class="bf-skeleton" style="width: 40px; height: 40px; border-radius: 8px; flex-shrink: 0;"></div>
      <div style="flex: 1; display: flex; flex-direction: column; gap: 6px;">
        <div class="bf-skeleton bf-skeleton-text" style="width: 40%;"></div>
        <div class="bf-skeleton bf-skeleton-text" style="width: 25%;"></div>
      </div>
      <div class="bf-skeleton" style="width: 60px; height: 14px; border-radius: 4px;"></div>
      <div class="bf-skeleton" style="width: 80px; height: 30px; border-radius: 8px;"></div>
    </div>

    <!-- Text Lines Skeleton -->
    <div v-else-if="type === 'text'" class="bf-skeleton-text-block">
      <div class="bf-skeleton bf-skeleton-title" :style="{ width: titleWidth }"></div>
      <div v-for="n in lines" :key="n" class="bf-skeleton bf-skeleton-text" :style="{ width: getLineWidth(n) }"></div>
    </div>

    <!-- Avatar + Text Skeleton -->
    <div v-else-if="type === 'avatar'" class="bf-skeleton-avatar-block">
      <div class="bf-skeleton bf-skeleton-avatar"></div>
      <div style="flex: 1; display: flex; flex-direction: column; gap: 6px;">
        <div class="bf-skeleton bf-skeleton-text" style="width: 50%;"></div>
        <div class="bf-skeleton bf-skeleton-text" style="width: 30%;"></div>
      </div>
    </div>

    <!-- Stat Widget Skeleton -->
    <div v-else-if="type === 'stat'" class="bf-skeleton-stat">
      <div class="bf-skeleton" style="width: 48px; height: 48px; border-radius: 12px;"></div>
      <div style="flex: 1; display: flex; flex-direction: column; gap: 6px;">
        <div class="bf-skeleton" style="width: 40%; height: 24px; border-radius: 6px;"></div>
        <div class="bf-skeleton bf-skeleton-text" style="width: 60%;"></div>
      </div>
    </div>

    <!-- Default / Generic Block -->
    <div v-else class="bf-skeleton bf-skeleton-card" :style="{ height: height, width: width }"></div>
  </div>
</template>

<script>
export default {
  name: 'LoadingSkeleton',
  props: {
    type: {
      type: String,
      default: 'card',
      validator: (v) => ['card', 'table', 'text', 'avatar', 'stat', 'block'].includes(v)
    },
    lines: {
      type: Number,
      default: 3
    },
    titleWidth: {
      type: String,
      default: '60%'
    },
    height: {
      type: String,
      default: '200px'
    },
    width: {
      type: String,
      default: '100%'
    },
    wrapperClass: {
      type: String,
      default: ''
    }
  },
  methods: {
    getLineWidth(n) {
      const widths = ['100%', '85%', '70%', '90%', '60%'];
      return widths[(n - 1) % widths.length];
    }
  }
};
</script>

<style scoped>
.bf-skeleton-card-wrapper {
  border-radius: var(--bf-radius-lg, 16px);
  border: 1px solid var(--bf-border, #e2e8f0);
  overflow: hidden;
  background: var(--bf-bg-card, #fff);
}

.bf-skeleton-table-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid var(--bf-border-light, #f1f5f9);
}

.bf-skeleton-text-block {
  display: flex;
  flex-direction: column;
}

.bf-skeleton-avatar-block {
  display: flex;
  align-items: center;
  gap: 14px;
}

.bf-skeleton-stat {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: var(--bf-radius-lg, 16px);
  border: 1px solid var(--bf-border, #e2e8f0);
  background: var(--bf-bg-card, #fff);
}
</style>
