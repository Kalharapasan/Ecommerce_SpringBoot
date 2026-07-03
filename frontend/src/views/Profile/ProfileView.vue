<template>
  <div class="container my-5">
    <div class="row justify-content-center">
      <div class="col-md-8 col-lg-6">
        <div class="card shadow-sm border-0">
          <div class="card-header bg-dark text-white p-4">
            <h4 class="mb-0">User Profile</h4>
          </div>
          <div class="card-body p-4">
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
            </div>
            
            <div v-else-if="error" class="alert alert-danger" role="alert">
              {{ error }}
            </div>

            <div v-else-if="user">
              <div class="row mb-3 pb-3 border-bottom text-start">
                <div class="col-sm-4 text-muted">Full Name</div>
                <div class="col-sm-8 font-weight-bold">
                  {{ user.fullName || 'N/A' }}
                </div>
              </div>
              
              <div class="row mb-3 pb-3 border-bottom text-start">
                <div class="col-sm-4 text-muted">Username</div>
                <div class="col-sm-8"><strong>{{ user.username }}</strong></div>
              </div>

              <div class="row mb-3 pb-3 border-bottom text-start">
                <div class="col-sm-4 text-muted">Email Address</div>
                <div class="col-sm-8">{{ user.email }}</div>
              </div>

              <div class="row mb-3 text-start">
                <div class="col-sm-4 text-muted">Account Role</div>
                <div class="col-sm-8">
                  <span class="badge bg-secondary">{{ user.role }}</span>
                </div>
              </div>

              <!-- Cart Summary Stats -->
              <div class="mt-4 pt-4 border-top text-center bg-light rounded p-3">
                <h6 class="text-secondary font-weight-bold mb-2">Shopping Activity</h6>
                <p class="mb-0 text-muted">
                  Your shopping cart currently holds 
                  <strong class="text-primary h5 mx-1 mb-0 align-middle">{{ cartCount }}</strong> 
                  item(s).
                </p>
                <router-link to="/cart" class="btn btn-outline-primary btn-sm mt-3" v-if="cartCount > 0">
                  Go to Cart
                </router-link>
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
import { getCurrentUser, authState } from '../../utils/auth';

export default {
  name: 'ProfileView',
  data() {
    return {
      user: null,
      loading: true,
      error: null
    };
  },
  computed: {
    cartCount() {
      return authState.cartCount;
    }
  },
  async mounted() {
    const currentUser = getCurrentUser();
    if (!currentUser || !currentUser.userId) {
      this.error = 'User session not found. Please log in.';
      this.loading = false;
      return;
    }
    
    try {
      const response = await api.get(`/user/profile/${currentUser.userId}`);
      this.user = response.data.data;
    } catch (err) {
      this.error = extractErrorMessage(err);
    } finally {
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.card {
  border-radius: 12px;
  overflow: hidden;
}
</style>
