<template>
  <div class="bf-page bf-fade-in p-4 text-start">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-danger mb-2">Control Center</span>
          <h2 class="text-white font-weight-bold mb-1">Admin Operations Console</h2>
          <p class="text-muted small">Manage products, categories, registered users, deliveries, and support tickets for ByteForge.</p>
        </div>
      </div>

      <!-- Dashboard Tabs Grid Navigation -->
      <div class="bf-card bf-glass overflow-hidden mb-4 border-light">
        <div class="bf-tabs-nav bg-navbar-custom border-light-bottom">
          <button
            v-for="tab in tabItems"
            :key="tab.id"
            class="bf-tab-btn-cyber"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <span class="bf-tab-icon">{{ tab.icon }}</span>
            <span class="bf-tab-text">{{ tab.label }}</span>
            <span v-if="tab.badgeCount && tab.badgeCount > 0" class="bf-tab-badge bf-pulse">
              {{ tab.badgeCount }}
            </span>
          </button>
        </div>

        <div class="p-4 text-start text-white">
          <!-- Loading skeleton -->
          <div v-if="loading" class="py-5">
            <LoadingSkeleton type="table" :lines="6" />
          </div>

          <!-- Error Alert -->
          <div v-else-if="error" class="bf-empty-state text-center">
            <div class="bf-empty-icon text-danger">⚠</div>
            <h5 class="text-white font-weight-bold">Operation Failure</h5>
            <p>{{ error }}</p>
            <button class="bf-btn bf-btn-primary" @click="fetchData">Retry Sync</button>
          </div>

          <div v-else>
            <!-- ─── TAB 1: DELIVERIES / ORDERS ─── -->
            <div v-if="activeTab === 'deliveries'">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                  <h4 class="font-weight-bold text-white mb-1">Marketplace Deliveries</h4>
                  <p class="text-secondary small mb-0">Track and dispatch customer hardware packages</p>
                </div>
                <span class="bf-badge bf-badge-primary font-weight-bold">{{ orders.length }} Total Order(s)</span>
              </div>

              <div v-if="orders.length === 0" class="text-center py-5 text-muted">
                <p class="mb-0">No active customer orders found.</p>
              </div>

              <div v-else class="table-responsive">
                <table class="bf-table text-white">
                  <thead>
                    <tr>
                      <th class="text-white">Order ID</th>
                      <th class="text-white">Customer</th>
                      <th class="text-white">Date</th>
                      <th class="text-white">Components</th>
                      <th class="text-white">Total</th>
                      <th class="text-white">Address</th>
                      <th class="text-white">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="order in orders" :key="order.orderId" class="border-light-bottom">
                      <td class="font-weight-bold text-white">#{{ order.orderId }}</td>
                      <td>
                        <div class="font-weight-bold text-white">{{ order.user ? order.user.fullName : 'Guest' }}</div>
                        <small class="text-muted">@{{ order.user ? order.user.username : 'guest' }}</small>
                      </td>
                      <td class="small">{{ formatDate(order.createdDate) }}</td>
                      <td>
                        <ul class="list-unstyled mb-0 small text-secondary">
                          <li v-for="item in order.orderItems" :key="item.orderItemId">
                            ⚙️ {{ item.product.productName }} <span class="text-white font-weight-bold">(x{{ item.quantity }})</span>
                          </li>
                        </ul>
                      </td>
                      <td class="font-weight-bold text-primary">{{ formatPrice(order.totalPrice) }}</td>
                      <td class="small text-secondary">{{ order.address }}</td>
                      <td>
                        <select
                          class="bf-input py-1 px-2 text-capitalize font-weight-bold bg-dark text-white border-light text-sm"
                          :style="getStatusColorStyle(order.status)"
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

            <!-- ─── TAB 2: REGISTERED USERS ─── -->
            <div v-if="activeTab === 'users'">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                  <h4 class="font-weight-bold text-white mb-1">User Directory</h4>
                  <p class="text-secondary small mb-0">Registered admin, seller, and buyer accounts</p>
                </div>
                <span class="bf-badge bf-badge-primary">{{ users.length }} Active Account(s)</span>
              </div>

              <div class="table-responsive">
                <table class="bf-table text-white">
                  <thead>
                    <tr>
                      <th class="text-white">UID</th>
                      <th class="text-white">Name</th>
                      <th class="text-white">Username</th>
                      <th class="text-white">Email Address</th>
                      <th class="text-white">Phone Number</th>
                      <th class="text-white">Role Privilege</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="u in users" :key="u.userId" class="border-light-bottom">
                      <td>#{{ u.userId }}</td>
                      <td class="font-weight-bold text-white">{{ u.fullName || 'No Name' }}</td>
                      <td>@{{ u.username }}</td>
                      <td>{{ u.email }}</td>
                      <td>{{ u.phoneNumber || 'Not Provided' }}</td>
                      <td>
                        <span class="bf-badge" :class="getRoleBadgeClass(u.role)">{{ u.role }}</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- ─── TAB 3: PRODUCTS ─── -->
            <div v-if="activeTab === 'products'">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                  <h4 class="font-weight-bold text-white mb-1">Marketplace Inventory</h4>
                  <p class="text-secondary small mb-0">Manage listed graphics cards, processors, storage, and configurations</p>
                </div>
                <router-link :to="{ name: 'AddProduct' }" class="bf-btn bf-btn-primary">
                  List New Component
                </router-link>
              </div>

              <div class="table-responsive">
                <table class="bf-table align-middle text-white">
                  <thead>
                    <tr>
                      <th class="text-white">Product Spec</th>
                      <th class="text-white">Price Rate</th>
                      <th class="text-white">Stock Level</th>
                      <th class="text-white text-end">Operations</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="p in products" :key="p.productId" class="border-light-bottom">
                      <td>
                        <div class="d-flex align-items-center gap-3">
                          <div class="bf-product-mini-img-wrapper border-light">
                            <img :src="p.imageUrl" class="bf-product-mini-img" alt="Product Thumbnail" />
                          </div>
                          <div>
                            <h6 class="mb-0 font-weight-bold text-white">{{ p.productName }}</h6>
                            <small class="text-muted" v-if="p.category">{{ p.category.categoryName }}</small>
                          </div>
                        </div>
                      </td>
                      <td class="font-weight-bold text-primary">{{ formatPrice(p.price) }}</td>
                      <td>
                        <span class="bf-badge" :class="p.stock > 0 ? 'bf-badge-success' : 'bf-badge-danger'">
                          {{ p.stock > 0 ? p.stock + ' in stock' : 'Out of stock' }}
                        </span>
                      </td>
                      <td class="text-end">
                        <div class="d-inline-flex gap-2">
                          <router-link :to="{ name: 'EditProduct', params: { id: p.productId } }" class="bf-btn bf-btn-ghost bf-btn-sm text-primary">
                            Edit
                          </router-link>
                          <button class="bf-btn bf-btn-danger bf-btn-sm" @click="handleDeleteProduct(p.productId)">
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
            <div v-if="activeTab === 'categories'">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                  <h4 class="font-weight-bold text-white mb-1">Catalog Categories</h4>
                  <p class="text-secondary small mb-0">Organize motherboard types, CPU sockets, memory, and cases</p>
                </div>
                <router-link :to="{ name: 'AddCategory' }" class="bf-btn bf-btn-primary">
                  Create Category
                </router-link>
              </div>

              <div class="table-responsive">
                <table class="bf-table align-middle text-white">
                  <thead>
                    <tr>
                      <th class="text-white">Category Name</th>
                      <th class="text-white">Description</th>
                      <th class="text-white text-end">Operations</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="c in categories" :key="c.categoryId" class="border-light-bottom">
                      <td class="font-weight-bold text-white">{{ c.categoryName }}</td>
                      <td class="small text-secondary">{{ c.description }}</td>
                      <td class="text-end">
                        <div class="d-inline-flex gap-2">
                          <router-link :to="{ name: 'EditCategory', params: { id: c.categoryId } }" class="bf-btn bf-btn-ghost bf-btn-sm text-primary">
                            Edit
                          </router-link>
                          <button class="bf-btn bf-btn-danger bf-btn-sm" @click="handleDeleteCategory(c.categoryId)">
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
            <div v-if="activeTab === 'stores'">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                  <h4 class="font-weight-bold text-white mb-1">Marketplace Stores & Vendors</h4>
                  <p class="text-secondary small mb-0">Verify and manage multi-vendor storefront credentials</p>
                </div>
                <span class="bf-badge bf-badge-primary">{{ stores.length }} Storefronts</span>
              </div>

              <div class="table-responsive" v-if="stores.length > 0">
                <table class="bf-table align-middle text-white">
                  <thead>
                    <tr>
                      <th class="text-white">Store Profile</th>
                      <th class="text-white">Owner Account</th>
                      <th class="text-white">Rating & Reviews</th>
                      <th class="text-white">Followers</th>
                      <th class="text-white">Verification</th>
                      <th class="text-white text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="store in stores" :key="store.storeId" class="border-light-bottom">
                      <td>
                        <div class="d-flex align-items-center gap-3">
                          <div class="bf-product-mini-img-wrapper border-light">
                            <img :src="store.storeLogoUrl || 'https://placehold.co/100x100?text=Store'" class="bf-product-mini-img" alt="Logo" />
                          </div>
                          <div>
                            <h6 class="mb-0 font-weight-bold text-white">
                              <router-link :to="'/store/' + store.storeId" class="text-decoration-none text-white">
                                {{ store.storeName }}
                              </router-link>
                            </h6>
                            <small class="text-muted text-truncate d-inline-block" style="max-width: 250px;">{{ store.storeDescription }}</small>
                          </div>
                        </div>
                      </td>
                      <td>
                        <div class="font-weight-bold text-white">{{ store.ownerName }}</div>
                        <small class="text-muted">UID #{{ store.ownerId }}</small>
                      </td>
                      <td>
                        <div class="text-warning font-weight-bold">{{ store.rating }} ★</div>
                        <small class="text-muted">{{ store.reviewsCount }} reviews</small>
                      </td>
                      <td class="font-weight-bold text-white">{{ store.followersCount }}</td>
                      <td>
                        <span class="bf-badge" :class="store.isVerified ? 'bf-badge-success' : 'bf-badge-warning'">
                          {{ store.isVerified ? 'Verified' : 'Pending Review' }}
                        </span>
                      </td>
                      <td class="text-end">
                        <button
                          class="bf-btn bf-btn-sm"
                          :class="store.isVerified ? 'bf-btn-outline' : 'bf-btn-primary'"
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
                No stores registered in the marketplace yet.
              </div>
            </div>

            <!-- ─── TAB 6: SUPPORT MESSAGES ─── -->
            <div v-if="activeTab === 'messages'">
              <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                <div>
                  <h4 class="font-weight-bold text-white mb-1">Support Resolution Center</h4>
                  <p class="text-secondary small mb-0">Reply to customer issues and system messages</p>
                </div>
                <span class="bf-badge bf-badge-danger" v-if="openMessagesCount > 0">
                  {{ openMessagesCount }} Open Ticket(s)
                </span>
              </div>

              <div v-if="messages.length === 0" class="text-center py-5 text-muted">
                <p class="mb-0">No support tickets found.</p>
              </div>

              <div v-else>
                <div v-for="msg in messages" :key="msg.messageId" class="bf-card bg-dark border-light p-4 mb-4 text-white">
                  <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3 border-light-bottom pb-2">
                    <div class="d-flex align-items-center gap-2">
                      <span class="bf-badge" :class="msg.status === 'OPEN' ? 'bf-badge-warning' : 'bf-badge-success'">
                        {{ msg.status }}
                      </span>
                      <strong class="h5 text-white mb-0">{{ msg.subject }}</strong>
                    </div>
                    <div class="small text-muted">
                      Created: {{ formatDate(msg.createdDate) }}
                    </div>
                  </div>

                  <p class="text-white mb-3"><strong>Description:</strong> {{ msg.content }}</p>

                  <div class="bg-secondary-dark p-3 rounded mb-3">
                    <p class="small text-muted mb-1">
                      <strong>Submitted By:</strong> {{ msg.user ? msg.user.fullName : 'Guest' }} 
                      (@{{ msg.user ? msg.user.username : 'guest' }} | {{ msg.user ? msg.user.email : 'N/A' }})
                    </p>
                    <p class="small text-muted mb-0"><strong>Phone:</strong> {{ msg.user && msg.user.phoneNumber ? msg.user.phoneNumber : 'Not provided' }}</p>
                  </div>

                  <!-- Reply Form -->
                  <div v-if="msg.status === 'OPEN'" class="mt-3">
                    <div class="mb-3">
                      <label class="form-label small font-weight-bold text-muted">Reply Response</label>
                      <textarea class="bf-input bg-dark text-white border-light" rows="3" v-model="msg.replyInput" placeholder="Write your response message here..."></textarea>
                    </div>
                    <button class="bf-btn bf-btn-primary bf-btn-sm" @click="submitMessageReply(msg)">
                      Send Response
                    </button>
                  </div>

                  <!-- Replied View -->
                  <div v-else class="mt-3 border-light-top pt-3 text-start">
                    <h6 class="text-success font-weight-bold mb-1">Admin Response Logged:</h6>
                    <p class="small text-white mb-0 bg-secondary-dark p-3 rounded border border-secondary">{{ msg.reply }}</p>
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
        { id: 'deliveries', label: 'Deliveries / Orders', icon: '📦', badgeCount: 0 },
        { id: 'users', label: 'Registered Users', icon: '👥', badgeCount: 0 },
        { id: 'products', label: 'Products', icon: '🔌', badgeCount: 0 },
        { id: 'categories', label: 'Categories', icon: '📂', badgeCount: 0 },
        { id: 'stores', label: 'Stores', icon: '🏢', badgeCount: 0 },
        { id: 'messages', label: 'Support Messages', icon: '✉️', badgeCount: 0 }
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
    formatPrice(val) {
      return formatPrice(val);
    },
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
      if (status === 'Pending') return { borderColor: 'var(--bf-warning)', color: 'var(--bf-warning)' };
      if (status === 'Shipped') return { borderColor: 'var(--bf-info)', color: 'var(--bf-info)' };
      if (status === 'Delivered') return { borderColor: 'var(--bf-success)', color: 'var(--bf-success)' };
      if (status === 'Cancelled') return { borderColor: 'var(--bf-danger)', color: 'var(--bf-danger)' };
      return {};
    },
    getRoleBadgeClass(role) {
      if (role === 'ADMIN') return 'bf-badge-danger';
      if (role === 'SELLER') return 'bf-badge-warning';
      return 'bf-badge-primary';
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
        await this.fetchData(); // reload to reset UI
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
        // Refresh products list
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
        // Update local state
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
        showToast({
          message: 'Please enter a reply response.',
          type: 'warning',
          title: 'Input Required'
        });
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
    }
  },
  mounted() {
    this.fetchData();
  }
};
</script>

<style scoped>
.bg-navbar-custom {
  background: rgba(11, 15, 25, 0.95);
}

.bf-tabs-nav {
  display: flex;
  overflow-x: auto;
}

.bf-tab-btn-cyber {
  flex: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 18px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  font-family: var(--bf-font-family);
  font-weight: 700;
  font-size: var(--bf-font-size-sm);
  transition: all var(--bf-transition-fast);
  white-space: nowrap;
  position: relative;
}

.bf-tab-btn-cyber:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.bf-tab-btn-cyber.active {
  background: rgba(26, 34, 56, 0.85);
  color: var(--bf-primary);
}

.bf-tab-btn-cyber.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--bf-primary);
  box-shadow: 0 0 8px var(--bf-primary-glow);
}

.bf-tab-badge {
  background: var(--bf-danger);
  color: white;
  font-size: 0.65rem;
  padding: 2px 6px;
  border-radius: var(--bf-radius-full);
}

.bf-product-mini-img-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--bf-radius-md);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: var(--bf-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.bf-product-mini-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.bg-secondary-dark {
  background: rgba(255, 255, 255, 0.02);
}

.border-light-bottom {
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.border-light-top {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}
</style>
