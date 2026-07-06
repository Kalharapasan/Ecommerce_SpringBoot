import { createRouter, createWebHistory } from 'vue-router'
import ProductView from '../views/Product/ProductView.vue'
import ProductDetail from '../views/Product/ProductDetail.vue'
import AddProduct from '../views/Product/AddProduct.vue'
import EditProduct from '../views/Product/EditProduct.vue'
import CategoryView from '../views/Category/CategoryView.vue'
import AddCategory from '../views/Category/AddCategory.vue'
import CartView from '../views/Cart/CartView.vue'
import SignIn from '../views/Auth/SignIn.vue'
import SignUp from '../views/Auth/SignUp.vue'
import ProfileView from '../views/Profile/ProfileView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: ProductView
  },
  {
    path: '/product',
    name: 'ProductView',
    component: ProductView
  },
  {
    path: '/product/detail/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/admin/product/add',
    name: 'AddProduct',
    component: AddProduct
  },
  {
    path: '/admin/product/edit/:id',
    name: 'EditProduct',
    component: EditProduct
  },
  {
    path: '/admin/category',
    name: 'CategoryView',
    component: CategoryView
  },
  {
    path: '/admin/category/add',
    name: 'AddCategory',
    component: AddCategory
  },
  {
    path: '/cart',
    name: 'CartView',
    component: CartView
  },
  {
    path: '/signIn',
    name: 'SignIn',
    component: SignIn
  },
  {
    path: '/signUp',
    name: 'SignUp',
    component: SignUp
  },
  {
    path: '/profile',
    name: 'Profile',
    component: ProfileView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
