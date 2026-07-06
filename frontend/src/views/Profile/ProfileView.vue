<template>
  <div class="bf-page bf-fade-in p-4 text-start">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-primary mb-2">Customer Profile</span>
          <h2 class="text-white font-weight-bold mb-1">My Account & Overview</h2>
          <p class="text-muted small">Manage account information, check order statuses, and create support queries.</p>
        </div>
      </div>

      <div class="row g-4">
        <!-- Profile Details Column -->
        <div class="col-lg-4">
          <div class="bf-card bf-glass position-relative h-100 border-light text-white">
            <!-- Header -->
            <div class="p-4 text-center bg-dark border-light-bottom position-relative" style="border-top-left-radius: var(--bf-radius-lg); border-top-right-radius: var(--bf-radius-lg);">
              <h5 class="mb-0 font-weight-bold text-white">Member Information</h5>
              <span class="bf-badge bf-badge-primary text-uppercase mt-2">
                {{ user ? user.role : 'Guest' }}
              </span>
            </div>

            <div class="p-4 text-center">
              <div v-if="loading" class="py-4">
                <LoadingSkeleton type="avatar" />
              </div>

              <div v-else-if="error" class="bf-empty-state py-3">
                <p class="text-danger small mb-0">{{ error }}</p>
              </div>

              <div v-else-if="user" class="bf-fade-in">
                <!-- Avatar Container -->
                <div class="avatar-container mb-4 position-relative mx-auto border-light rounded-circle">
                  <img
                    :src="user.profileImageUrl || defaultAvatar"
                    class="rounded-circle border border-secondary shadow-sm avatar-image"
                    alt="Profile Avatar"
                    @error="handleAvatarError"
                  />
                  <!-- Upload overlay -->
                  <div class="avatar-upload-overlay rounded-circle d-flex align-items-center justify-content-center cursor-pointer" @click="triggerFileInput">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="text-white" viewBox="0 0 16 16">
                      <path d="M15 12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h1.172a3 3 0 0 0 2.12-.879l.83-.828A1 1 0 0 1 6.827 3h2.344a1 1 0 0 1 .702.292l.83.828A3 3 0 0 0 12.828 5H14a1 1 0 0 1 1 1zM2 4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-1.172a2 2 0 0 1-1.414-.586l-.828-.828A2 2 0 0 0 9.172 2H6.828a2 2 0 0 0-1.414.586l-.828.828A2 2 0 0 1 3.172 4z"/>
                      <path d="M8 11a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5m0 1a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7zM3 6.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0"/>
                    </svg>
                  </div>
                  <!-- Hidden file input -->
                  <input
                    type="file"
                    ref="fileInput"
                    class="d-none"
                    accept="image/*"
                    @change="handleAvatarUpload"
                  />
                </div>

                <!-- Upload Progress Indicator -->
                <div v-if="uploading" class="bf-spinner mb-3 mx-auto"></div>

                <!-- User Header -->
                <h5 class="font-weight-bold text-white mb-1">{{ user.fullName || 'Anonymous' }}</h5>
                <p class="text-muted small mb-4">@{{ user.username }}</p>

                <hr class="my-4 opacity-25" />

                <!-- Details Panel -->
                <div class="text-start">
                  <!-- VIEW MODE -->
                  <div v-if="!editMode">
                    <div class="mb-3">
                      <span class="text-muted d-block small">Full Name</span>
                      <strong class="text-white">{{ user.fullName || 'N/A' }}</strong>
                    </div>
                    <div class="mb-3">
                      <span class="text-muted d-block small">Email Address</span>
                      <strong class="text-white text-break">{{ user.email }}</strong>
                    </div>
                    <div class="mb-3">
                      <span class="text-muted d-block small">Phone Connection</span>
                      <strong class="text-white">{{ user.phoneNumber || 'Not provided' }}</strong>
                    </div>
                    <div class="mb-3">
                      <span class="text-muted d-block small">Shipping Address</span>
                      <strong class="text-white">{{ user.address || 'No shipping address set' }}</strong>
                    </div>

                    <button class="bf-btn bf-btn-outline w-100 mt-4" @click="toggleEdit">
                      Edit Account Info
                    </button>

                    <!-- Become a Seller Section for regular USERs -->
                    <div v-if="user && user.role === 'USER'" class="mt-4 p-3 bg-dark border-light rounded text-center">
                      <h6 class="font-weight-bold text-white mb-2">💡 Start Earning</h6>
                      <p class="small text-muted mb-3">Sell your high-quality PC hardware and components on the ByteForge marketplace.</p>
                      <router-link to="/seller/store-setup" class="bf-btn bf-btn-primary bf-btn-sm w-100">Become a Seller</router-link>
                    </div>
                  </div>

                  <!-- EDIT MODE -->
                  <div v-else>
                    <form @submit.prevent="saveProfile">
                      <div class="mb-3">
                        <label class="form-label small text-muted font-weight-bold">Full Name</label>
                        <input type="text" class="bf-input text-white bg-dark border-light" v-model="editForm.fullName" required />
                      </div>

                      <div class="mb-3">
                        <label class="form-label small text-muted font-weight-bold">Email Address</label>
                        <input type="email" class="bf-input text-white bg-dark border-light" v-model="editForm.email" required />
                      </div>

                      <div class="mb-3">
                        <label class="form-label small text-muted font-weight-bold">Phone Number</label>
                        <input type="text" class="bf-input text-white bg-dark border-light" v-model="editForm.phoneNumber" placeholder="e.g. +94 77 123 4567" />
                      </div>

                      <div class="mb-3">
                        <label class="form-label small text-muted font-weight-bold">Shipping Address</label>
                        <textarea class="bf-input text-white bg-dark border-light" rows="3" v-model="editForm.address" placeholder="Enter your full delivery address..."></textarea>
                      </div>

                      <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="bf-btn bf-btn-primary flex-grow-1" :disabled="saving">
                          <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
                          Save Changes
                        </button>
                        <button type="button" class="bf-btn bf-btn-ghost text-muted" @click="toggleEdit" :disabled="saving">
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

        <!-- Orders & Support Columns -->
        <div class="col-lg-8 d-flex flex-column gap-4">
          <!-- Order History Card -->
          <div class="bf-card border-light text-white">
            <div class="p-4 bg-dark border-light-bottom d-flex justify-content-between align-items-center" style="border-top-left-radius: var(--bf-radius-lg); border-top-right-radius: var(--bf-radius-lg);">
              <h5 class="mb-0 font-weight-bold text-white">Order History</h5>
              <span class="bf-badge bf-badge-primary" v-if="!ordersLoading && !ordersError">
                {{ orders.length }} Order(s)
              </span>
            </div>

            <div class="p-4" style="max-height: 400px; overflow-y: auto;">
              <div v-if="ordersLoading" class="py-5 text-center">
                <span class="spinner-border spinner-border-sm text-primary"></span>
              </div>

              <div v-else-if="ordersError" class="bf-empty-state py-3">
                <p class="text-danger small mb-0">{{ ordersError }}</p>
              </div>

              <div v-else-if="orders.length === 0" class="text-center py-4">
                <p class="text-secondary small mb-3">You haven't placed any marketplace orders yet.</p>
                <router-link to="/product" class="bf-btn bf-btn-primary bf-btn-sm px-3">Shop Parts Now</router-link>
              </div>

              <div v-else class="d-flex flex-column gap-3">
                <div v-for="order in orders" :key="order.orderId" class="bf-card bg-dark border-light overflow-hidden">
                  <div class="p-3 bg-secondary-dark border-light-bottom d-flex flex-wrap justify-content-between align-items-center gap-3">
                    <div class="text-start">
                      <span class="text-muted d-block small">ORDER PLACED</span>
                      <strong class="small text-white">{{ formatDate(order.createdDate) }}</strong>
                    </div>
                    <div class="text-start">
                      <span class="text-muted d-block small">SHIP TO</span>
                      <strong class="small text-white text-truncate d-inline-block" style="max-width: 180px;" :title="order.address">
                        {{ order.address }}
                      </strong>
                    </div>
                    <div class="text-start">
                      <span class="text-muted d-block small">PAYMENT METHOD</span>
                      <span class="bf-badge bf-badge-info text-uppercase">{{ formatPaymentMethod(order.paymentMethod) }}</span>
                    </div>
                    <div class="text-end">
                      <span class="text-muted d-block small">GRAND TOTAL</span>
                      <strong class="text-primary">{{ formatPrice(order.totalPrice) }}</strong>
                    </div>
                  </div>

                  <div class="p-0">
                    <div
                      v-for="item in order.orderItems"
                      :key="item.orderItemId"
                      class="d-flex align-items-center p-3 border-light-bottom last-border-0"
                    >
                      <img
                        :src="item.product.imageUrl"
                        class="img-thumbnail bg-dark border-secondary me-3"
                        style="width: 50px; height: 50px; object-fit: contain;"
                        alt="Product Image"
                      />
                      <div class="text-start flex-grow-1">
                        <h6 class="mb-1 font-weight-bold small text-white">{{ item.product.productName }}</h6>
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

          <!-- Support Tickets -->
          <div class="bf-card border-light text-white">
            <div class="p-4 bg-dark border-light-bottom d-flex justify-content-between align-items-center" style="border-top-left-radius: var(--bf-radius-lg); border-top-right-radius: var(--bf-radius-lg);">
              <h5 class="mb-0 font-weight-bold text-white">Support Resolution Desk</h5>
              <button class="bf-btn bf-btn-accent bf-btn-sm px-3" @click="showTicketForm = !showTicketForm">
                {{ showTicketForm ? 'Close Window' : 'File Complaint' }}
              </button>
            </div>

            <div class="p-4 text-start">
              <!-- Report form -->
              <transition name="page-fade">
                <div v-if="showTicketForm" class="bf-card bg-dark border-light p-3 mb-4 text-white">
                  <h5 class="font-weight-bold text-white mb-3">File Support Request</h5>
                  <form @submit.prevent="sendTicket">
                    <div class="mb-3">
                      <label class="form-label small font-weight-bold text-muted">Subject</label>
                      <input type="text" class="bf-input text-white bg-dark border-light" v-model="ticketSubject" placeholder="e.g. Delivery status delay / Defective item" required />
                    </div>
                    <div class="mb-3">
                      <label class="form-label small font-weight-bold text-muted">Details</label>
                      <textarea class="bf-input text-white bg-dark border-light" rows="4" v-model="ticketContent" placeholder="Describe your problem or question in detail..." required></textarea>
                    </div>
                    <button type="submit" class="bf-btn bf-btn-primary bf-btn-sm" :disabled="sendingTicket">
                      <span v-if="sendingTicket" class="spinner-border spinner-border-sm me-1"></span>
                      Submit Ticket
                    </button>
                  </form>
                </div>
              </transition>

              <!-- Ticket logs -->
              <div v-if="messagesLoading" class="py-4 text-center">
                <span class="spinner-border spinner-border-sm text-primary"></span>
              </div>

              <div v-else-if="messagesError" class="alert alert-danger py-2">
                {{ messagesError }}
              </div>

              <div v-else-if="messages.length === 0" class="text-center py-3 text-secondary small">
                No support logs registered under your account.
              </div>

              <div v-else style="max-height: 350px; overflow-y: auto;" class="d-flex flex-column gap-3">
                <div v-for="msg in messages" :key="msg.messageId" class="bf-card bg-dark border-light p-3">
                  <div class="d-flex justify-content-between align-items-center py-2 flex-wrap gap-2 border-light-bottom mb-2">
                    <div class="d-flex align-items-center gap-2">
                      <span class="bf-badge" :class="msg.status === 'OPEN' ? 'bf-badge-warning' : 'bf-badge-success'">
                        {{ msg.status }}
                      </span>
                      <strong class="text-white small">{{ msg.subject }}</strong>
                    </div>
                    <small class="text-muted">{{ formatDate(msg.createdDate) }}</small>
                  </div>
                  
                  <p class="small text-secondary mb-2"><strong>My Request:</strong> {{ msg.content }}</p>

                  <div v-if="msg.status === 'REPLIED'" class="bg-secondary-dark p-3 rounded border-start border-success border-3 text-start mt-2">
                    <small class="font-weight-bold text-success d-block mb-1">Response from Support:</small>
                    <p class="small mb-0 text-white">{{ msg.reply }}</p>
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
import LoadingSkeleton from '../../components/Common/LoadingSkeleton.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'ProfileView',
  components: { LoadingSkeleton },
  data() {
    return {
      user: null,
      loading: true,
      error: null,

      // Inline Editing Form
      editMode: false,
      saving: false,
      editForm: {
        fullName: '',
        email: '',
        phoneNumber: '',
        address: ''
      },

      // Avatar Image Upload
      uploading: false,
      defaultAvatar: 'https://placehold.co/150x150?text=Upload+Photo',

      // Support Tickets Configuration
      messages: [],
      messagesLoading: true,
      messagesError: null,
      showTicketForm: false,
      ticketSubject: '',
      ticketContent: '',
      sendingTicket: false,

      // Orders History
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
    formatPrice(val) {
      return formatPrice(val);
    },
    handleAvatarError(e) {
      e.target.src = this.defaultAvatar;
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    async handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append('file', file);

      this.uploading = true;
      try {
        const uploadRes = await api.post('/file/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
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
    } catch (err) {
      this.error = extractErrorMessage(err);
    } finally {
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.avatar-container {
  width: 120px;
  height: 120px;
  overflow: hidden;
}

.avatar-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  background-color: var(--bf-bg-secondary);
}

.avatar-upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.25s ease;
}

.avatar-container:hover .avatar-upload-overlay {
  opacity: 1;
}

.bg-secondary-dark {
  background: rgba(255, 255, 255, 0.03);
}

.border-light-bottom {
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.last-border-0:last-child {
  border-bottom: 0 !important;
}
</style>
