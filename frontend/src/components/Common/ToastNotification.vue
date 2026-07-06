<template>
  <teleport to="body">
    <div class="bf-toast-container" :class="positionClass">
      <transition-group name="toast-slide" tag="div">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="bf-toast"
          :class="'bf-toast-' + toast.type"
          @mouseenter="pauseToast(toast)"
          @mouseleave="resumeToast(toast)"
        >
          <div class="bf-toast-icon">
            <span v-if="toast.type === 'success'">✓</span>
            <span v-else-if="toast.type === 'error'">✕</span>
            <span v-else-if="toast.type === 'warning'">⚠</span>
            <span v-else>ℹ</span>
          </div>
          <div class="bf-toast-content">
            <div class="bf-toast-title" v-if="toast.title">{{ toast.title }}</div>
            <div class="bf-toast-message">{{ toast.message }}</div>
          </div>
          <button class="bf-toast-close" @click="removeToast(toast.id)">&times;</button>
          <div class="bf-toast-progress" v-if="!toast.paused">
            <div
              class="bf-toast-progress-bar"
              :style="{ animationDuration: toast.duration + 'ms' }"
            ></div>
          </div>
        </div>
      </transition-group>
    </div>
  </teleport>
</template>

<script>
import { reactive } from 'vue';

// Global toast state — accessible from any component via `import { showToast }`
const toastState = reactive({
  toasts: [],
  nextId: 0
});

/**
 * Show a toast notification from anywhere in the app.
 * @param {Object} options - { message, title?, type?, duration? }
 *   type: 'success' | 'error' | 'warning' | 'info'
 *   duration: auto-dismiss time in ms (default 4000)
 */
export function showToast({ message, title = '', type = 'info', duration = 4000 }) {
  const id = toastState.nextId++;
  const toast = { id, message, title, type, duration, paused: false, timer: null };

  toast.timer = setTimeout(() => {
    removeToastById(id);
  }, duration);

  toastState.toasts.push(toast);

  // Keep max 5 toasts on screen
  if (toastState.toasts.length > 5) {
    const oldest = toastState.toasts[0];
    clearTimeout(oldest.timer);
    toastState.toasts.shift();
  }
}

function removeToastById(id) {
  const idx = toastState.toasts.findIndex(t => t.id === id);
  if (idx !== -1) {
    clearTimeout(toastState.toasts[idx].timer);
    toastState.toasts.splice(idx, 1);
  }
}

export default {
  name: 'ToastNotification',
  props: {
    position: {
      type: String,
      default: 'top-right',
      validator: (v) => ['top-right', 'top-left', 'bottom-right', 'bottom-left', 'top-center'].includes(v)
    }
  },
  computed: {
    toasts() {
      return toastState.toasts;
    },
    positionClass() {
      return 'bf-toast-' + this.position;
    }
  },
  methods: {
    removeToast(id) {
      removeToastById(id);
    },
    pauseToast(toast) {
      toast.paused = true;
      clearTimeout(toast.timer);
    },
    resumeToast(toast) {
      toast.paused = false;
      toast.timer = setTimeout(() => {
        removeToastById(toast.id);
      }, 1500);
    }
  }
};
</script>

<style>
.bf-toast-container {
  position: fixed;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 400px;
  pointer-events: none;
}
.bf-toast-container > div {
  pointer-events: auto;
}

/* Positions */
.bf-toast-top-right { top: 20px; right: 20px; }
.bf-toast-top-left { top: 20px; left: 20px; }
.bf-toast-bottom-right { bottom: 20px; right: 20px; }
.bf-toast-bottom-left { bottom: 20px; left: 20px; }
.bf-toast-top-center { top: 20px; left: 50%; transform: translateX(-50%); }

/* Toast Item */
.bf-toast {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--bf-radius-lg, 16px);
  background: var(--bf-bg-elevated, #fff);
  border: 1px solid var(--bf-border, #e2e8f0);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.06);
  min-width: 320px;
  position: relative;
  overflow: hidden;
  cursor: default;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

.bf-toast-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
  margin-top: 1px;
}

.bf-toast-content {
  flex: 1;
  min-width: 0;
}

.bf-toast-title {
  font-weight: 700;
  font-size: 0.875rem;
  margin-bottom: 2px;
  color: var(--bf-text-primary, #0f172a);
}

.bf-toast-message {
  font-size: 0.8125rem;
  color: var(--bf-text-secondary, #475569);
  line-height: 1.4;
}

.bf-toast-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: var(--bf-text-muted, #94a3b8);
  padding: 0 4px;
  line-height: 1;
  flex-shrink: 0;
  transition: color 0.15s;
}
.bf-toast-close:hover {
  color: var(--bf-text-primary, #0f172a);
}

/* Progress bar */
.bf-toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(0, 0, 0, 0.05);
}

.bf-toast-progress-bar {
  height: 100%;
  width: 100%;
  animation: bf-progressShrink linear forwards;
}

/* Type variants */
.bf-toast-success .bf-toast-icon { background: rgba(16, 185, 129, 0.15); color: #10b981; }
.bf-toast-success .bf-toast-progress-bar { background: #10b981; }
.bf-toast-success { border-left: 4px solid #10b981; }

.bf-toast-error .bf-toast-icon { background: rgba(239, 68, 68, 0.15); color: #ef4444; }
.bf-toast-error .bf-toast-progress-bar { background: #ef4444; }
.bf-toast-error { border-left: 4px solid #ef4444; }

.bf-toast-warning .bf-toast-icon { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
.bf-toast-warning .bf-toast-progress-bar { background: #f59e0b; }
.bf-toast-warning { border-left: 4px solid #f59e0b; }

.bf-toast-info .bf-toast-icon { background: rgba(59, 130, 246, 0.15); color: #3b82f6; }
.bf-toast-info .bf-toast-progress-bar { background: #3b82f6; }
.bf-toast-info { border-left: 4px solid #3b82f6; }

/* Transition animations */
.toast-slide-enter-active {
  animation: bf-toastSlideIn 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.toast-slide-leave-active {
  animation: bf-toastSlideOut 0.25s ease-in forwards;
}
.toast-slide-move {
  transition: transform 0.3s ease;
}
</style>
