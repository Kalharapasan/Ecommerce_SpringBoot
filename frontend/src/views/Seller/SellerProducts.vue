<template>
  <div class="bf-page bf-fade-in p-4">
    <div class="container">
      <div class="row mb-4">
        <div class="col-12 text-start">
          <span class="bf-badge bf-badge-warning mb-2">Inventory Management</span>
          <h2 class="bf-section-title">My Listed Products</h2>
          <p class="bf-section-desc">Manage, add, and update products in your storefront catalog.</p>
        </div>
      </div>

      <div class="bf-card p-4 text-start">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="font-weight-bold text-dark">Hardware Catalog</h5>
          <button class="bf-btn bf-btn-primary bf-btn-sm">List New Product</button>
        </div>

        <div class="table-responsive">
          <table class="bf-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Status</th>
                <th class="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colspan="5" class="text-center py-4 text-muted">No products listed. Add some above to start selling!</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SellerProducts'
};
</script>
