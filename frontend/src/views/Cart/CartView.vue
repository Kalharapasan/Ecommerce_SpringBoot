<template>
  <div class="container my-5">
    <h3 class="mb-4 font-weight-bold text-start">Shopping Cart</h3>

    <!-- Order Progression Step Tracker -->
    <div class="row mb-5 justify-content-center" v-if="items.length > 0">
      <div class="col-md-10">
        <div class="d-flex justify-content-between align-items-center position-relative">
          <div class="progress position-absolute top-50 start-0 translate-middle-y w-100" style="height: 2px; z-index: 1;">
            <div 
              class="progress-bar" 
              role="progressbar" 
              :style="{ width: progressPercent + '%' }" 
              aria-valuenow="0" 
              aria-valuemin="0" 
              aria-valuemax="100"
            ></div>
          </div>
          <div class="d-flex flex-column align-items-center position-relative" style="z-index: 2;">
            <div 
              class="rounded-circle d-flex align-items-center justify-content-center font-weight-bold" 
              :class="currentStep >= 1 ? 'bg-primary text-white' : 'bg-white border text-secondary shadow-sm'"
              style="width: 35px; height: 35px; transition: all 0.3s;"
            >1</div>
            <span class="small font-weight-bold mt-2" :class="currentStep >= 1 ? 'text-primary' : 'text-secondary'">Cart Review</span>
          </div>
          <div class="d-flex flex-column align-items-center position-relative" style="z-index: 2;">
            <div 
              class="rounded-circle d-flex align-items-center justify-content-center font-weight-bold" 
              :class="currentStep >= 2 ? 'bg-primary text-white' : 'bg-white border text-secondary shadow-sm'"
              style="width: 35px; height: 35px; transition: all 0.3s;"
            >2</div>
            <span class="small font-weight-bold mt-2" :class="currentStep >= 2 ? 'text-primary' : 'text-secondary'">Delivery Info</span>
          </div>
          <div class="d-flex flex-column align-items-center position-relative" style="z-index: 2;">
            <div 
              class="rounded-circle d-flex align-items-center justify-content-center font-weight-bold" 
              :class="currentStep >= 3 ? 'bg-primary text-white' : 'bg-white border text-secondary shadow-sm'"
              style="width: 35px; height: 35px; transition: all 0.3s;"
            >3</div>
            <span class="small font-weight-bold mt-2" :class="currentStep >= 3 ? 'text-primary' : 'text-secondary'">Secure Payment</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading Spinner -->
    <div v-if="loading && items.length === 0" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading your cart...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger text-start" role="alert">
      {{ error }}
    </div>

    <!-- Cart Layout -->
    <div v-else class="row">
      <!-- Empty Cart State -->
      <div v-if="items.length === 0" class="col-12 text-center py-5 bg-white rounded shadow-sm border">
        <h4 class="text-muted mb-3">Your cart is empty.</h4>
        <router-link to="/product" class="btn btn-primary">Start Shopping</router-link>
      </div>

      <!-- Line Items & Checkout Details -->
      <template v-else>
        <!-- STEP 1: CART REVIEW -->
        <div v-if="currentStep === 1" class="col-lg-8 mb-4">
          <div class="card shadow-sm border-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                  <tr>
                    <th scope="col" class="border-0 text-start">Product Details</th>
                    <th scope="col" class="border-0">Quantity</th>
                    <th scope="col" class="border-0 text-start">Total Price</th>
                    <th scope="col" class="border-0">Actions</th>
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
        <div v-else-if="currentStep === 2" class="col-lg-8 mb-4 text-start">
          <div class="card shadow-sm border-0 p-4 bg-white rounded">
            <h5 class="font-weight-bold mb-4">Delivery Information</h5>
            <form @submit.prevent="currentStep = 3">
              <div class="form-group mb-3">
                <label for="addressStreet" class="form-label">Street Address</label>
                <input 
                  type="text" 
                  id="addressStreet" 
                  class="form-control" 
                  v-model="delivery.street" 
                  required 
                  placeholder="e.g., 123 Galle Road"
                />
              </div>
              <div class="row">
                <div class="col-md-6 form-group mb-3">
                  <label for="addressCity" class="form-label">City</label>
                  <input 
                    type="text" 
                    id="addressCity" 
                    class="form-control" 
                    v-model="delivery.city" 
                    required 
                    placeholder="e.g., Colombo"
                  />
                </div>
                <div class="col-md-6 form-group mb-3">
                  <label for="addressPhone" class="form-label">Phone Number</label>
                  <input 
                    type="tel" 
                    id="addressPhone" 
                    class="form-control" 
                    v-model="delivery.phone" 
                    required 
                    placeholder="e.g., 0771234567"
                  />
                </div>
              </div>
              <div class="d-flex justify-content-between mt-4">
                <button type="button" class="btn btn-outline-secondary px-4" @click="currentStep = 1">Back</button>
                <button type="submit" class="btn btn-primary px-4" :disabled="!isDeliveryValid">Proceed to Payment</button>
              </div>
            </form>
          </div>
        </div>

        <!-- STEP 3: SECURE PAYMENT -->
        <div v-else-if="currentStep === 3" class="col-lg-8 mb-4 text-start">
          <div class="card shadow-sm border-0 p-4 bg-white rounded">
            <h5 class="font-weight-bold mb-4">Payment Method</h5>
            
            <div class="mb-4">
              <!-- Cash on Delivery Option -->
              <div 
                class="form-check p-3 border rounded mb-3 d-flex align-items-center cursor-pointer" 
                :class="{'border-primary bg-light': payment.method === 'CASH_ON_DELIVERY'}"
                @click="payment.method = 'CASH_ON_DELIVERY'"
                style="padding-left: 2.5rem !important;"
              >
                <input 
                  class="form-check-input me-3" 
                  type="radio" 
                  name="payMethod" 
                  id="payCod" 
                  value="CASH_ON_DELIVERY" 
                  v-model="payment.method"
                  @click.stop
                />
                <label class="form-check-label w-100 cursor-pointer" for="payCod">
                  <span class="d-block font-weight-bold">Cash on Delivery (COD)</span>
                  <span class="small text-muted">Pay with cash when your package is delivered to your door.</span>
                </label>
              </div>
              
              <!-- Online Payment Option -->
              <div 
                class="form-check p-3 border rounded d-flex flex-column cursor-pointer" 
                :class="{'border-primary bg-light': payment.method === 'ONLINE_PAYMENT'}"
                @click="payment.method = 'ONLINE_PAYMENT'"
                style="padding-left: 2.5rem !important;"
              >
                <div class="d-flex align-items-center w-100">
                  <input 
                    class="form-check-input me-3" 
                    type="radio" 
                    name="payMethod" 
                    id="payOnline" 
                    value="ONLINE_PAYMENT" 
                    v-model="payment.method"
                    @click.stop
                  />
                  <label class="form-check-label w-100 cursor-pointer" for="payOnline">
                    <span class="d-block font-weight-bold">Online Payment (Credit / Debit Card)</span>
                    <span class="small text-muted">Pay securely using your Visa, MasterCard, or Amex card.</span>
                  </label>
                </div>
                
                <!-- Mock Card Form -->
                <div v-if="payment.method === 'ONLINE_PAYMENT'" class="mt-3 pt-3 border-top w-100" @click.stop>
                  <div class="form-group mb-3">
                    <label class="form-label small text-secondary font-weight-bold">Cardholder Name</label>
                    <input type="text" class="form-control form-control-sm" v-model="payment.cardName" required placeholder="John Doe">
                  </div>
                  <div class="form-group mb-3">
                    <label class="form-label small text-secondary font-weight-bold">Card Number</label>
                    <input type="text" class="form-control form-control-sm" v-model="payment.cardNumber" required placeholder="4111 2222 3333 4444" maxlength="19">
                  </div>
                  <div class="row">
                    <div class="col-6 form-group mb-3">
                      <label class="form-label small text-secondary font-weight-bold">Expiry Date</label>
                      <input type="text" class="form-control form-control-sm" v-model="payment.cardExpiry" required placeholder="MM/YY" maxlength="5">
                    </div>
                    <div class="col-6 form-group mb-3">
                      <label class="form-label small text-secondary font-weight-bold">CVV</label>
                      <input type="password" class="form-control form-control-sm" v-model="payment.cardCvv" required placeholder="123" maxlength="3">
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="d-flex justify-content-between mt-4">
              <button type="button" class="btn btn-outline-secondary px-4" @click="currentStep = 2" :disabled="actionLoading">Back</button>
              <button 
                type="button" 
                class="btn btn-success px-4 font-weight-bold" 
                @click="handlePlaceOrder" 
                :disabled="actionLoading || !isPaymentValid"
              >
                <span v-if="actionLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                Place Order
              </button>
            </div>
          </div>
        </div>

        <!-- Cost Breakdown Card & Promo Code Card -->
        <div class="col-lg-4">
          <div class="card shadow-sm border-0 p-4 bg-white rounded mb-4">
            <h5 class="font-weight-bold mb-4 text-start">Order Summary</h5>
            
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Subtotal</span>
              <span class="font-weight-bold text-dark">{{ formattedSubtotal }}</span>
            </div>
            
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Discount ({{ appliedDiscountPercent }}%)</span>
              <span class="text-success font-weight-bold">- {{ formattedDiscount }}</span>
            </div>
            
            <hr />
            
            <div class="d-flex justify-content-between mb-4">
              <span class="font-weight-bold">Grand Total</span>
              <span class="h5 font-weight-bold text-primary mb-0">{{ formattedTotal }}</span>
            </div>
            
            <!-- Dynamic Proceed Buttons -->
            <button 
              v-if="currentStep === 1" 
              class="btn btn-primary btn-lg w-100 py-3 font-weight-bold" 
              @click="currentStep = 2" 
              :disabled="actionLoading"
            >
              Proceed to Checkout
            </button>
            <button 
              v-else-if="currentStep === 2" 
              class="btn btn-primary btn-lg w-100 py-3 font-weight-bold" 
              @click="currentStep = 3" 
              :disabled="!isDeliveryValid"
            >
              Proceed to Payment
            </button>
            <button 
              v-else 
              class="btn btn-success btn-lg w-100 py-3 font-weight-bold" 
              @click="handlePlaceOrder" 
              :disabled="actionLoading || !isPaymentValid"
            >
              <span v-if="actionLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Place Order
            </button>
          </div>

          <!-- Promo Code Card (Only on Step 1) -->
          <div class="card shadow-sm border-0 p-4 bg-white rounded" v-if="currentStep === 1">
            <h6 class="font-weight-bold mb-3 text-start">Promo Code</h6>
            <div class="input-group mb-2">
              <input 
                type="text" 
                class="form-control" 
                v-model="promoCode" 
                placeholder="Enter SAVE10 or SAVE50" 
                @keyup.enter="applyPromoCode"
              />
              <button class="btn btn-outline-primary" type="button" @click="applyPromoCode">
                Apply
              </button>
            </div>
            <div v-if="promoMessage" :class="{'text-danger': promoError, 'text-success': !promoError}" class="small text-start">
              {{ promoMessage }}
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import api, { extractErrorMessage, formatPrice } from '../../utils/api';
import { getToken, getCurrentUser, updateCartCount } from '../../utils/auth';
import CartItemRow from '../../components/Cart/CartItemRow.vue';

