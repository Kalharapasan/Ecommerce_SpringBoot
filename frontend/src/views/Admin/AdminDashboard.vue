<template>
  <div class="container my-5">
    <div class="row mb-4">
      <div class="col-12 text-start">
        <h2 class="font-weight-bold text-dark mb-1">Control Center</h2>
        <p class="text-secondary small">Manage products, categories, users, and deliveries for ByteForge PC Store.</p>
      </div>
    </div>

    <!-- Dashboard Tabs -->
    <div class="card shadow-sm border-0 mb-4 bg-white rounded">
      <div class="card-header bg-dark p-0 border-0">
        <ul class="nav nav-tabs nav-justified border-0" id="adminTabs" role="tablist">
          <li class="nav-item" role="presentation">
            <button 
              class="nav-link py-3 font-weight-bold border-0 text-white rounded-0"
              :class="{'active bg-primary text-white': activeTab === 'deliveries', 'opacity-75': activeTab !== 'deliveries'}"
              @click="activeTab = 'deliveries'"
              type="button"
            >
              Deliveries / Orders
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button 
              class="nav-link py-3 font-weight-bold border-0 text-white rounded-0"
              :class="{'active bg-primary text-white': activeTab === 'users', 'opacity-75': activeTab !== 'users'}"
              @click="activeTab = 'users'"
              type="button"
            >
              Registered Users
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button 
              class="nav-link py-3 font-weight-bold border-0 text-white rounded-0"
              :class="{'active bg-primary text-white': activeTab === 'products', 'opacity-75': activeTab !== 'products'}"
              @click="activeTab = 'products'"
              type="button"
            >
              Products Management
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button 
              class="nav-link py-3 font-weight-bold border-0 text-white rounded-0"
              :class="{'active bg-primary text-white': activeTab === 'categories', 'opacity-75': activeTab !== 'categories'}"
              @click="activeTab = 'categories'"
              type="button"
            >
              Categories Management
            </button>
          </li>
        </ul>
      </div>

      <div class="card-body p-4 text-start">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading data...</span>
          </div>
        </div>

        <div v-else-if="error" class="alert alert-danger" role="alert">
          {{ error }}
        </div>

        <div v-else>
          <!-- TAB 1: DELIVERIES / ORDERS -->
          <div v-if="activeTab === 'deliveries'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="font-weight-bold mb-0">Delivery Orders</h5>
              <span class="badge bg-primary">{{ orders.length }} Order(s)</span>
            </div>
            
            <div v-if="orders.length === 0" class="text-center py-4">
              <p class="text-muted">No orders found in the system.</p>
            </div>

            <div v-else class="table-responsive">
              <table class="table table-hover border">
                <thead class="table-light">
                  <tr>
                    <th>Order ID</th>
                    <th>User</th>
                    <th>Date</th>
                    <th>Items</th>
                    <th>Total Price</th>
                    <th>Delivery Address</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="order in orders" :key="order.orderId">
                    <td class="font-weight-bold">#{{ order.orderId }}</td>
                    <td>{{ order.user ? order.user.fullName : 'Unknown' }}</td>
                    <td>{{ formatDate(order.createdDate) }}</td>
                    <td>
                      <ul class="list-unstyled mb-0 small">
                        <li v-for="item in order.orderItems" :key="item.orderItemId">
                          {{ item.product.productName }} (x{{ item.quantity }})
                        </li>
                      </ul>
                    </td>
                    <td class="font-weight-bold text-primary">{{ formatPrice(order.totalPrice) }}</td>
                    <td class="small">{{ order.address }}</td>
                    <td>
                      <select 
                        class="form-select form-select-sm font-weight-bold" 
                        :class="getStatusClass(order.status)"
                        v-model="order.status"
                        @change="handleStatusChange(order.orderId, order.status)"
                      >
                        <option value="Pending">Pending</option>
                        <option value="Shipped">Shipped</option>
                        <option value="Delivered">Delivered</option>
                        <option value="Cancelled">Cancelled</option>
                      </select>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- TAB 2: REGISTERED USERS -->
          <div v-if="activeTab === 'users'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="font-weight-bold mb-0">Registered Users</h5>
              <span class="badge bg-secondary">{{ users.length }} User(s)</span>
            </div>

            <div class="table-responsive">
              <table class="table table-hover border">
                <thead class="table-light">
                  <tr>
                    <th>User ID</th>
                    <th>Full Name</th>
                    <th>Username</th>
                    <th>Email Address</th>
                    <th>Role</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="u in users" :key="u.userId">
                    <td>#{{ u.userId }}</td>
                    <td class="font-weight-bold">{{ u.fullName || 'N/A' }}</td>
                    <td>{{ u.username }}</td>
                    <td>{{ u.email }}</td>
                    <td>
                      <span class="badge" :class="u.role === 'ADMIN' ? 'bg-danger' : 'bg-secondary'">{{ u.role }}</span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- TAB 3: PRODUCTS -->
          <div v-if="activeTab === 'products'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="font-weight-bold mb-0">Products Catalog</h5>
              <router-link :to="{ name: 'AddProduct' }" class="btn btn-primary btn-sm">Add Product</router-link>
            </div>

            <div class="table-responsive">
              <table class="table table-hover border align-middle">
                <thead class="table-light">
                  <tr>
                    <th>Product</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th class="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="p in products" :key="p.productId">
                    <td>
                      <div class="d-flex align-items-center">
                        <img :src="p.imageUrl" class="img-thumbnail me-3" style="width: 50px; height: 50px; object-fit: contain;">
                        <div>
                          <h6 class="mb-0 font-weight-bold small">{{ p.productName }}</h6>
                        </div>
                      </div>
                    </td>
                    <td class="font-weight-bold text-primary">{{ formatPrice(p.price) }}</td>
                    <td>
                      <span class="badge" :class="p.stock > 0 ? 'bg-success' : 'bg-danger'">{{ p.stock }} in stock</span>
                    </td>
                    <td class="text-end">
                      <router-link :to="{ name: 'EditProduct', params: { id: p.productId } }" class="btn btn-outline-secondary btn-sm me-2">Edit</router-link>
                      <button class="btn btn-outline-danger btn-sm" @click="handleDeleteProduct(p.productId)">Delete</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- TAB 4: CATEGORIES -->
          <div v-if="activeTab === 'categories'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="font-weight-bold mb-0">Categories</h5>
              <router-link :to="{ name: 'AddCategory' }" class="btn btn-primary btn-sm">Add Category</router-link>
            </div>

            <div class="table-responsive">
              <table class="table table-hover border align-middle">
                <thead class="table-light">
                  <tr>
                    <th>Category Name</th>
                    <th>Description</th>
                    <th class="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="c in categories" :key="c.categoryId">
                    <td class="font-weight-bold">{{ c.categoryName }}</td>
                    <td class="small">{{ c.description }}</td>
                    <td class="text-end">
                      <button class="btn btn-outline-danger btn-sm" @click="handleDeleteCategory(c.categoryId)">Delete</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';

