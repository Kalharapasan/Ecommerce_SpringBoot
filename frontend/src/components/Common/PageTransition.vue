<template>
  <transition name="page-fade" mode="out-in">
    <slot></slot>
  </transition>
</template>

<script>
export default {
  name: 'PageTransition'
};
</script>

<style scoped>
/* Page transition is defined globally in animations.css, but this provides a clean wrapper component */
</style>
