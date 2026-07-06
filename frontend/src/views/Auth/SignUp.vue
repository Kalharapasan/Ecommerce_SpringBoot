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
                <p class="opacity-75">Create an account to build high-performance gaming rigs, buy custom PC parts, and access exclusive desktop build components.</p>
              </div>
              <div class="small opacity-50">
                © 2026 ByteForge PC Store. All rights reserved.
              </div>
            </div>

            <!-- Right Form Panel -->
            <div class="col-md-7 p-4 p-lg-5 bg-white">
              <h3 class="text-center mb-4 font-weight-bold">Sign Up</h3>

              <div v-if="error" class="alert alert-danger text-start" role="alert">
                {{ error }}
              </div>

              <div v-if="success" class="alert alert-success text-start" role="alert">
                {{ success }}
              </div>

              <form @submit.prevent="handleSignUp">
                <div class="form-group mb-3 text-start">
                  <label for="fullName" class="form-label">Full Name</label>
                  <input
                    type="text"
                    id="fullName"
                    class="form-control"
                    v-model="fullName"
                    required
                    placeholder="Enter your full name"
                  />
                </div>

                <div class="form-group mb-3 text-start">
                  <label for="email" class="form-label">Email Address</label>
                  <input
                    type="email"
                    id="email"
                    class="form-control"
                    v-model="email"
                    required
                    placeholder="Enter your email address"
                  />
                </div>

                <div class="form-group mb-3 text-start">
                  <label for="username" class="form-label">Username</label>
                  <input
                    type="text"
                    id="username"
                    class="form-control"
                    v-model="username"
                    required
                    placeholder="Choose a username"
                  />
                </div>

                <div class="form-group mb-3 text-start">
                  <label for="password" class="form-label">Password</label>
                  <input
                    type="password"
                    id="password"
                    class="form-control"
                    v-model="password"
                    required
                    placeholder="Create a password"
                  />
                </div>

                <div class="form-group mb-4 text-start">
                  <label for="role" class="form-label">Role</label>
                  <select id="role" class="form-select" v-model="role" required>
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
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
                  Sign Up
                </button>

                <div class="text-center">
                  <span class="text-muted">Already have an account? </span>
                  <router-link to="/signIn" class="text-decoration-none">Sign In</router-link>
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

export default {
  name: 'SignUp',
  data() {
    return {
      fullName: '',
      email: '',
      username: '',
      password: '',
      role: 'USER',
      loading: false,
      error: null,
      success: null
    };
  },
  methods: {
    async handleSignUp() {
      this.loading = true;
      this.error = null;
      this.success = null;
      
      const payload = {
        fullName: this.fullName,
        email: this.email,
        username: this.username,
        password: this.password,
        role: this.role
      };

      try {
        await api.post('/user/signUp', payload);
        this.success = 'Registration successful! Redirecting to Sign In...';
        setTimeout(() => {
          this.$router.push({ name: 'SignIn' });
        }, 1500);
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
