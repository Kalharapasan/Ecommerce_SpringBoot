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
                  <p class="opacity-75 small" style="line-height: 1.6;">Create an account to build high-performance gaming rigs, buy custom PC parts, or list products as a verified seller.</p>
                </div>
                <div class="small opacity-50">
                  © 2026 ByteForge Store.
                </div>
              </div>

              <!-- Right Form Panel -->
              <div class="col-md-7 p-4 p-lg-5 text-start bg-white">
                <h3 class="font-weight-bold text-dark mb-4">Sign Up</h3>

                <form @submit.prevent="handleSignUp">
                  <div class="mb-3">
                    <label for="fullName" class="form-label font-weight-bold text-secondary">Full Name</label>
                    <input
                      type="text"
                      id="fullName"
                      class="bf-input"
                      v-model="fullName"
                      required
                      placeholder="Enter your full name"
                    />
                  </div>

                  <div class="mb-3">
                    <label for="email" class="form-label font-weight-bold text-secondary">Email Address</label>
                    <input
                      type="email"
                      id="email"
                      class="bf-input"
                      v-model="email"
                      required
                      placeholder="Enter your email address"
                    />
                  </div>

                  <div class="mb-3">
                    <label for="username" class="form-label font-weight-bold text-secondary">Username</label>
                    <input
                      type="text"
                      id="username"
                      class="bf-input"
                      v-model="username"
                      required
                      placeholder="Choose a username"
                    />
                  </div>

                  <div class="mb-3">
                    <label for="password" class="form-label font-weight-bold text-secondary">Password</label>
                    <input
                      type="password"
                      id="password"
                      class="bf-input"
                      v-model="password"
                      required
                      placeholder="Create a password"
                    />
                  </div>

                  <div class="mb-4">
                    <label for="role" class="form-label font-weight-bold text-secondary">Account Type</label>
                    <select id="role" class="bf-input text-capitalize" v-model="role" required>
                      <option value="USER">Buyer (Customer)</option>
                      <option value="SELLER">Seller (Merchant)</option>
                      <option value="ADMIN">Administrator</option>
                    </select>
                  </div>

                  <button
                    type="submit"
                    class="bf-btn bf-btn-primary w-100 py-2.5 mb-3"
                    :disabled="loading"
                  >
                    <span v-if="loading" class="bf-spinner me-2"></span>
                    Sign Up
                  </button>

                  <div class="text-center">
                    <span class="text-muted small">Already have an account? </span>
                    <router-link to="/signIn" class="small font-weight-bold text-decoration-none">Sign In</router-link>
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
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'SignUp',
  data() {
    return {
      fullName: '',
      email: '',
      username: '',
      password: '',
      role: 'USER',
      loading: false
    };
  },
  methods: {
    async handleSignUp() {
      this.loading = true;

      const payload = {
        fullName: this.fullName,
        email: this.email,
        username: this.username,
        password: this.password,
        role: this.role
      };

      try {
        await api.post('/user/signUp', payload);
        showToast({
          message: 'Your account was registered successfully!',
          type: 'success',
          title: 'Registration Successful'
        });
        setTimeout(() => {
          this.$router.push({ name: 'SignIn' });
        }, 1500);
      } catch (err) {
        showToast({
          message: extractErrorMessage(err),
          type: 'error',
          title: 'Registration Failed'
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
