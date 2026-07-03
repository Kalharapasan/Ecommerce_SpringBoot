<template>
  <div class="container my-5">
    <h3 class="mb-4 font-weight-bold text-start">Shopping Cart</h3>

    <!-- Order Progression Step Tracker -->
    <div class="row mb-5 justify-content-center" v-if="items.length > 0">
      <div class="col-md-10">
        <div class="d-flex justify-content-between align-items-center position-relative">
          <div class="progress position-absolute top-50 start-0 translate-middle-y w-100" style="height: 2px; z-index: 1;">
            <div class="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
          </div>
          <div class="d-flex flex-column align-items-center position-relative" style="z-index: 2;">
            <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center font-weight-bold" style="width: 35px; height: 35px;">1</div>
            <span class="small font-weight-bold mt-2 text-primary">Cart Review</span>
          </div>
          <div class="d-flex flex-column align-items-center position-relative" style="z-index: 2;">
            <div class="rounded-circle bg-white border text-secondary d-flex align-items-center justify-content-center font-weight-bold shadow-sm" style="width: 35px; height: 35px;">2</div>
            <span class="small font-weight-bold mt-2 text-secondary">Delivery Info</span>
          </div>
          <div class="d-flex flex-column align-items-center position-relative" style="z-index: 2;">
            <div class="rounded-circle bg-white border text-secondary d-flex align-items-center justify-content-center font-weight-bold shadow-sm" style="width: 35px; height: 35px;">3</div>
            <span class="small font-weight-bold mt-2 text-secondary">Secure Payment</span>
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
      <div v-if="items.length === 0" class="col-12 text-center py-5 bg-white rounded shadow-sm">
        <h4 class="text-muted mb-3">Your cart is empty.</h4>
        <router-link to="/product" class="btn btn-primary">Start Shopping</router-link>
      </div>

      <!-- Line Items & Checkout Details -->
      <template v-else>
        <!-- Table of Items -->
        <div class="col-lg-8 mb-4">
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
            
            <button class="btn btn-primary btn-lg w-100 py-3 font-weight-bold" @click="handleCheckout" :disabled="actionLoading">
              <span v-if="actionLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Proceed to Checkout
            </button>
          </div>

          <!-- Promo Code Card -->
          <div class="card shadow-sm border-0 p-4 bg-white rounded">
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
      promoError: false
    };
  },
  computed: {
    formattedSubtotal() {
      return formatPrice(this.totals.amount);
    },
    formattedDiscount() {
      return formatPrice(this.totals.discount);
    },
    formattedTotal() {
      return formatPrice(this.totals.finalAmount);
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
    async handleCheckout() {
      if (!confirm('Would you like to proceed with the checkout? This will finalize your order and clear your cart.')) {
        return;
      }
      this.actionLoading = true;
      try {
        const token = getToken();
        const response = await api.delete(`/cart/clear/${token}`);
        alert(response.data || 'Order Placed Successfully! Your cart has been cleared.');
        this.items = [];
        this.resetTotals();
        updateCartCount(0);
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
</style>
