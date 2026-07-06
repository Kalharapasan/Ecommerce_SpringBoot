<template>
  <div class="container my-5">
    <div class="row">
      <!-- Profile Details Column -->
      <div class="col-lg-4 mb-4">
        <div class="card shadow-sm border-0 position-relative h-100">
          <!-- Banner Header -->
          <div class="card-header bg-dark text-white p-4 text-center position-relative">
            <h4 class="mb-0 font-weight-bold">My Account</h4>
            <span class="badge bg-secondary position-absolute top-0 start-0 m-3 text-uppercase">
              {{ user ? user.role : '' }}
            </span>
          </div>

          <div class="card-body p-4 text-center">
            <div v-if="loading" class="py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading profile...</span>
              </div>
            </div>

            <div v-else-if="error" class="alert alert-danger" role="alert">
              {{ error }}
            </div>

            <div v-else-if="user">
              <!-- Avatar Container -->
              <div class="avatar-container mb-4 position-relative mx-auto">
                <img 
                  :src="user.profileImageUrl || defaultAvatar" 
                  class="rounded-circle border shadow-sm avatar-image"
                  alt="Profile Avatar"
                  @error="handleAvatarError"
                />
                <!-- Upload overlay -->
                <div class="avatar-upload-overlay rounded-circle d-flex align-items-center justify-content-center cursor-pointer" @click="triggerFileInput">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-camera text-white" viewBox="0 0 16 16">
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

              <!-- Upload Progress / Info -->
              <div v-if="uploading" class="spinner-border spinner-border-sm text-primary mb-3" role="status">
                <span class="visually-hidden">Uploading image...</span>
              </div>

              <!-- User Header -->
              <h5 class="font-weight-bold mb-1">{{ user.fullName || 'Anonymous User' }}</h5>
              <p class="text-muted small mb-4">@{{ user.username }}</p>

              <hr />

              <!-- User Details Panel (Read-only / Edit-mode) -->
              <div class="text-start">
                <!-- VIEW MODE -->
                <div v-if="!editMode">
                  <div class="row mb-3">
                    <div class="col-4 text-muted small">Full Name</div>
                    <div class="col-8 font-weight-bold">{{ user.fullName || 'N/A' }}</div>
                  </div>
                  <div class="row mb-3">
                    <div class="col-4 text-muted small">Email</div>
                    <div class="col-8 text-break">{{ user.email }}</div>
                  </div>
                  <div class="row mb-3">
                    <div class="col-4 text-muted small">Phone</div>
                    <div class="col-8">{{ user.phoneNumber || 'Not provided' }}</div>
                  </div>
                  <div class="row mb-3">
                    <div class="col-4 text-muted small">Address</div>
                    <div class="col-8">{{ user.address || 'No shipping address set' }}</div>
                  </div>

                  <button class="btn btn-outline-primary btn-sm w-100 mt-3 font-weight-bold" @click="toggleEdit">
                    Edit Account Info
                  </button>
                </div>

                <!-- EDIT MODE -->
                <div v-else>
                  <form @submit.prevent="saveProfile">
                    <div class="mb-3">
                      <label class="form-label small text-muted font-weight-bold mb-1">Full Name</label>
                      <input type="text" class="form-control form-control-sm" v-model="editForm.fullName" required />
                    </div>

                    <div class="mb-3">
                      <label class="form-label small text-muted font-weight-bold mb-1">Email Address</label>
                      <input type="email" class="form-control form-control-sm" v-model="editForm.email" required />
                    </div>

                    <div class="mb-3">
                      <label class="form-label small text-muted font-weight-bold mb-1">Phone Number</label>
                      <input type="text" class="form-control form-control-sm" v-model="editForm.phoneNumber" placeholder="e.g. +94 77 123 4567" />
                    </div>

                    <div class="mb-3">
                      <label class="form-label small text-muted font-weight-bold mb-1">Shipping Address</label>
                      <textarea class="form-control form-control-sm" rows="3" v-model="editForm.address" placeholder="Enter your full delivery address..."></textarea>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                      <button type="submit" class="btn btn-success btn-sm flex-grow-1 font-weight-bold" :disabled="saving">
                        <span v-if="saving" class="spinner-border spinner-border-sm me-1"></span>
                        Save Changes
                      </button>
                      <button type="button" class="btn btn-outline-secondary btn-sm" @click="toggleEdit" :disabled="saving">
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

      <!-- Order History & Support messages Column -->
      <div class="col-lg-8 mb-4">
        <!-- Order History Card -->
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header bg-primary text-white p-4 d-flex justify-content-between align-items-center">
            <h4 class="mb-0">Order History</h4>
            <span class="badge bg-light text-primary font-weight-bold" style="font-size: 0.9rem;" v-if="!ordersLoading && !ordersError">
              {{ orders.length }} Order(s)
            </span>
          </div>
          <div class="card-body p-4" style="max-height: 400px; overflow-y: auto;">
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

        <!-- Support Messages & Help Card -->
        <div class="card shadow-sm border-0">
          <div class="card-header bg-dark text-white p-4 d-flex justify-content-between align-items-center">
            <h4 class="mb-0">Support Tickets & Help</h4>
            <button class="btn btn-warning btn-sm font-weight-bold text-dark" @click="showTicketForm = !showTicketForm">
              {{ showTicketForm ? 'Close Form' : 'Report a Problem' }}
            </button>
          </div>
          <div class="card-body p-4 text-start">
            
            <!-- Send Ticket Form -->
            <div v-if="showTicketForm" class="border p-3 rounded mb-4 bg-light">
              <h5 class="font-weight-bold mb-3">Submit Support Ticket</h5>
              <form @submit.prevent="sendTicket">
                <div class="mb-3">
                  <label class="form-label small font-weight-bold text-secondary">Subject</label>
                  <input type="text" class="form-control" v-model="ticketSubject" placeholder="e.g. Delivery status delay / Defective item" required />
                </div>
                <div class="mb-3">
                  <label class="form-label small font-weight-bold text-secondary">Details</label>
                  <textarea class="form-control" rows="4" v-model="ticketContent" placeholder="Describe your problem or question in detail..." required></textarea>
                </div>
                <button type="submit" class="btn btn-primary btn-sm font-weight-bold" :disabled="sendingTicket">
                  <span v-if="sendingTicket" class="spinner-border spinner-border-sm me-1"></span>
                  Send Message
                </button>
              </form>
            </div>

            <!-- Messages List -->
            <div v-if="messagesLoading" class="text-center py-5">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading messages...</span>
              </div>
            </div>

            <div v-else-if="messagesError" class="alert alert-danger">
              {{ messagesError }}
            </div>

            <div v-else-if="messages.length === 0">
              <p class="text-muted text-center mb-0">No active support tickets found.</p>
            </div>

            <div v-else style="max-height: 350px; overflow-y: auto;">
              <div v-for="msg in messages" :key="msg.messageId" class="card mb-3 border shadow-xs">
                <div class="card-header bg-light d-flex justify-content-between align-items-center py-2 flex-wrap gap-2">
                  <div>
                    <span class="badge me-2" :class="msg.status === 'OPEN' ? 'bg-warning text-dark' : 'bg-success'">
                      {{ msg.status }}
                    </span>
                    <strong class="text-dark small">{{ msg.subject }}</strong>
                  </div>
                  <small class="text-muted">{{ formatDate(msg.createdDate) }}</small>
                </div>
                <div class="card-body py-2">
                  <p class="small mb-2 text-dark"><strong>My message:</strong> {{ msg.content }}</p>
                  
                  <div v-if="msg.status === 'REPLIED'" class="bg-light p-2 rounded border-start border-success border-3 text-start mt-2">
                    <small class="font-weight-bold text-success d-block mb-1">Admin Response:</small>
                    <p class="small mb-0 text-dark">{{ msg.reply }}</p>
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

