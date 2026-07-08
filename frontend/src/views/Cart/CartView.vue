<template>
  <div class="bf-page bf-fade-in py-5 text-start">
    <div class="container position-relative">
      <div class="glow-bg animate-float"></div>

      <!-- Header details -->
      <div class="row mb-5 position-relative z-2">
        <div class="col-12 text-start">
          <span class="badge bg-primary bg-opacity-25 text-cyan px-3 py-2 mb-2 rounded-pill text-uppercase">
            <i class="bi bi-shield-lock-fill me-1"></i> Secure Checkout Pipeline
          </span>
          <h1 class="text-white font-bold mb-1 fs-2">Shopping Cart & Checkout</h1>
          <p class="text-muted small">Review components, complete delivery details, and execute secure transactions.</p>
        </div>
      </div>

      <!-- Order Progression Step Tracker -->
      <div class="row mb-5 justify-content-center position-relative z-2" v-if="items.length > 0">
        <div class="col-md-10">
          <div class="bf-steps-tracker position-relative bf-glass p-3 border-0">
            <!-- Progress Line Background -->
            <div class="bf-steps-line-bg"></div>
            <!-- Progress Line Active -->
            <div class="bf-steps-line" :style="{ width: progressPercent + '%' }"></div>
            
            <!-- Step 1 -->
            <div class="bf-step-item" :class="{ active: currentStep >= 1 }">
              <div class="bf-step-circle"><i class="bi bi-cart3"></i></div>
              <span class="bf-step-label">Cart Review</span>
            </div>
            <!-- Step 2 -->
            <div class="bf-step-item" :class="{ active: currentStep >= 2 }">
              <div class="bf-step-circle"><i class="bi bi-geo-alt"></i></div>
              <span class="bf-step-label">Delivery Info</span>
            </div>
            <!-- Step 3 -->
            <div class="bf-step-item" :class="{ active: currentStep >= 3 }">
              <div class="bf-step-circle"><i class="bi bi-credit-card"></i></div>
              <span class="bf-step-label">Secure Payment</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading && items.length === 0" class="py-5 position-relative z-2">
        <div class="bf-skeleton" style="height: 300px; width: 100%;"></div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bf-empty-state bf-glass p-5 text-center position-relative z-2">
        <div class="bf-empty-icon text-danger fs-1"><i class="bi bi-exclamation-triangle"></i></div>
        <h5 class="text-white font-bold mt-3">Unable to Sync Cart</h5>
        <p class="text-muted">{{ error }}</p>
        <button class="bf-btn-premium border-0 mt-3" @click="fetchCartData">Retry Sync</button>
      </div>

      <!-- Empty Cart State -->
      <div v-else-if="items.length === 0" class="bf-empty-state bf-glass p-5 text-center position-relative z-2">
        <div class="bf-empty-icon text-cyan fs-1 mb-2"><i class="bi bi-cart-x"></i></div>
        <h5 class="text-white font-bold">Your Shopping Cart is Empty</h5>
        <p class="text-muted small mb-4">Explore our catalogue of premium PC graphics cards, processors, and storage kits to get started.</p>
        <router-link to="/product" class="bf-btn-premium border-0 py-2.5 text-decoration-none">Browse Products</router-link>
      </div>

      <!-- Cart Content Layout -->
      <div v-else class="row g-4 position-relative z-2">
        <!-- Left Column: Checkout steps -->
        <div class="col-lg-8">
          <!-- STEP 1: CART REVIEW -->
          <div v-if="currentStep === 1" class="bf-glass-card border-0 overflow-hidden">
            <div class="table-responsive">
              <table class="table table-borderless align-middle mb-0 text-white">
                <thead>
                  <tr class="border-bottom border-secondary border-opacity-25 text-muted small text-uppercase">
                    <th class="py-3 px-4">Product Details</th>
                    <th class="py-3">Quantity</th>
                    <th class="py-3 text-start">Total Price</th>
                    <th class="py-3">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <CartItemRow
                    v-for="item in items"
                    :key="item.cartId"
                    :item="item"
                    :loading="actionLoading"
                    @update-qty="handleUpdateQty"
                    @remove="handleRemoveItem"
                  />
                </tbody>
              </table>
            </div>
          </div>

          <!-- STEP 2: DELIVERY INFO -->
          <div class="col-lg-12" v-else-if="currentStep === 2">
            <div class="bf-glass-card p-4 p-md-5 border-0">
              <h5 class="text-white font-bold mb-4 d-flex align-items-center gap-2">
                <i class="bi bi-geo-alt-fill text-cyan"></i> Delivery Information
              </h5>
              <form @submit.prevent="currentStep = 3">
                <div class="mb-4 text-start">
                  <label class="form-label small text-muted font-bold mb-2">Street Address</label>
                  <input
                    type="text"
                    class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white"
                    v-model="delivery.street"
                    required
                    placeholder="e.g., 123 Galle Road, Colombo 03"
                  />
                </div>
                <div class="row g-4">
                  <div class="col-md-6 text-start">
                    <label class="form-label small text-muted font-bold mb-2">City</label>
                    <input
                      type="text"
                      class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white"
                      v-model="delivery.city"
                      required
                      placeholder="e.g., Colombo"
                    />
                  </div>
                  <div class="col-md-6 text-start">
                    <label class="form-label small text-muted font-bold mb-2">Phone Number</label>
                    <input
                      type="tel"
                      class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white"
                      v-model="delivery.phone"
                      required
                      placeholder="e.g., 0771234567"
                    />
                  </div>
                </div>
                <div class="d-flex justify-content-between mt-5">
                  <button type="button" class="bf-btn-ghost text-muted border-0" @click="currentStep = 1">Back</button>
                  <button type="submit" class="bf-btn-premium border-0 py-2.5 px-4" :disabled="!isDeliveryValid">
                    Proceed to Payment <i class="bi bi-arrow-right ms-1"></i>
                  </button>
                </div>
              </form>
            </div>
          </div>

          <!-- STEP 3: SECURE PAYMENT -->
          <div v-else-if="currentStep === 3" class="col-lg-12">
            <div class="bf-glass-card p-4 p-md-5 border-0">
              <h5 class="text-white font-bold mb-4 d-flex align-items-center gap-2">
                <i class="bi bi-credit-card-fill text-purple"></i> Payment Configuration
              </h5>

              <div class="mb-4 d-flex flex-column gap-3">
                <!-- Cash on Delivery Option -->
                <div
                  class="bf-payment-option-cyber bf-glass border-0"
                  :class="{ active: payment.method === 'CASH_ON_DELIVERY' }"
                  @click="payment.method = 'CASH_ON_DELIVERY'"
                >
                  <div class="d-flex align-items-center gap-3">
                    <input
                      type="radio"
                      class="form-check-input accent-cyan"
                      name="payMethod"
                      id="payCod"
                      value="CASH_ON_DELIVERY"
                      v-model="payment.method"
                      @click.stop
                    />
                    <label class="form-check-label w-100 cursor-pointer text-start" for="payCod">
                      <span class="d-block font-bold text-white mb-1">Cash on Delivery (COD)</span>
                      <span class="small text-muted">Execute transaction physically upon package courier delivery.</span>
                    </label>
                  </div>
                </div>

                <!-- Online Payment Option -->
                <div
                  class="bf-payment-option-cyber bf-glass border-0 d-flex flex-column gap-3"
                  :class="{ active: payment.method === 'ONLINE_PAYMENT' }"
                  @click="payment.method = 'ONLINE_PAYMENT'"
                >
                  <div class="d-flex align-items-center gap-3">
                    <input
                      type="radio"
                      class="form-check-input accent-cyan"
                      name="payMethod"
                      id="payOnline"
                      value="ONLINE_PAYMENT"
                      v-model="payment.method"
                      @click.stop
                    />
                    <label class="form-check-label w-100 cursor-pointer text-start" for="payOnline">
                      <span class="d-block font-bold text-white mb-1">Online Checkout (Credit / Debit Card)</span>
                      <span class="small text-muted">Execute instant transaction processing via Visa, Mastercard, or Amex.</span>
                    </label>
                  </div>

                  <!-- Card Details Fields -->
                  <transition name="page-fade">
                    <div v-if="payment.method === 'ONLINE_PAYMENT'" class="border-top border-secondary border-opacity-25 pt-4 mt-2 text-start" @click.stop>
                      <div class="mb-4">
                        <label class="form-label small text-muted font-bold mb-2">Cardholder Name</label>
                        <input type="text" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="payment.cardName" required placeholder="John Doe" />
                      </div>
                      <div class="mb-4">
                        <label class="form-label small text-muted font-bold mb-2">Card Number</label>
                        <input type="text" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="payment.cardNumber" required placeholder="4111 2222 3333 4444" maxlength="19" />
                      </div>
                      <div class="row g-4">
                        <div class="col-6">
                          <label class="form-label small text-muted font-bold mb-2">Expiry Date</label>
                          <input type="text" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="payment.cardExpiry" required placeholder="MM/YY" maxlength="5" />
                        </div>
                        <div class="col-6">
                          <label class="form-label small text-muted font-bold mb-2">CVV Code</label>
                          <input type="password" class="bf-search-input py-2.5 px-3 border-secondary border-opacity-25 w-100 text-white" v-model="payment.cardCvv" required placeholder="123" maxlength="3" />
                        </div>
                      </div>
                    </div>
                  </transition>
                </div>
              </div>

              <div class="d-flex justify-content-between mt-5">
                <button type="button" class="bf-btn-ghost text-muted border-0" @click="currentStep = 2" :disabled="actionLoading">Back</button>
                <button
                  type="button"
                  class="bf-btn-premium border-0 font-bold px-4 py-2.5"
                  @click="handlePlaceOrder"
                  :disabled="actionLoading || !isPaymentValid"
                >
                  <span v-if="actionLoading" class="spinner-border spinner-border-sm me-2"></span>
                  Confirm and Place Order
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Column: Order Summary & Promo Code -->
        <div class="col-lg-4">
          <div class="bf-glass-card p-4 border-0 mb-4">
            <h5 class="text-white font-bold mb-4">Order Summary</h5>

            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span class="small text-muted">Subtotal</span>
              <span class="font-semibold text-white">{{ formattedSubtotal }}</span>
            </div>

            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span class="small text-muted">Discount ({{ appliedDiscountPercent }}%)</span>
              <span class="text-success font-semibold">- {{ formattedDiscount }}</span>
            </div>

            <hr class="border-secondary border-opacity-25 my-4" />

            <div class="d-flex justify-content-between mb-4 align-items-center">
              <span class="font-bold text-white">Grand Total</span>
              <span class="text-gradient-primary fs-3 font-bold">{{ formattedTotal }}</span>
            </div>

            <!-- Proceed buttons -->
            <button
              v-if="currentStep === 1"
              class="bf-btn-premium border-0 w-100 py-3 shadow-glow"
              @click="currentStep = 2"
              :disabled="actionLoading"
            >
              Proceed to Checkout
            </button>
            <button
              v-else-if="currentStep === 2"
              class="bf-btn-premium border-0 w-100 py-3 shadow-glow"
              @click="currentStep = 3"
              :disabled="!isDeliveryValid"
            >
              Proceed to Payment
            </button>
            <button
              v-else
              class="bf-btn-premium border-0 w-100 py-3 shadow-glow"
              @click="handlePlaceOrder"
              :disabled="actionLoading || !isPaymentValid"
            >
              <span v-if="actionLoading" class="spinner-border spinner-border-sm me-2"></span>
              Place Order
            </button>
          </div>

          <!-- Promo Code Input (Only Step 1) -->
          <div class="bf-glass p-4 border-0 mb-4 text-start" v-if="currentStep === 1">
            <h6 class="text-white font-bold mb-3">Promo Code Coupon</h6>
            <div class="d-flex gap-2">
              <input
                type="text"
                class="bf-search-input py-2 px-3 border-secondary border-opacity-25 text-white flex-grow-1"
                v-model="promoCode"
                placeholder="SAVE10 or SAVE50"
                @keyup.enter="applyPromoCode"
              />
              <button class="bf-btn-ghost py-2 border-secondary border-opacity-25 px-3" type="button" @click="applyPromoCode">
                Apply
              </button>
            </div>
            <div v-if="promoMessage" :class="{'text-danger': promoError, 'text-success': !promoError}" class="small mt-2 font-bold">
              {{ promoMessage }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { getToken, getCurrentUser, updateCartCount } from '../../utils/auth';
import CartItemRow from '../../components/Cart/CartItemRow.vue';
import LoadingSkeleton from '../../components/Common/LoadingSkeleton.vue';
import { showToast } from '../../components/Common/ToastNotification.vue';

export default {
  name: 'CartView',
  components: { CartItemRow, LoadingSkeleton },
  data() {
    return {
      currentStep: 1,
      items: [],
      totals: {
        amount: 0,
        discount: 0,
        finalAmount: 0
      },
      loading: true,
      actionLoading: false,
      error: null,
      promoCode: '',
      appliedDiscountPercent: 0,
      promoMessage: '',
      promoError: false,
      delivery: { street: '', city: '', phone: '' },
      payment: {
        method: 'CASH_ON_DELIVERY',
        cardName: '',
        cardNumber: '',
        cardExpiry: '',
        cardCvv: ''
      }
    };
  },
  computed: {
    progressPercent() {
      if (this.currentStep === 1) return 0;
      if (this.currentStep === 2) return 50;
      return 100;
    },
    formattedSubtotal() { return formatPrice(this.totals.amount); },
    formattedDiscount() { return formatPrice(this.totals.discount); },
    formattedTotal() { return formatPrice(this.totals.finalAmount); },
    isDeliveryValid() {
      return (
        this.delivery.street.trim() !== '' &&
        this.delivery.city.trim() !== '' &&
        this.delivery.phone.trim() !== ''
      );
    },
    isPaymentValid() {
      if (this.payment.method === 'CASH_ON_DELIVERY') return true;
      return (
        this.payment.cardName.trim() !== '' &&
        this.payment.cardNumber.trim() !== '' &&
        this.payment.cardExpiry.trim() !== '' &&
        this.payment.cardCvv.trim() !== ''
      );
    }
  },
  methods: {
    async fetchCartData() {
      this.error = null;
      const token = getToken();
      if (!token) {
        this.error = 'User authentication token not found. Please log in.';
        this.loading = false;
        return;
      }

      try {
        const response = await api.get(`/cart/${token}`);
        this.items = response.data.data || [];

        if (this.items.length > 0) {
          const user = getCurrentUser();
          if (user && user.userId) {
            try {
              const calcResponse = await api.get(`/cart/totalCalculation/${user.userId}?discount=${this.appliedDiscountPercent}`);
              this.totals = calcResponse.data;
            } catch (calcErr) {
              console.warn('Calculation error:', extractErrorMessage(calcErr));
              this.resetTotals();
            }
          }
          const totalCount = this.items.reduce((sum, item) => sum + item.count, 0);
          updateCartCount(totalCount);
        } else {
          this.resetTotals();
          updateCartCount(0);
        }
      } catch (err) {
        this.error = extractErrorMessage(err);
      } finally {
        this.loading = false;
      }
    },
    resetTotals() {
      this.totals = { amount: 0, discount: 0, finalAmount: 0 };
    },
    async handleUpdateQty({ cartId, count }) {
      this.actionLoading = true;
      try {
        await api.put(`/cart/${cartId}`, { count });
        await this.fetchCartData();
      } catch (err) {
        showToast({
          message: 'Failed to update quantity: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Quantity Error'
        });
      } finally {
        this.actionLoading = false;
      }
    },
    async handleRemoveItem(cartId) {
      this.actionLoading = true;
      try {
        await api.delete(`/cart/${cartId}`);
        await this.fetchCartData();
        showToast({
          message: 'Item removed from your cart.',
          type: 'info',
          title: 'Cart Updated'
        });
      } catch (err) {
        showToast({
          message: 'Failed to remove item: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Operation Failed'
        });
      } finally {
        this.actionLoading = false;
      }
    },
    applyPromoCode() {
      this.promoMessage = '';
      this.promoError = false;
      const code = this.promoCode.trim().toUpperCase();
      if (code === 'SAVE10') {
        this.appliedDiscountPercent = 10;
        this.promoMessage = 'Coupon SAVE10 applied! 10% discount has been calculated.';
        this.fetchCartData();
      } else if (code === 'SAVE55' || code === 'SAVE50') {
        this.appliedDiscountPercent = 50;
        this.promoMessage = 'Coupon SAVE50 applied! 50% discount has been calculated.';
        this.fetchCartData();
      } else if (code === '') {
        this.appliedDiscountPercent = 0;
        this.fetchCartData();
      } else {
        this.promoError = true;
        this.promoMessage = 'Invalid Promo Code!';
        this.appliedDiscountPercent = 0;
        this.fetchCartData();
      }
    },
    async handlePlaceOrder() {
      if (!this.isDeliveryValid) {
        showToast({ message: 'Please complete the delivery details first.', type: 'warning' });
        this.currentStep = 2;
        return;
      }
      if (!this.isPaymentValid) {
        showToast({ message: 'Please complete the payment details first.', type: 'warning' });
        this.currentStep = 3;
        return;
      }

      this.actionLoading = true;
      try {
        const token = getToken();
        const addressString = `${this.delivery.street}, ${this.delivery.city} (Phone: ${this.delivery.phone})`;

        const payload = {
          token: token,
          address: addressString,
          paymentMethod: this.payment.method,
          discountPercent: this.appliedDiscountPercent
        };

        const response = await api.post('/order/create', payload);
        showToast({
          message: response.data.message || 'Order Placed Successfully! Your cart has been cleared.',
          type: 'success',
          title: 'Order Placed'
        });

        this.items = [];
        this.resetTotals();
        updateCartCount(0);
        this.currentStep = 1;
        this.delivery = { street: '', city: '', phone: '' };
        this.payment = { method: 'CASH_ON_DELIVERY', cardName: '', cardNumber: '', cardExpiry: '', cardCvv: '' };

        this.$router.push({ name: 'Profile' });
      } catch (err) {
        showToast({
          message: 'Checkout failed: ' + extractErrorMessage(err),
          type: 'error',
          title: 'Payment Failed'
        });
      } finally {
        this.actionLoading = false;
      }
    }
  },
  mounted() {
    this.fetchCartData();
  }
};
</script>

<style scoped>
.glow-bg {
  position: absolute;
  width: 450px; height: 450px;
  background: radial-gradient(circle, rgba(124, 58, 237, 0.07) 0%, transparent 75%);
  top: -80px; right: -80px;
  pointer-events: none;
  z-index: 1;
}

/* Steps Tracker */
.bf-steps-tracker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: var(--bf-radius-lg);
}

