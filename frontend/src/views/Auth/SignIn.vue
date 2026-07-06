<template>
  <div class="bf-page bf-fade-in p-4 d-flex align-items-center justify-content-center" style="min-height: calc(100vh - 160px);">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
          <div class="bf-card bf-glass overflow-hidden shadow-xl" style="border-radius: var(--bf-radius-xl);">
            <div class="row g-0">
              <!-- Left Branding Panel -->
              <div class="col-md-5 d-none d-md-flex flex-column justify-content-between p-4 p-lg-5 text-white text-start" style="background: var(--bf-gradient-hero);">
                <div>
                  <h4 class="font-weight-bold mb-3 d-flex align-items-center gap-2" style="color: var(--bf-accent)">
                    ⚡ ByteForge PC
                  </h4>
                  <p class="opacity-75 small" style="line-height: 1.6;">Welcome back! Sign in to build your dream PC setup, check shipping status, or browse the latest graphics cards.</p>
                </div>
                <div class="small opacity-50">
                  © 2026 ByteForge Store.
                </div>
              </div>

              <!-- Right Form Panel -->
              <div class="col-md-7 p-4 p-lg-5 text-start bg-white">
                <h3 class="font-weight-bold text-dark mb-4">Sign In</h3>

                <form @submit.prevent="handleSignIn">
                  <div class="mb-3">
                    <label for="username" class="form-label font-weight-bold text-secondary">Username</label>
                    <input
                      type="text"
                      id="username"
                      class="bf-input"
                      v-model="username"
                      required
                      placeholder="Enter your username"
                    />
                  </div>

                  <div class="mb-4">
                    <label for="password" class="form-label font-weight-bold text-secondary">Password</label>
                    <input
                      type="password"
                      id="password"
                      class="bf-input"
                      v-model="password"
                      required
                      placeholder="Enter your password"
                    />
                  </div>

                  <button
                    type="submit"
                    class="bf-btn bf-btn-primary w-100 py-2.5 mb-3"
                    :disabled="loading"
                  >
                    <span v-if="loading" class="bf-spinner me-2"></span>
                    Sign In
                  </button>

                  <div class="text-center">
                    <span class="text-muted small">Don't have an account? </span>
                    <router-link to="/signUp" class="small font-weight-bold text-decoration-none">Sign Up</router-link>
                  </div>
                </form>
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
import { login } from '../../utils/auth';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'SignIn',
  data() {
    return {
      username: '',
      password: '',
      loading: false
    };
  },
  methods: {
    async handleSignIn() {
      this.loading = true;
      try {
        const response = await api.post('/user/signIn', {
          username: this.username,
          password: this.password
        });

        const token = response.data.data.token;
        login(token);

        showToast({
          message: 'Welcome back to ByteForge PC Store!',
          type: 'success',
          title: 'Sign In Successful'
        });

        const redirectPath = this.$route.query.redirect || '/';
        this.$router.push(redirectPath);
      } catch (err) {
        showToast({
          message: extractErrorMessage(err),
          type: 'error',
          title: 'Authentication Failed'
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
