<template>
  <div class="bf-page bf-fade-in py-5 text-start">
    <div class="container position-relative">
      <div class="glow-bg animate-float"></div>

      <!-- Header -->
      <div class="row mb-5 position-relative z-2">
        <div class="col-12 text-start">
          <span class="badge bg-danger bg-opacity-25 text-danger px-3 py-2 mb-2 rounded-pill text-uppercase">
            <i class="bi bi-shield-lock-fill me-1"></i> Control Center Console
          </span>
          <h1 class="text-white font-bold mb-1 fs-2">Admin Operations Console</h1>
          <p class="text-muted small">Manage catalog configurations, inspect user directory privileges, approve multi-vendor storefronts, and process ticket resolutions.</p>
        </div>
      </div>

      <!-- Dashboard Tabs Grid Navigation -->
      <div class="bf-glass-card overflow-hidden mb-5 border-0 position-relative z-2">
        <div class="bf-tabs-nav bg-black bg-opacity-30 border-bottom border-secondary border-opacity-10 d-flex overflow-x-auto">
          <button
            v-for="tab in tabItems"
            :key="tab.id"
            class="bf-tab-btn-cyber text-uppercase"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <span class="bf-tab-icon me-2">{{ tab.icon }}</span>
            <span class="bf-tab-text">{{ tab.label }}</span>
            <span v-if="tab.badgeCount && tab.badgeCount > 0" class="bf-tab-badge badge bg-danger ms-2 animate-pulse-glow">
              {{ tab.badgeCount }}
            </span>
          </button>
        </div>

        <div class="p-4 p-md-5 text-start text-white">
          <!-- Loading skeleton -->
          <div v-if="loading" class="py-5">
            <div class="bf-skeleton mb-3" style="height: 40px; width: 30%;"></div>
            <div class="bf-skeleton" style="height: 250px;"></div>
          </div>

          <!-- Error Alert -->
          <div v-else-if="error" class="bf-empty-state p-5 text-center">
            <div class="bf-empty-icon text-danger fs-1"><i class="bi bi-exclamation-triangle"></i></div>
            <h5 class="text-white font-bold mt-3">Sync Operation Failure</h5>
            <p class="text-muted">{{ error }}</p>
            <button class="bf-btn-premium border-0 mt-3" @click="fetchData">Retry Sync Pipeline</button>
          </div>

          <div v-else>
            <!-- ─── TAB 1: DELIVERIES / ORDERS ─── -->
            <div v-if="activeTab === 'deliveries'" class="bf-fade-in">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <div>
                  <h4 class="text-white font-bold mb-1">Marketplace Deliveries</h4>
                  <p class="text-muted small">Track and dispatch customer components packages</p>
                </div>
                <span class="badge bg-primary bg-opacity-25 text-cyan font-bold px-3 py-2 fs-7">{{ orders.length }} Total Orders</span>
              </div>

              <div v-if="orders.length === 0" class="text-center py-5 text-muted">
                <p class="mb-0">No active customer orders registered in pipeline.</p>
              </div>

              <div v-else class="table-responsive">
                <table class="table table-borderless align-middle text-white mb-0">
                  <thead>
                    <tr class="border-bottom border-secondary border-opacity-10 text-muted small text-uppercase">
                      <th class="py-3">Order ID</th>
                      <th class="py-3">Customer</th>
                      <th class="py-3">Date</th>
                      <th class="py-3">Components</th>
                      <th class="py-3">Total Cost</th>
                      <th class="py-3">Address</th>
                      <th class="py-3">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="order in orders" :key="order.orderId" class="border-bottom border-secondary border-opacity-10">
                      <td class="font-bold text-white py-4">#{{ order.orderId }}</td>
                      <td class="py-4">
                        <div class="font-bold text-white">{{ order.user ? order.user.fullName : 'Guest' }}</div>
                        <small class="text-muted">@{{ order.user ? order.user.username : 'guest' }}</small>
                      </td>
                      <td class="small py-4 text-muted">{{ formatDate(order.createdDate) }}</td>
                      <td class="py-4">
                        <ul class="list-unstyled mb-0 small text-secondary">
                          <li v-for="item in order.orderItems" :key="item.orderItemId" class="mb-1 text-muted">
                            ⚙️ {{ item.product.productName }} <span class="text-cyan font-bold">(x{{ item.quantity }})</span>
                          </li>
                        </ul>
                      </td>
                      <td class="font-bold text-gradient-primary py-4">{{ formatPrice(order.totalPrice) }}</td>
                      <td class="small text-muted py-4">{{ order.address }}</td>
                      <td class="py-4">
                        <select
                          class="bf-search-input py-1.5 px-3 bg-opacity-25 bg-black border-secondary border-opacity-25 text-white font-bold small"
                          :style="getStatusColorStyle(order.status)"
                          v-model="order.status"
                          @change="handleStatusChange(order.orderId, order.status)"
                        >
                          <option value="Pending" class="text-warning bg-black">Pending</option>
                          <option value="Shipped" class="text-info bg-black">Shipped</option>
                          <option value="Delivered" class="text-success bg-black">Delivered</option>
                          <option value="Cancelled" class="text-danger bg-black">Cancelled</option>
                        </select>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- ─── TAB 2: REGISTERED USERS ─── -->
            <div v-if="activeTab === 'users'" class="bf-fade-in">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <div>
                  <h4 class="text-white font-bold mb-1">User Directory</h4>
                  <p class="text-muted small">Registered privilege roles, client information, and authentication status</p>
                </div>
                <span class="badge bg-primary bg-opacity-25 text-cyan font-bold px-3 py-2 fs-7">{{ users.length }} Active accounts</span>
              </div>

              <div class="table-responsive">
                <table class="table table-borderless align-middle text-white mb-0">
                  <thead>
                    <tr class="border-bottom border-secondary border-opacity-10 text-muted small text-uppercase">
                      <th class="py-3">UID</th>
                      <th class="py-3">Name</th>
                      <th class="py-3">Username</th>
                      <th class="py-3">Email Address</th>
                      <th class="py-3">Phone Number</th>
                      <th class="py-3">Privilege status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="u in users" :key="u.userId" class="border-bottom border-secondary border-opacity-10">
                      <td class="py-4 text-muted">#{{ u.userId }}</td>
                      <td class="font-bold text-white py-4">{{ u.fullName || 'No Name' }}</td>
                      <td class="py-4 text-muted">@{{ u.username }}</td>
                      <td class="py-4 text-white">{{ u.email }}</td>
                      <td class="py-4 text-muted">{{ u.phoneNumber || 'Not Provided' }}</td>
                      <td class="py-4">
                        <span class="badge" :class="getRoleBadgeClass(u.role)">{{ u.role }}</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- ─── TAB 3: PRODUCTS ─── -->
            <div v-if="activeTab === 'products'" class="bf-fade-in">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <div>
                  <h4 class="text-white font-bold mb-1">Marketplace Inventory</h4>
                  <p class="text-muted small">Manage active hardware listings catalog levels</p>
                </div>
                <router-link :to="{ name: 'AddProduct' }" class="text-decoration-none">
                  <button class="bf-btn-premium border-0 py-2 px-4">
                    <i class="bi bi-plus-circle me-1"></i> List Component
                  </button>
                </router-link>
              </div>

              <div class="table-responsive">
                <table class="table table-borderless align-middle text-white mb-0">
                  <thead>
                    <tr class="border-bottom border-secondary border-opacity-10 text-muted small text-uppercase">
                      <th class="py-3">Product Specification</th>
                      <th class="py-3">Market Rate</th>
                      <th class="py-3">Stock level</th>
                      <th class="py-3 text-end">Operations</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="p in products" :key="p.productId" class="border-bottom border-secondary border-opacity-10">
                      <td class="py-3">
                        <div class="d-flex align-items-center gap-3">
                          <div class="bf-product-mini-img-wrapper bf-glass border-0">
                            <img :src="p.imageUrl" class="bf-product-mini-img" alt="Thumbnail" @error="handleImageError" />
                          </div>
                          <div class="text-start">
                            <h6 class="mb-1 font-bold text-white">{{ p.productName }}</h6>
                            <small class="text-muted" v-if="p.category">{{ p.category.categoryName }}</small>
                          </div>
                        </div>
                      </td>
                      <td class="font-bold text-gradient-primary py-3">{{ formatPrice(p.price) }}</td>
                      <td class="py-3">
                        <span class="badge" :class="p.stock > 0 ? 'bg-success bg-opacity-25 text-success' : 'bg-danger bg-opacity-25 text-danger'">
                          {{ p.stock > 0 ? p.stock + ' in stock' : 'Out of stock' }}
                        </span>
                      </td>
                      <td class="text-end py-3">
                        <div class="d-inline-flex gap-2">
                          <router-link :to="{ name: 'EditProduct', params: { id: p.productId } }" class="text-decoration-none">
                            <button class="bf-btn-ghost py-1.5 px-3 border-secondary border-opacity-25 text-cyan small">Edit</button>
                          </router-link>
                          <button class="bf-btn-ghost py-1.5 px-3 border-danger border-opacity-25 text-danger small" @click="handleDeleteProduct(p.productId)">
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- ─── TAB 4: CATEGORIES ─── -->
            <div v-if="activeTab === 'categories'" class="bf-fade-in">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <div>
                  <h4 class="text-white font-bold mb-1">Catalog Categories</h4>
                  <p class="text-muted small">Configure categorizations structure metrics</p>
                </div>
                <router-link :to="{ name: 'AddCategory' }" class="text-decoration-none">
                  <button class="bf-btn-premium border-0 py-2 px-4">
                    <i class="bi bi-plus-circle me-1"></i> Create Category
                  </button>
                </router-link>
              </div>

              <div class="table-responsive">
                <table class="table table-borderless align-middle text-white mb-0">
                  <thead>
                    <tr class="border-bottom border-secondary border-opacity-10 text-muted small text-uppercase">
                      <th class="py-3">Category classification</th>
                      <th class="py-3">Description coordinates</th>
                      <th class="py-3 text-end">Operations</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="c in categories" :key="c.categoryId" class="border-bottom border-secondary border-opacity-10">
                      <td class="font-bold text-white py-4">{{ c.categoryName }}</td>
                      <td class="small text-muted py-4">{{ c.description }}</td>
                      <td class="text-end py-4">
                        <div class="d-inline-flex gap-2">
                          <router-link :to="{ name: 'EditCategory', params: { id: c.categoryId } }" class="text-decoration-none">
                            <button class="bf-btn-ghost py-1.5 px-3 border-secondary border-opacity-25 text-cyan small">Edit</button>
                          </router-link>
                          <button class="bf-btn-ghost py-1.5 px-3 border-danger border-opacity-25 text-danger small" @click="handleDeleteCategory(c.categoryId)">
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- ─── TAB 5: STORES / VENDORS ─── -->
            <div v-if="activeTab === 'stores'" class="bf-fade-in">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <div>
                  <h4 class="text-white font-bold mb-1">Marketplace storefronts</h4>
                  <p class="text-muted small">Verify and register multi-vendor merchants configurations</p>
                </div>
                <span class="badge bg-primary bg-opacity-25 text-cyan font-bold px-3 py-2 fs-7">{{ stores.length }} Storefronts</span>
              </div>

              <div class="table-responsive" v-if="stores.length > 0">
                <table class="table table-borderless align-middle text-white mb-0">
                  <thead>
                    <tr class="border-bottom border-secondary border-opacity-10 text-muted small text-uppercase">
                      <th class="py-3">Store profile</th>
                      <th class="py-3">Owner Account</th>
                      <th class="py-3">Rating stats</th>
                      <th class="py-3">Followers count</th>
                      <th class="py-3">Verification status</th>
                      <th class="py-3 text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="store in stores" :key="store.storeId" class="border-bottom border-secondary border-opacity-10">
                      <td class="py-3">
                        <div class="d-flex align-items-center gap-3">
                          <div class="bf-product-mini-img-wrapper bf-glass border-0">
                            <img :src="store.storeLogoUrl" class="bf-product-mini-img" alt="Logo" @error="handleImageError" />
                          </div>
                          <div class="text-start">
                            <h6 class="mb-1 font-bold text-white">
                              <router-link :to="'/store/' + store.storeId" class="text-decoration-none text-white hover-glow">
                                {{ store.storeName }}
                              </router-link>
                            </h6>
                            <small class="text-muted text-truncate d-inline-block" style="max-width: 250px;">{{ store.storeDescription }}</small>
                          </div>
                        </div>
                      </td>
                      <td class="py-3">
                        <div class="font-bold text-white">{{ store.ownerName }}</div>
                        <small class="text-muted">UID #{{ store.ownerId }}</small>
                      </td>
                      <td class="py-3">
                        <div class="text-warning font-bold"><i class="bi bi-star-fill me-1"></i>{{ store.rating }}</div>
                        <small class="text-muted">{{ store.reviewsCount }} reviews</small>
                      </td>
                      <td class="font-bold text-white py-3">{{ store.followersCount }}</td>
                      <td class="py-3">
                        <span class="badge" :class="store.isVerified ? 'bg-success bg-opacity-25 text-success' : 'bg-warning bg-opacity-25 text-warning'">
                          {{ store.isVerified ? 'Verified' : 'Pending Review' }}
                        </span>
                      </td>
                      <td class="text-end py-3">
                        <button
                          class="bf-btn-ghost py-1.5 px-3 small"
                          :class="store.isVerified ? 'border-danger border-opacity-25 text-danger' : 'border-cyan border-opacity-25 text-cyan'"
                          @click="toggleStoreVerify(store.storeId)"
                        >
                          {{ store.isVerified ? 'Revoke Status' : 'Grant Verified' }}
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div v-else class="text-center py-5 text-muted small">
                No stores registered in the marketplace system coordinates yet.
              </div>
            </div>

            <!-- ─── TAB 6: SUPPORT MESSAGES ─── -->
            <div v-if="activeTab === 'messages'" class="bf-fade-in">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
                <div>
                  <h4 class="text-white font-bold mb-1">Support Desk Tickets</h4>
                  <p class="text-muted small">Review customer files and dispatch reply responses</p>
                </div>
                <span class="badge bg-danger bg-opacity-25 text-danger font-bold px-3 py-2 fs-7" v-if="openMessagesCount > 0">
                  {{ openMessagesCount }} Open Ticket(s)
                </span>
              </div>

              <div v-if="messages.length === 0" class="text-center py-5 text-muted">
                <p class="mb-0">No active customer tickets registered.</p>
              </div>

              <div v-else>
                <div v-for="msg in messages" :key="msg.messageId" class="bf-glass p-4 mb-4 border-secondary border-opacity-25">
                  <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3 pb-2 border-bottom border-secondary border-opacity-10 text-start">
                    <div class="d-flex align-items-center gap-2">
                      <span class="badge" :class="msg.status === 'OPEN' ? 'bg-warning bg-opacity-25 text-warning' : 'bg-success bg-opacity-25 text-success'">
                        {{ msg.status }}
                      </span>
                      <strong class="h6 text-white mb-0">{{ msg.subject }}</strong>
                    </div>
                    <div class="small text-muted">
                      Launched: {{ formatDate(msg.createdDate) }}
                    </div>
                  </div>

                  <p class="text-white text-start mb-3"><strong>Issue:</strong> {{ msg.content }}</p>

                  <div class="bg-black bg-opacity-20 p-3 rounded mb-3 text-start">
                    <p class="small text-muted mb-1">
                      <strong>Client Details:</strong> {{ msg.user ? msg.user.fullName : 'Guest' }} 
                      (@{{ msg.user ? msg.user.username : 'guest' }} | {{ msg.user ? msg.user.email : 'N/A' }})
                    </p>
                    <p class="small text-muted mb-0"><strong>Phone connection:</strong> {{ msg.user && msg.user.phoneNumber ? msg.user.phoneNumber : 'Not provided' }}</p>
                  </div>

                  <!-- Reply Input -->
                  <div v-if="msg.status === 'OPEN'" class="mt-3 text-start">
                    <div class="mb-3">
                      <label class="form-label small font-bold text-muted mb-2">Reply response</label>
                      <textarea class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" rows="3" v-model="msg.replyInput" placeholder="Write response message coordinates here..."></textarea>
                    </div>
                    <button class="bf-btn-premium border-0 py-2 px-4" @click="submitMessageReply(msg)">
                      Send Response
                    </button>
                  </div>

                  <!-- Replied View -->
                  <div v-else class="mt-3 border-top border-secondary border-opacity-10 pt-3 text-start">
                    <h6 class="text-success font-bold mb-2">Response Logged:</h6>
                    <p class="small text-secondary mb-0 bg-black bg-opacity-30 p-3 rounded border border-secondary border-opacity-10">{{ msg.reply }}</p>
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
import LoadingSkeleton from '../../components/Common/LoadingSkeleton.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';
import { getToken } from '../../utils/auth';