export default {
  name: 'AdminDashboard',
  data() {
    return {
      activeTab: 'deliveries',
      loading: true,
      error: null,
      orders: [],
      users: [],
      products: [],
      categories: []
    };
  },
  methods: {
    formatPrice(val) {
      return formatPrice(val);
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },
    getStatusClass(status) {
      if (status === 'Pending') return 'text-warning border-warning';
      if (status === 'Shipped') return 'text-primary border-primary';
      if (status === 'Delivered') return 'text-success border-success';
      if (status === 'Cancelled') return 'text-danger border-danger';
      return '';
    },
    async fetchData() {
      this.loading = true;
      this.error = null;
      try {
        const [ordersRes, usersRes, prodRes, catRes] = await Promise.all([
          api.get('/order/all'),
          api.get('/user/all'),
          api.get('/product'),
          api.get('/category')
        ]);
        this.orders = ordersRes.data.data || [];
        this.users = usersRes.data.data || [];
        this.products = prodRes.data.data || [];
        this.categories = catRes.data.data || [];
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    },
    async handleStatusChange(orderId, status) {
      try {
        await api.put(`/order/${orderId}/status?status=${status}`);
        alert('Order status updated successfully.');
      } catch (err) {
        alert('Failed to update status: ' + extractErrorMessage(err));
        await this.fetchData(); // reload
      }
    },
    async handleDeleteProduct(productId) {
      if (!confirm('Are you sure you want to delete this product?')) return;
      try {
        await api.delete(`/product/${productId}`);
        alert('Product deleted successfully.');
        this.products = this.products.filter(p => p.productId !== productId);
      } catch (err) {
        alert('Failed to delete product: ' + extractErrorMessage(err));
      }
    },
    async handleDeleteCategory(categoryId) {
      if (!confirm('Are you sure you want to delete this category? This will delete all products under it.')) return;
      try {
        await api.delete(`/category/${categoryId}`);
        alert('Category deleted successfully.');
        this.categories = this.categories.filter(c => c.categoryId !== categoryId);
        // Refresh products list since some might have been cascade-deleted or unlinked
        const prodRes = await api.get('/product');
        this.products = prodRes.data.data || [];
      } catch (err) {
        alert('Failed to delete category: ' + extractErrorMessage(err));
      }
    }
  },
  mounted() {
    this.fetchData();
  }
};
</script>

<style scoped>
.card {
  border-radius: 12px;
  overflow: hidden;
}
.nav-tabs .nav-link {
  transition: all 0.25s ease;
}
.nav-tabs .nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>