.bf-steps-line-bg {
  position: absolute;
  top: 50%; left: 30px; right: 30px;
  height: 2px;
  background: rgba(255, 255, 255, 0.08);
  z-index: 1;
  transform: translateY(-50%);
}

.bf-steps-line {
  position: absolute;
  top: 50%; left: 30px;
  height: 2px;
  background: var(--bf-gradient-primary);
  z-index: 1;
  transform: translateY(-50%);
  transition: width 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.bf-step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
  position: relative;
  width: 100px;
}

.bf-step-circle {
  width: 40px; height: 40px;
  border-radius: 50%;
  background: var(--bf-bg-secondary);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--bf-text-muted);
  display: flex; align-items: center; justify-content: center;
  font-size: 1.1rem;
  transition: all 0.3s ease;
}

.bf-step-label {
  font-size: 0.72rem;
  font-weight: 700;
  color: var(--bf-text-muted);
  margin-top: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: color 0.3s ease;
}

.bf-step-item.active .bf-step-circle {
  background: var(--bf-gradient-primary);
  border-color: transparent;
  color: #fff;
  box-shadow: 0 0 15px rgba(124, 58, 237, 0.4);
}

.bf-step-item.active .bf-step-label {
  color: var(--bf-cyan);
}

/* Payment Option Radio Buttons */
.bf-payment-option-cyber {
  padding: 20px;
  cursor: pointer;
  border-radius: var(--bf-radius-lg);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.bf-payment-option-cyber:hover {
  background: rgba(255, 255, 255, 0.04);
}
.bf-payment-option-cyber.active {
  background: rgba(59, 130, 246, 0.05);
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.1) inset;
  border: 1px solid rgba(59, 130, 246, 0.2) !important;
}

@media (max-width: 768px) {
  .bf-steps-tracker {
    padding: 10px;
  }
  .bf-steps-line, .bf-steps-line-bg {
    display: none;
  }
  .bf-step-item {
    width: auto;
    flex-grow: 1;
  }
}
</style>
