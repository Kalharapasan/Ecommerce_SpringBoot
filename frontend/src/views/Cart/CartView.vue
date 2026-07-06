<template>
  <div class="bf-page bf-fade-in p-4">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-primary mb-2">Shopping Experience</span>
          <h2 class="bf-section-title">Shopping Cart</h2>
          <p class="bf-section-desc">Review your items, enter shipping information, and place your order securely.</p>
        </div>
      </div>

      <!-- Order Progression Step Tracker -->
      <div class="row mb-5 justify-content-center" v-if="items.length > 0">
        <div class="col-md-10">
          <div class="bf-steps-tracker position-relative">
            <!-- Progress Line -->
            <div class="bf-steps-line" :style="{ width: progressPercent + '%' }"></div>
            <!-- Step 1 -->
            <div class="bf-step-item" :class="{ active: currentStep >= 1 }">
              <div class="bf-step-circle">1</div>
              <span class="bf-step-label">Cart Review</span>
            </div>
            <!-- Step 2 -->
            <div class="bf-step-item" :class="{ active: currentStep >= 2 }">
              <div class="bf-step-circle">2</div>
              <span class="bf-step-label">Delivery Info</span>
            </div>
            <!-- Step 3 -->
            <div class="bf-step-item" :class="{ active: currentStep >= 3 }">
              <div class="bf-step-circle">3</div>
              <span class="bf-step-label">Secure Payment</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading && items.length === 0" class="py-5">
        <LoadingSkeleton type="table" :lines="4" />
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bf-empty-state">
        <div class="bf-empty-icon" style="color: var(--bf-danger);">⚠</div>
        <h5>Unable to Sync Cart</h5>
        <p>{{ error }}</p>
        <button class="bf-btn bf-btn-primary" @click="fetchCartData">Retry Sync</button>
      </div>

      <!-- Empty Cart State -->
      <div v-else-if="items.length === 0" class="bf-empty-state">
        <div class="bf-empty-icon">🛒</div>
        <h5>Your Shopping Cart is Empty</h5>
        <p>Browse our catalogue of premium PC graphics cards, processors, and storage kits to get started.</p>
        <router-link to="/product" class="bf-btn bf-btn-primary">Browse Products</router-link>
      </div>

      <!-- Cart Content Layout -->
      <div v-else class="row g-4 text-start">
        <!-- STEP 1: CART REVIEW -->
        <div v-if="currentStep === 1" class="col-lg-8">
          <div class="bf-card bf-glass overflow-hidden">
            <div class="table-responsive">
              <table class="bf-table">
                <thead>
                  <tr>
                    <th>Product Details</th>
                    <th>Quantity</th>
                    <th>Total Price</th>
                    <th>Actions</th>
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
        </div>

        <!-- STEP 2: DELIVERY INFO -->
        <div v-else-if="currentStep === 2" class="col-lg-8">
          <div class="bf-card bf-glass p-4">
            <h5 class="font-weight-bold text-dark mb-4">Delivery Information</h5>
            <form @submit.prevent="currentStep = 3">
              <div class="mb-3">
                <label class="form-label font-weight-bold text-secondary">Street Address</label>
                <input
                  type="text"
                  class="bf-input"
                  v-model="delivery.street"
                  required
                  placeholder="e.g., 123 Galle Road, Colombo 03"
                />
              </div>
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label font-weight-bold text-secondary">City</label>
                  <input
                    type="text"
                    class="bf-input"
                    v-model="delivery.city"
                    required
                    placeholder="e.g., Colombo"
                  />
                </div>
                <div class="col-md-6">
                  <label class="form-label font-weight-bold text-secondary">Phone Number</label>
                  <input
                    type="tel"
                    class="bf-input"
                    v-model="delivery.phone"
                    required
                    placeholder="e.g., 0771234567"
                  />
                </div>
              </div>
              <div class="d-flex justify-content-between mt-4">
                <button type="button" class="bf-btn bf-btn-ghost" @click="currentStep = 1">Back</button>
                <button type="submit" class="bf-btn bf-btn-primary" :disabled="!isDeliveryValid">Proceed to Payment</button>
              </div>
            </form>
          </div>
        </div>

        <!-- STEP 3: SECURE PAYMENT -->
        <div v-else-if="currentStep === 3" class="col-lg-8">
          <div class="bf-card bf-glass p-4">
            <h5 class="font-weight-bold text-dark mb-4">Payment Method</h5>

            <div class="mb-4 d-flex flex-column gap-3">
              <!-- Cash on Delivery Option -->
              <div
                class="bf-payment-option"
                :class="{ active: payment.method === 'CASH_ON_DELIVERY' }"
                @click="payment.method = 'CASH_ON_DELIVERY'"
              >
                <div class="d-flex align-items-center gap-3">
                  <input
                    type="radio"
                    class="form-check-input"
                    name="payMethod"
                    id="payCod"
                    value="CASH_ON_DELIVERY"
                    v-model="payment.method"
                    @click.stop
                  />
                  <label class="form-check-label w-100 cursor-pointer" for="payCod">
                    <span class="d-block font-weight-bold text-dark">Cash on Delivery (COD)</span>
                    <span class="small text-secondary">Pay with cash when your package is delivered to your door.</span>
                  </label>
                </div>
              </div>

              <!-- Online Payment Option -->
              <div
                class="bf-payment-option d-flex flex-column gap-3"
                :class="{ active: payment.method === 'ONLINE_PAYMENT' }"
                @click="payment.method = 'ONLINE_PAYMENT'"
              >
                <div class="d-flex align-items-center gap-3">
                  <input
                    type="radio"
                    class="form-check-input"
                    name="payMethod"
                    id="payOnline"
                    value="ONLINE_PAYMENT"
                    v-model="payment.method"
                    @click.stop
                  />
                  <label class="form-check-label w-100 cursor-pointer" for="payOnline">
                    <span class="d-block font-weight-bold text-dark">Online Payment (Credit / Debit Card)</span>
                    <span class="small text-secondary">Pay securely using Visa, MasterCard, or Amex credit/debit cards.</span>
                  </label>
                </div>

                <!-- Card Details fields inside the active state -->
                <transition name="page-fade">
                  <div v-if="payment.method === 'ONLINE_PAYMENT'" class="border-top pt-3 mt-2" @click.stop>
                    <div class="mb-3">
                      <label class="form-label small text-secondary font-weight-bold">Cardholder Name</label>
                      <input type="text" class="bf-input py-2" v-model="payment.cardName" required placeholder="John Doe" />
                    </div>
                    <div class="mb-3">
                      <label class="form-label small text-secondary font-weight-bold">Card Number</label>
                      <input type="text" class="bf-input py-2" v-model="payment.cardNumber" required placeholder="4111 2222 3333 4444" maxlength="19" />
                    </div>
                    <div class="row g-3">
                      <div class="col-6">
                        <label class="form-label small text-secondary font-weight-bold">Expiry Date</label>
                        <input type="text" class="bf-input py-2" v-model="payment.cardExpiry" required placeholder="MM/YY" maxlength="5" />
                      </div>
                      <div class="col-6">
                        <label class="form-label small text-secondary font-weight-bold">CVV Code</label>
                        <input type="password" class="bf-input py-2" v-model="payment.cardCvv" required placeholder="123" maxlength="3" />
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
            </div>

            <div class="d-flex justify-content-between mt-4">
              <button type="button" class="bf-btn bf-btn-ghost" @click="currentStep = 2" :disabled="actionLoading">Back</button>
              <button
                type="button"
                class="bf-btn bf-btn-success font-weight-bold px-4"
                @click="handlePlaceOrder"
                :disabled="actionLoading || !isPaymentValid"
              >
                <span v-if="actionLoading" class="bf-spinner me-2"></span>
                Place Order
              </button>
            </div>
          </div>
        </div>

        <!-- Cost Breakdown Card & Promo Code Card -->
        <div class="col-lg-4">
          <div class="bf-card p-4 bg-white mb-4">
            <h5 class="font-weight-bold text-dark mb-4">Order Summary</h5>

            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span>Subtotal</span>
              <span class="font-weight-bold text-dark">{{ formattedSubtotal }}</span>
            </div>

            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span>Discount ({{ appliedDiscountPercent }}%)</span>
              <span class="text-success font-weight-bold">- {{ formattedDiscount }}</span>
            </div>

            <hr />

            <div class="d-flex justify-content-between mb-4">
              <span class="font-weight-bold text-dark">Grand Total</span>
              <span class="bf-price-amount mb-0" style="font-size: 1.5rem;">{{ formattedTotal }}</span>
            </div>

            <!-- Proceed buttons -->
            <button
              v-if="currentStep === 1"
              class="bf-btn bf-btn-primary w-100 py-3"
              @click="currentStep = 2"
              :disabled="actionLoading"
            >
              Proceed to Checkout
            </button>
            <button
              v-else-if="currentStep === 2"
              class="bf-btn bf-btn-primary w-100 py-3"
              @click="currentStep = 3"
              :disabled="!isDeliveryValid"
            >
              Proceed to Payment
            </button>
            <button
              v-else
              class="bf-btn bf-btn-success w-100 py-3"
              @click="handlePlaceOrder"
              :disabled="actionLoading || !isPaymentValid"
            >
              <span v-if="actionLoading" class="bf-spinner me-2"></span>
              Place Order
            </button>
          </div>

          <!-- Promo Code Card (Only on Step 1) -->
          <div class="bf-card p-4 bg-white" v-if="currentStep === 1">
            <h6 class="font-weight-bold text-dark mb-3">Promo Code</h6>
            <div class="d-flex gap-2">
              <input
                type="text"
                class="bf-input"
                v-model="promoCode"
                placeholder="Enter SAVE10 or SAVE50"
                @keyup.enter="applyPromoCode"
              />
              <button class="bf-btn bf-btn-outline" type="button" @click="applyPromoCode">
                Apply
              </button>
            </div>
            <div v-if="promoMessage" :class="{'text-danger': promoError, 'text-success': !promoError}" class="small mt-2 font-weight-bold">
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

      // Delivery Information
      delivery: {
        street: '',
        city: '',
        phone: ''
      },

      // Payment Information
      payment: {
        method: 'CASH_ON_DELIVERY', // default
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
    formattedSubtotal() {
      return formatPrice(this.totals.amount);
    },
    formattedDiscount() {
      return formatPrice(this.totals.discount);
    },
    formattedTotal() {
      return formatPrice(this.totals.finalAmount);
    },
    isDeliveryValid() {
      return (
        this.delivery.street.trim() !== '' &&
        this.delivery.city.trim() !== '' &&
        this.delivery.phone.trim() !== ''
      );
    },
    isPaymentValid() {
      if (this.payment.method === 'CASH_ON_DELIVERY') {
        return true;
      }
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
      this.totals = {
        amount: 0,
        discount: 0,
        finalAmount: 0
      };
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

        // Reset state
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
.bf-steps-tracker {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.bf-steps-line {
  position: absolute;
  top: 50%;
  left: 0;
  height: 2px;
  background: var(--bf-primary);
  z-index: 1;
  transform: translateY(-50%);
  transition: width var(--bf-transition-base);
}

.bf-step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
  position: relative;
}

.bf-step-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--bf-font-size-sm);
  background: var(--bf-bg-secondary);
  border: 2px solid var(--bf-border);
  color: var(--bf-text-secondary);
  transition: all var(--bf-transition-base);
}

.bf-step-label {
  font-size: var(--bf-font-size-xs);
  font-weight: 600;
  color: var(--bf-text-muted);
  margin-top: 8px;
  transition: color var(--bf-transition-base);
}

.bf-step-item.active .bf-step-circle {
  background: var(--bf-primary);
  border-color: var(--bf-primary);
  color: white;
  box-shadow: 0 0 0 4px var(--bf-primary-light);
}

.bf-step-item.active .bf-step-label {
  color: var(--bf-primary);
}

.bf-payment-option {
  border: 1px solid var(--bf-border);
  border-radius: var(--bf-radius-lg);
  padding: 18px;
  cursor: pointer;
  background: var(--bf-bg-secondary);
  transition: all var(--bf-transition-base);
}

.bf-payment-option.active {
  border-color: var(--bf-primary);
  background: var(--bf-primary-light);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.05);
}
</style>
