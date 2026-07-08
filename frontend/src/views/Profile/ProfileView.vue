<template>
  <div class="bf-page bf-fade-in py-5 text-start">
    <div class="container position-relative">
      <div class="glow-bg animate-float"></div>

      <!-- Header -->
      <div class="row mb-5 position-relative z-2">
        <div class="col-12 text-start">
          <span class="badge bg-primary bg-opacity-25 text-cyan px-3 py-2 mb-2 rounded-pill text-uppercase">
            <i class="bi bi-person-fill me-1"></i> Account Command Center
          </span>
          <h1 class="text-white font-bold mb-1 fs-2">My Account Overview</h1>
          <p class="text-muted small">Update profile credentials, inspect your order pipelines, and contact system admin.</p>
        </div>
      </div>

      <div class="row g-4 position-relative z-2">
        <!-- Profile Details Column (Left) -->
        <div class="col-lg-4">
          <div class="bf-glass-card h-100 border-0 p-4 d-flex flex-column justify-content-between">
            <div>
              <!-- Header tag -->
              <div class="text-center mb-4">
                <span class="badge bg-primary bg-opacity-15 text-cyan text-uppercase px-3 py-2">
                  🛡️ {{ user ? user.role : 'Guest' }} MEMBER
                </span>
              </div>

              <!-- Avatar & Details -->
              <div class="p-2 text-center">
                <div v-if="loading" class="py-4">
                  <div class="bf-skeleton rounded-circle mx-auto" style="width: 120px; height: 120px;"></div>
                </div>

                <div v-else-if="error" class="text-center py-3">
                  <p class="text-danger small mb-0">{{ error }}</p>
                </div>

                <div v-else-if="user" class="bf-fade-in">
                  <!-- Avatar with Upload Overlay -->
                  <div class="avatar-container mb-4 position-relative mx-auto rounded-circle bf-glass p-1">
                    <img
                      :src="user.profileImageUrl || defaultAvatar"
                      class="rounded-circle avatar-image"
                      alt="Profile Avatar"
                      @error="handleAvatarError"
                    />
                    <!-- Camera Upload Overlay -->
                    <div class="avatar-upload-overlay rounded-circle d-flex align-items-center justify-content-center cursor-pointer" @click="triggerFileInput">
                      <i class="bi bi-camera-fill text-white fs-4"></i>
                    </div>
                    <!-- Hidden input -->
                    <input
                      type="file"
                      ref="fileInput"
                      class="d-none"
                      accept="image/*"
                      @change="handleAvatarUpload"
                    />
                  </div>

                  <!-- Uploading spinner -->
                  <div v-if="uploading" class="spinner-border spinner-border-sm text-cyan mb-3 d-block mx-auto" role="status"></div>

                  <!-- User headers -->
                  <h5 class="text-white font-bold mb-1 fs-5">{{ user.fullName || 'Enthusiast Member' }}</h5>
                  <p class="text-muted small mb-4">@{{ user.username }}</p>

                  <hr class="border-secondary border-opacity-25 my-4" />

                  <!-- Profile Attributes display -->
                  <div class="text-start">
                    <!-- Read Mode -->
                    <div v-if="!editMode" class="d-flex flex-column gap-3">
                      <div>
                        <span class="text-muted d-block small mb-1">Full Name</span>
                        <strong class="text-white">{{ user.fullName || 'N/A' }}</strong>
                      </div>
                      <div>
                        <span class="text-muted d-block small mb-1">Email Address</span>
                        <strong class="text-white text-break">{{ user.email }}</strong>
                      </div>
                      <div>
                        <span class="text-muted d-block small mb-1">Phone Connection</span>
                        <strong class="text-white">{{ user.phoneNumber || 'Not provided' }}</strong>
                      </div>
                      <div>
                        <span class="text-muted d-block small mb-1">Shipping Destination</span>
                        <strong class="text-white">{{ user.address || 'No address registered' }}</strong>
                      </div>

                      <button class="bf-btn-ghost w-100 mt-3 border-secondary border-opacity-20" @click="toggleEdit">
                        <i class="bi bi-pencil-square me-1"></i> Edit Account details
                      </button>

                      <!-- Become seller banner -->
                      <div v-if="user && user.role === 'USER'" class="mt-4 p-3 bf-glass border-0 bg-opacity-25 bg-black">
                        <h6 class="text-white font-bold mb-2"><i class="bi bi-lightbulb-fill text-warning me-1"></i> Start Earning</h6>
                        <p class="small text-muted mb-3">Sell high-quality components on the ByteForge tech marketplace.</p>
                        <router-link to="/seller/store-setup" class="bf-btn-premium w-100 py-2 border-0 text-center text-decoration-none d-block">
                          Become a Seller
                        </router-link>
                      </div>
                    </div>

                    <!-- Edit Mode -->
                    <div v-else>
                      <form @submit.prevent="saveProfile">
                        <div class="mb-3">
                          <label class="form-label small text-muted font-bold mb-2">Full Name</label>
                          <input type="text" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="editForm.fullName" required />
                        </div>

                        <div class="mb-3">
                          <label class="form-label small text-muted font-bold mb-2">Email Address</label>
                          <input type="email" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="editForm.email" required />
                        </div>

                        <div class="mb-3">
                          <label class="form-label small text-muted font-bold mb-2">Phone Number</label>
                          <input type="text" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="editForm.phoneNumber" placeholder="e.g. +94 77 123 4567" />
                        </div>

                        <div class="mb-3">
                          <label class="form-label small text-muted font-bold mb-2">Shipping Destination</label>
                          <textarea class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" rows="3" v-model="editForm.address" placeholder="Enter delivery coordinates..."></textarea>
                        </div>

                        <div class="d-flex gap-2 mt-4">
                          <button type="submit" class="bf-btn-premium border-0 py-2.5 flex-grow-1" :disabled="saving">
                            <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
                            Save Profile
                          </button>
                          <button type="button" class="bf-btn-ghost border-0 text-muted" @click="toggleEdit" :disabled="saving">
                            Cancel
                          </button>
                        </div>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Orders & Support Columns (Right) -->
        <div class="col-lg-8 d-flex flex-column gap-4">
          <!-- Order History Panel -->
          <div class="bf-glass-card border-0 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
              <h5 class="text-white font-bold mb-0">Order Pipeline Logs</h5>
              <span class="badge bg-primary bg-opacity-25 text-cyan" v-if="!ordersLoading && !ordersError">
                {{ orders.length }} Order(s)
              </span>
            </div>

            <!-- Scrollable orders list -->
            <div style="max-height: 420px; overflow-y: auto; padding-right: 4px;">
              <div v-if="ordersLoading" class="py-5 text-center">
                <span class="spinner-border spinner-border-sm text-cyan"></span>
              </div>

              <div v-else-if="ordersError" class="text-center py-3">
                <p class="text-danger small mb-0">{{ ordersError }}</p>
              </div>

              <div v-else-if="orders.length === 0" class="text-center py-5">
                <p class="text-muted small mb-3">No marketplace orders registered under your profile yet.</p>
                <router-link to="/product" class="bf-btn-premium border-0 py-2 px-4 text-decoration-none">Configure a Build</router-link>
              </div>

              <!-- Order entries -->
              <div v-else class="d-flex flex-column gap-3">
                <div v-for="order in orders" :key="order.orderId" class="bf-glass border-secondary border-opacity-25 overflow-hidden">
                  <!-- Header details -->
                  <div class="p-3 bg-black bg-opacity-20 border-bottom border-secondary border-opacity-10 d-flex flex-wrap justify-content-between align-items-center gap-3">
                    <div class="text-start">
                      <span class="text-muted d-block small mb-1">DATE LAUNCHED</span>
                      <strong class="text-white small">{{ formatDate(order.createdDate) }}</strong>
                    </div>
                    <div class="text-start">
                      <span class="text-muted d-block small mb-1">SHIPPING DESTINATION</span>
                      <strong class="text-white small text-truncate d-inline-block" style="max-width: 180px;" :title="order.address">
                        {{ order.address }}
                      </strong>
                    </div>
                    <div class="text-start">
                      <span class="text-muted d-block small mb-1">TRANSACTION METHOD</span>
                      <span class="badge bg-success bg-opacity-25 text-success">{{ formatPaymentMethod(order.paymentMethod) }}</span>
                    </div>
                    <div class="text-end">
                      <span class="text-muted d-block small mb-1">TOTAL AMOUNT</span>
                      <strong class="text-gradient-primary font-bold">{{ formatPrice(order.totalPrice) }}</strong>
                    </div>
                  </div>

                  <!-- Inner item listing -->
                  <div class="p-0">
                    <div
                      v-for="item in order.orderItems"
                      :key="item.orderItemId"
                      class="d-flex align-items-center p-3 border-bottom border-secondary border-opacity-10 last-border-0"
                    >
                      <img
                        :src="item.product.imageUrl"
                        class="rounded border border-secondary border-opacity-25 me-3"
                        style="width: 48px; height: 48px; object-fit: cover;"
                        alt="Product Image"
                      />
                      <div class="text-start flex-grow-1">
                        <h6 class="text-white mb-1 font-semibold small">{{ item.product.productName }}</h6>
                        <small class="text-muted">Quantity: {{ item.quantity }}</small>
                      </div>
                      <div class="text-end">
                        <strong class="text-white small">{{ formatPrice(item.price * item.quantity) }}</strong>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Support Desk Resolution Center -->
          <div class="bf-glass-card border-0 p-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
              <h5 class="text-white font-bold mb-0">Support Resolution Desk</h5>
              <button class="bf-btn-premium border-0 py-2 px-3 small" @click="showTicketForm = !showTicketForm">
                {{ showTicketForm ? 'Close Console' : 'Open Ticket' }}
              </button>
            </div>

            <!-- Complaint Filing Console -->
            <transition name="page-fade">
              <div v-if="showTicketForm" class="bf-glass p-4 border-secondary border-opacity-25 mb-4 text-start">
                <h6 class="text-white font-bold mb-3 d-flex align-items-center gap-2">
                  <i class="bi bi-ticket-perforated-fill text-cyan"></i> File Support Ticket Request
                </h6>
                <form @submit.prevent="sendTicket">
                  <div class="mb-3">
                    <label class="form-label small text-muted font-bold mb-2">Subject</label>
                    <input type="text" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="ticketSubject" placeholder="e.g., Shipping delays / Hardware anomaly details" required />
                  </div>
                  <div class="mb-3">
                    <label class="form-label small text-muted font-bold mb-2">Description Detail</label>
                    <textarea class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" rows="3" v-model="ticketContent" placeholder="Describe query coordinates in detail..." required></textarea>
                  </div>
                  <button type="submit" class="bf-btn-premium border-0 py-2.5 px-4" :disabled="sendingTicket">
                    <span v-if="sendingTicket" class="spinner-border spinner-border-sm me-1"></span>
                    Launch Ticket
                  </button>
                </form>
              </div>
            </transition>

            <!-- Support Ticket Logs -->
            <div style="max-height: 300px; overflow-y: auto;">
              <div v-if="messagesLoading" class="py-4 text-center">
                <span class="spinner-border spinner-border-sm text-cyan"></span>
              </div>

              <div v-else-if="messagesError" class="alert alert-danger py-2">
                {{ messagesError }}
              </div>

              <div v-else-if="messages.length === 0" class="text-center py-4 text-muted small">
                No support logs registered under your profile coordinates.
              </div>

              <!-- Ticket entry listings -->
              <div v-else class="d-flex flex-column gap-3">
                <div v-for="msg in messages" :key="msg.messageId" class="bf-glass border-secondary border-opacity-25 p-3 text-start">
                  <div class="d-flex justify-content-between align-items-center pb-2 border-bottom border-secondary border-opacity-10 mb-2 flex-wrap gap-2">
                    <div class="d-flex align-items-center gap-2">
                      <span class="badge" :class="msg.status === 'OPEN' ? 'bg-warning bg-opacity-25 text-warning' : 'bg-success bg-opacity-25 text-success'">
                        {{ msg.status }}
                      </span>
                      <strong class="text-white small">{{ msg.subject }}</strong>
                    </div>
                    <small class="text-muted small" style="font-size: 0.72rem;">{{ formatDate(msg.createdDate) }}</small>
                  </div>
                  
                  <p class="small text-muted mb-2"><strong>Issue:</strong> {{ msg.content }}</p>

                  <!-- Replied Box -->
                  <div v-if="msg.status === 'REPLIED'" class="bg-black bg-opacity-30 p-3 border-start border-success border-3 text-start mt-2">
                    <small class="font-bold text-success d-block mb-1">Response:</small>
                    <p class="small text-secondary mb-0">{{ msg.reply }}</p>
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
import { getCurrentUser, getToken } from '../../utils/auth';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'ProfileView',
  data() {
    return {
      user: null,
      loading: true,
      error: null,
      editMode: false,
      saving: false,
      editForm: { fullName: '', email: '', phoneNumber: '', address: '' },
      uploading: false,
      defaultAvatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80',
      messages: [],
      messagesLoading: true,
      messagesError: null,
      showTicketForm: false,
      ticketSubject: '',
      ticketContent: '',
      sendingTicket: false,
      orders: [],
      ordersLoading: true,
      ordersError: null
    };
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
    formatPrice(val) { return formatPrice(val); },
    handleAvatarError(e) { e.target.src = this.defaultAvatar; },
    triggerFileInput() { this.$refs.fileInput.click(); },
    async handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append('file', file);

      this.uploading = true;
      try {
        const uploadRes = await api.post('/file/upload', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        const localImageUrl = uploadRes.data.data;

        const updateRes = await api.put('/user/update', {
          userId: this.user.userId,
          fullName: this.user.fullName,
          email: this.user.email,
          phoneNumber: this.user.phoneNumber,
          address: this.user.address,
          profileImageUrl: localImageUrl
        });

        this.user = updateRes.data.data;
        showToast({
          message: 'Avatar uploaded and updated successfully!',
          type: 'success',
          title: 'Profile Updated'
        });
      } catch (err) {
        showToast({
          message: 'Failed to upload picture: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Upload Failed'
        });
      } finally {
        this.uploading = false;
      }
    },
    toggleEdit() {
      if (!this.editMode) {
        this.editForm.fullName = this.user.fullName || '';
        this.editForm.email = this.user.email || '';
        this.editForm.phoneNumber = this.user.phoneNumber || '';
        this.editForm.address = this.user.address || '';
      }
      this.editMode = !this.editMode;
    },
    async saveProfile() {
      this.saving = true;
      try {
        const updatePayload = {
          userId: this.user.userId,
          fullName: this.editForm.fullName,
          email: this.editForm.email,
          phoneNumber: this.editForm.phoneNumber,
          address: this.editForm.address,
          profileImageUrl: this.user.profileImageUrl
        };
        const response = await api.put('/user/update', updatePayload);
        this.user = response.data.data;
        this.editMode = false;
        showToast({
          message: 'Profile information updated successfully.',
          type: 'success',
          title: 'Profile Saved'
        });
      } catch (err) {
        showToast({
          message: 'Failed to save profile: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      } finally {
        this.saving = false;
      }
    },
    async fetchUserMessages() {
      const token = getToken();
      if (!token) {
        this.messagesLoading = false;
        return;
      }
      this.messagesLoading = true;
      try {
        const response = await api.get(`/message/user/${token}`);
        this.messages = response.data.data || [];
      } catch (err) {
        this.messagesError = extractErrorMessage(err);
      } finally {
        this.messagesLoading = false;
      }
    },
    async sendTicket() {
      const token = getToken();
      if (!token) return;
      if (!this.ticketSubject.trim() || !this.ticketContent.trim()) {
        showToast({ message: 'Please fill in all details fields.', type: 'warning' });
        return;
      }
      this.sendingTicket = true;
      try {
        await api.post(`/message/create?subject=${encodeURIComponent(this.ticketSubject)}&content=${encodeURIComponent(this.ticketContent)}&token=${token}`);
        showToast({
          message: 'Your support ticket has been submitted to administrator queue.',
          type: 'success',
          title: 'Ticket Submitted'
        });
        this.ticketSubject = '';
        this.ticketContent = '';
        this.showTicketForm = false;
        await this.fetchUserMessages();
      } catch (err) {
        showToast({
          message: 'Failed to send message: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      } finally {
        this.sendingTicket = false;
      }
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
      this.error = 'Session expired. Please log in.';
      this.loading = false;
      this.ordersLoading = false;
      this.messagesLoading = false;
      return;
    }

    try {
      const response = await api.get(`/user/profile/${currentUser.userId}`);
      this.user = response.data.data;
      await Promise.all([
        this.fetchOrderHistory(),
        this.fetchUserMessages()
      ]);

      // Prefill support ticket if requested by redirect (eBay contact seller)
      const prefill = localStorage.getItem('bf-prefill-ticket');
      if (prefill) {
        try {
          const { subject, content } = JSON.parse(prefill);
          this.ticketSubject = subject;
          this.ticketContent = content;
          this.showTicketForm = true;

          // Scroll to Support Desk section
          this.$nextTick(() => {
            const el = document.querySelector('.bf-glass-card:last-child');
            if (el) el.scrollIntoView({ behavior: 'smooth' });
          });
        } catch (e) {
          console.warn('Failed to parse prefilled ticket details:', e);
        }
        localStorage.removeItem('bf-prefill-ticket');
      }
    } catch (err) {
      this.error = extractErrorMessage(err);
    } finally {
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.glow-bg {
  position: absolute;
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.08) 0%, transparent 75%);
  bottom: -100px; left: -100px;
  pointer-events: none;
  z-index: 1;
}

.avatar-container {
  width: 128px; height: 128px;
}

.avatar-image {
  width: 120px; height: 120px;
  object-fit: cover;
  background: var(--bf-bg-secondary);
}

.avatar-upload-overlay {
  position: absolute;
  top: 4px; left: 4px; right: 4px; bottom: 4px;
  background: rgba(0, 0, 0, 0.55);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-container:hover .avatar-upload-overlay {
  opacity: 1;
}

.last-border-0:last-child {
  border-bottom: 0 !important;
}
</style>