export default {
  name: 'AdminDashboard',
  components: { LoadingSkeleton },
  data() {
    return {
      activeTab: 'deliveries',
      loading: true,
      error: null,
      orders: [],
      users: [],
      products: [],
      categories: [],
      messages: [],
      stores: [],
      tabItems: [
        { id: 'deliveries', label: 'Deliveries', icon: '📦', badgeCount: 0 },
        { id: 'users', label: 'Users', icon: '👥', badgeCount: 0 },
        { id: 'products', label: 'Inventory', icon: '🔌', badgeCount: 0 },
        { id: 'categories', label: 'Categories', icon: '📂', badgeCount: 0 },
        { id: 'stores', label: 'Stores', icon: '🏢', badgeCount: 0 },
        { id: 'messages', label: 'Support Desk', icon: '✉️', badgeCount: 0 }
      ]
    };
  },
  computed: {
    openMessagesCount() {
      return this.messages.filter(m => m.status === 'OPEN').length;
    }
  },
  watch: {
    openMessagesCount(newCount) {
      const msgTab = this.tabItems.find(t => t.id === 'messages');
      if (msgTab) msgTab.badgeCount = newCount;
    }
  },
  methods: {
    formatPrice(val) { return formatPrice(val); },
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
    getStatusColorStyle(status) {
      if (status === 'Pending') return { color: 'var(--bf-warning)', borderColor: 'rgba(245, 158, 11, 0.3)' };
      if (status === 'Shipped') return { color: 'var(--bf-info)', borderColor: 'rgba(6, 182, 212, 0.3)' };
      if (status === 'Delivered') return { color: 'var(--bf-success)', borderColor: 'rgba(16, 185, 129, 0.3)' };
      if (status === 'Cancelled') return { color: 'var(--bf-danger)', borderColor: 'rgba(239, 68, 68, 0.3)' };
      return {};
    },
    getRoleBadgeClass(role) {
      if (role === 'ADMIN') return 'bg-danger bg-opacity-25 text-danger';
      if (role === 'SELLER') return 'bg-warning bg-opacity-25 text-warning';
      return 'bg-primary bg-opacity-25 text-primary';
    },
    async fetchData() {
      this.loading = true;
      this.error = null;
      try {
        const [ordersRes, usersRes, prodRes, catRes, msgRes, storeRes] = await Promise.all([
          api.get('/order/all'),
          api.get('/user/all'),
          api.get('/product'),
          api.get('/category'),
          api.get('/message/all'),
          api.get('/store/all')
        ]);
        this.orders = ordersRes.data.data || [];
        this.users = usersRes.data.data || [];
        this.products = prodRes.data.data || [];
        this.categories = catRes.data.data || [];
        this.messages = (msgRes.data.data || []).map(m => ({
          ...m,
          replyInput: ''
        }));
        this.stores = storeRes.data.data || [];
      } catch (err) {
        this.error = extractErrorMessage(err);
        showToast({
          message: this.error,
          type: 'error',
          title: 'Sync Error'
        });
      } finally {
        this.loading = false;
      }
    },
    async handleStatusChange(orderId, status) {
      try {
        await api.put(`/order/${orderId}/status?status=${status}`);
        showToast({
          message: `Order #${orderId} status set to ${status}`,
          type: 'success',
          title: 'Order Updated'
        });
      } catch (err) {
        showToast({
          message: 'Failed to update status: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
        await this.fetchData();
      }
    },
    async handleDeleteProduct(productId) {
      if (!confirm('Are you sure you want to delete this product?')) return;
      try {
        await api.delete(`/product/${productId}`);
        showToast({
          message: 'Component removed from catalogue successfully',
          type: 'success',
          title: 'Component Deleted'
        });
        this.products = this.products.filter(p => p.productId !== productId);
      } catch (err) {
        showToast({
          message: 'Failed to delete product: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      }
    },
    async handleDeleteCategory(categoryId) {
      if (!confirm('Are you sure you want to delete this category? This will delete all products under it.')) return;
      try {
        await api.delete(`/category/${categoryId}`);
        showToast({
          message: 'Category removed successfully',
          type: 'success',
          title: 'Category Deleted'
        });
        this.categories = this.categories.filter(c => c.categoryId !== categoryId);
        const prodRes = await api.get('/product');
        this.products = prodRes.data.data || [];
      } catch (err) {
        showToast({
          message: 'Failed to delete category: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      }
    },
    async toggleStoreVerify(storeId) {
      const token = getToken();
      if (!token) return;
      try {
        const response = await api.put(`/store/${storeId}/verify?token=${token}`);
        showToast({
          message: response.data.message,
          type: 'success',
          title: 'Store Verification Updated'
        });
        const updatedStore = response.data.data;
        const index = this.stores.findIndex(s => s.storeId === storeId);
        if (index !== -1) {
          this.stores[index] = updatedStore;
        }
      } catch (err) {
        showToast({
          message: 'Failed to update store verification: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      }
    },
    async submitMessageReply(msg) {
      if (!msg.replyInput.trim()) {
        showToast({ message: 'Please enter a reply response.', type: 'warning' });
        return;
      }
      try {
        await api.post(`/message/${msg.messageId}/reply?reply=${encodeURIComponent(msg.replyInput)}`);
        showToast({
          message: 'Response sent successfully to customer email',
          type: 'success',
          title: 'Ticket Replied'
        });
        msg.reply = msg.replyInput;
        msg.status = 'REPLIED';
        msg.replyInput = '';
      } catch (err) {
        showToast({
          message: 'Failed to send reply: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      }
    },
    handleImageError(event) {
      event.target.src = 'https://images.unsplash.com/photo-1587831990711-23ca6441447b?auto=format&fit=crop&w=100&q=80';
    }
  },
  mounted() {
    this.fetchData();
  }
};
</script>

<style scoped>
.glow-bg {
  position: absolute;
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(239, 68, 68, 0.05) 0%, transparent 75%);
  top: -100px; right: -100px;
  pointer-events: none;
  z-index: 1;
}

.bf-tab-btn-cyber {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  border: none;
  background: transparent;
  color: var(--bf-text-muted);
  cursor: pointer;
  font-weight: 700;
  font-size: 0.82rem;
  letter-spacing: 0.5px;
  transition: all var(--bf-transition-fast);
  white-space: nowrap;
  position: relative;
}

.bf-tab-btn-cyber:hover {
  background: rgba(255, 255, 255, 0.03);
  color: #fff;
}

.bf-tab-btn-cyber.active {
  background: rgba(26, 34, 56, 0.6);
  color: var(--bf-cyan);
}

.bf-tab-btn-cyber.active::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 2px;
  background: var(--bf-cyan);
  box-shadow: 0 0 10px var(--bf-cyan);
}

.bf-product-mini-img-wrapper {
  width: 48px; height: 48px;
  border-radius: 8px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}

.bf-product-mini-img {
  width: 100%; height: 100%;
  object-fit: cover;
}
</style>
