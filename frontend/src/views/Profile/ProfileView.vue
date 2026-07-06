<template>
  <div class="container my-5">
    <div class="row">
      <!-- Profile Details Column -->
      <div class="col-lg-4 mb-4">
        <div class="card shadow-sm border-0 h-100">
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
                  <span class="badge bg-secondary text-uppercase">{{ user.role }}</span>
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

      <!-- Order History Column -->
      <div class="col-lg-8 mb-4">
        <div class="card shadow-sm border-0 h-100">
          <div class="card-header bg-primary text-white p-4 d-flex justify-content-between align-items-center">
            <h4 class="mb-0">Order History</h4>
            <span class="badge bg-light text-primary font-weight-bold" style="font-size: 0.9rem;" v-if="!ordersLoading && !ordersError">
              {{ orders.length }} Order(s)
            </span>
          </div>
          <div class="card-body p-4" style="max-height: 700px; overflow-y: auto;">
            <div v-if="ordersLoading" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading orders...</span>
              </div>
            </div>
            
            <div v-else-if="ordersError" class="alert alert-danger text-start">
              {{ ordersError }}
            </div>
            
            <div v-else-if="orders.length === 0" class="text-center py-5">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" class="bi bi-bag-x text-muted mb-3" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M6.146 8.146a.5.5 0 0 1 .708 0L8 9.293l1.146-1.147a.5.5 0 1 1 .708.708L8.707 10l1.147 1.146a.5.5 0 0 1-.708.708L8 10.707l-1.146 1.147a.5.5 0 0 1-.708-.708L7.293 10 6.146 8.854a.5.5 0 0 1 0-.708"/>
                <path d="M8 1a2.5 2.5 0 0 1 2.5 2.5V4h-5v-.5A2.5 2.5 0 0 1 8 1m3.5 3v-.5a3.5 3.5 0 1 0-7 0V4H1v3a2.99 2.99 0 0 0 .39 1.488L3.102 14H12.9a3 3 0 0 0 2.992-2.733L15 4zM1.858 5H14.14l-1.294 8.283A2 2 0 0 1 10.858 15H5.143a2 2 0 0 1-1.988-1.717z"/>
              </svg>
              <p class="text-muted mb-3">You haven't placed any orders yet.</p>
              <router-link to="/product" class="btn btn-primary btn-sm">Shop Now</router-link>
            </div>
            
            <div v-else>
              <div v-for="order in orders" :key="order.orderId" class="card mb-4 border shadow-sm">
                <div class="card-header bg-light d-flex flex-wrap justify-content-between align-items-center gap-2">
                  <div class="text-start">
                    <small class="text-muted d-block">ORDER PLACED</small>
                    <span class="font-weight-bold small">{{ formatDate(order.createdDate) }}</span>
                  </div>
                  <div class="text-start">
                    <small class="text-muted d-block">SHIP TO</small>
                    <span class="text-truncate d-inline-block small font-weight-bold" style="max-width: 180px;" :title="order.address">
                      {{ order.address }}
                    </span>
                  </div>
                  <div class="text-start">
                    <small class="text-muted d-block">PAYMENT</small>
                    <span class="badge bg-secondary font-weight-bold">{{ formatPaymentMethod(order.paymentMethod) }}</span>
                  </div>
                  <div class="text-end">
                    <small class="text-muted d-block">TOTAL</small>
                    <span class="font-weight-bold text-primary">{{ formatPrice(order.totalPrice) }}</span>
                  </div>
                </div>
                <div class="card-body p-0">
                  <div 
                    v-for="item in order.orderItems" 
                    :key="item.orderItemId" 
                    class="d-flex align-items-center p-3 border-bottom last-border-0"
                  >
                    <img 
                      :src="item.product.imageUrl" 
                      class="img-thumbnail me-3" 
                      style="width: 50px; height: 50px; object-fit: contain;"
                      alt="Product Image"
                    />
                    <div class="text-start flex-grow-1">
                      <h6 class="mb-1 font-weight-bold small text-dark">{{ item.product.productName }}</h6>
                      <small class="text-muted">Quantity: {{ item.quantity }}</small>
                    </div>
                    <div class="text-end">
                      <span class="font-weight-bold text-dark small">{{ formatPrice(item.price * item.quantity) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { getCurrentUser, authState, getToken } from '../../utils/auth';

export default {
  name: 'ProfileView',
  data() {
    return {
      user: null,
      loading: true,
      error: null,
      
      // Orders History
      orders: [],
      ordersLoading: true,
      ordersError: null
    };
  },
  computed: {
    cartCount() {
      return authState.cartCount;
    }
  },
  methods: {
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    formatPaymentMethod(method) {
      if (method === 'CASH_ON_DELIVERY') return 'COD';
      if (method === 'ONLINE_PAYMENT') return 'Online';
      return method;
    },
    formatPrice(val) {
      return formatPrice(val);
    },
    async fetchOrderHistory() {
      const token = getToken();
      if (!token) {
        this.ordersLoading = false;
        return;
      }
      this.ordersLoading = true;
      try {
        const response = await api.get(`/order/user/${token}`);
        this.orders = response.data.data || [];
      } catch (err) {
        this.ordersError = extractErrorMessage(err);
      } finally {
        this.ordersLoading = false;
      }
    }
  },
  async mounted() {
    const currentUser = getCurrentUser();
    if (!currentUser || !currentUser.userId) {
      this.error = 'User session not found. Please log in.';
      this.loading = false;
      this.ordersLoading = false;
      return;
    }
    
    try {
      const response = await api.get(`/user/profile/${currentUser.userId}`);
      this.user = response.data.data;
      await this.fetchOrderHistory();
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
.last-border-0:last-child {
  border-bottom: 0 !important;
}
</style>
