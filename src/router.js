import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'

Vue.use(Router)

export default new Router({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/login',
      name: 'login',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('./views/Login.vue')
    },
    {
      path: "/register",
      name: "register",
      component: ()=> import('./views/About.vue')
    },
    {
      path: "/products",
      name: "products",
      component: ()=> import('./views/Products.vue')
    },
    {
      path: "/product/:product_path",
      name: "product",
      component: ()=> import('./views/ProductDetail.vue')
    },
    {
      path: "/cart",
      name: "cart",
      component: ()=> import('./views/Cart.vue')
    },
    {
      path: "/order/:order_id",
      name: "order",
      component: ()=> import('./views/About.vue')
    },
    {
      path: "/signout",
      name: "signout",
      component: ()=> import('./views/About.vue')
    },
    {
      path: "/mypage/me",
      name: "mypage.me",
      component: ()=> import('./views/MyPage.vue')
    },
    {
      path: "/mypage",
      name: "mypage",
      component: ()=> import('./views/About.vue')
    },
    {
      path: "/search",
      name: "search",
      component: ()=> import('./views/About.vue')
    },
  ]
})
