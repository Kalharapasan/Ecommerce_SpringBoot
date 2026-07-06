<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-10 col-lg-8">
        <div class="card shadow-lg border-0 overflow-hidden" style="border-radius: 16px;">
          <div class="row g-0">
            <!-- Left Branding Panel -->
            <div class="col-md-5 d-none d-md-flex flex-column justify-content-between p-4 p-lg-5 text-white text-start" style="background: linear-gradient(135deg, #1e1b4b 0%, #0f172a 100%);">
              <div>
                <h4 class="font-weight-bold text-warning mb-3">ByteForge PC Store</h4>
                <p class="opacity-75">Welcome back! Log in to build your dream PC setup, check shipping status, or browse the latest graphics cards.</p>
              </div>
              <div class="small opacity-50">
                © 2026 ByteForge PC Store. All rights reserved.
              </div>
            </div>

            <!-- Right Form Panel -->
            <div class="col-md-7 p-4 p-lg-5 bg-white">
              <h3 class="text-center mb-4 font-weight-bold">Sign In</h3>

              <div v-if="error" class="alert alert-danger" role="alert">
                {{ error }}
              </div>

              <form @submit.prevent="handleSignIn">
                <div class="form-group mb-3 text-start">
                  <label for="username" class="form-label">Username</label>
                  <input
                    type="text"
                    id="username"
                    class="form-control"
                    v-model="username"
                    required
                    placeholder="Enter your username"
                  />
                </div>

                <div class="form-group mb-4 text-start">
                  <label for="password" class="form-label">Password</label>
                  <input
                    type="password"
                    id="password"
                    class="form-control"
                    v-model="password"
                    required
                    placeholder="Enter your password"
                  />
                </div>

                <button
                  type="submit"
                  class="btn btn-primary w-100 py-2 mb-3 font-weight-bold"
                  :disabled="loading"
                >
                  <span
                    v-if="loading"
                    class="spinner-border spinner-border-sm me-2"
                    role="status"
                    aria-hidden="true"
                  ></span>
                  Sign In
                </button>

                <div class="text-center">
                  <span class="text-muted">Don't have an account? </span>
                  <router-link to="/signUp" class="text-decoration-none">Sign Up</router-link>
                </div>
              </form>
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

export default {
  name: 'SignIn',
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      error: null
    };
  },
  methods: {
    async handleSignIn() {
      this.loading = true;
      this.error = null;
      try {
        const response = await api.post('/user/signIn', {
          username: this.username,
          password: this.password
        });
        
        const token = response.data.data.token;
        login(token);
        
        const redirectPath = this.$route.query.redirect || '/';
        this.$router.push(redirectPath);
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
</style>