export default {
  name: 'ProfileView',
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
        // 1. Upload the image using our file controller
        const uploadRes = await api.post('/file/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        const localImageUrl = uploadRes.data;

        // 2. Instantly persist to user profile database record
        const updateRes = await api.put('/user/update', {
          userId: this.user.userId,
          fullName: this.user.fullName,
          email: this.user.email,
          phoneNumber: this.user.phoneNumber,
          address: this.user.address,
          profileImageUrl: localImageUrl
        });

        this.user = updateRes.data.data;
        alert('Profile picture updated successfully!');
      } catch (err) {
        alert('Failed to upload picture: ' + extractErrorMessage(err));
      } finally {
        this.uploading = false;
      }
    },
    toggleEdit() {
      if (!this.editMode) {
        // Initialize form fields with current user state
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
        alert('Profile saved successfully.');
      } catch (err) {
        alert('Failed to save profile: ' + extractErrorMessage(err));
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
        alert('Please fill in all fields.');
        return;
      }
      this.sendingTicket = true;
      try {
        await api.post(`/message/create?subject=${encodeURIComponent(this.ticketSubject)}&content=${encodeURIComponent(this.ticketContent)}&token=${token}`);
        alert('Support ticket created successfully!');
        this.ticketSubject = '';
        this.ticketContent = '';
        this.showTicketForm = false;
        await this.fetchUserMessages();
      } catch (err) {
        alert('Failed to send message: ' + extractErrorMessage(err));
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
      this.error = 'User session not found. Please log in.';
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
.card {
  border-radius: 12px;
  overflow: hidden;
}
.last-border-0:last-child {
  border-bottom: 0 !important;
}

/* Avatar upload aesthetics */
.avatar-container {
  width: 130px;
  height: 130px;
}
.avatar-image {
  width: 130px;
  height: 130px;
  object-fit: cover;
  background-color: #f8f9fa;
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
</style>