export default {
  name: 'CartView',
  components: { CartItemRow },
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
        this.error = 'User token not found. Please log in.';
        this.loading = false;
        return;
      }

      try {
        // Fetch cart items
        const response = await api.get(`/cart/${token}`);
        this.items = response.data.data || [];
        
        // Fetch calculations if there are items in the cart
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
          
          // Recalculate global cart badge items count
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
        alert('Failed to update quantity: ' + extractErrorMessage(err));
      } finally {
        this.actionLoading = false;
      }
    },
    async handleRemoveItem(cartId) {
      this.actionLoading = true;
      try {
        await api.delete(`/cart/${cartId}`);
        await this.fetchCartData();
      } catch (err) {
        alert('Failed to remove item: ' + extractErrorMessage(err));
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
        alert('Please complete the delivery details first.');
        this.currentStep = 2;
        return;
      }
      if (!this.isPaymentValid) {
        alert('Please complete the payment details first.');
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
        alert(response.data.message || 'Order Placed Successfully! Your cart has been cleared.');
        
        // Reset state
        this.items = [];
        this.resetTotals();
        updateCartCount(0);
        this.currentStep = 1;
        this.delivery = { street: '', city: '', phone: '' };
        this.payment = { method: 'CASH_ON_DELIVERY', cardName: '', cardNumber: '', cardExpiry: '', cardCvv: '' };
        
        // Redirect to profile to see order history
        this.$router.push({ name: 'Profile' });
      } catch (err) {
        alert('Checkout failed: ' + extractErrorMessage(err));
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
.card {
  border-radius: 12px;
}
.cursor-pointer {
  cursor: pointer;
}
.bg-light {
  background-color: rgba(13, 110, 253, 0.05) !important;
}
</style>
