import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
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
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
    },
    {
      path: "/register",
      name: "register",
      component: ()=> import()
    },
    {
      path: "/products",
      name: "products",
      component: ()=> import()
    },
    {
      path: "/product/:product_path",
      name: "product/:product_path",
      component: ()=> import()
    },
    {
      path: "/cart",
      name: "cart",
      component: ()=> import()
    },
    {
      path: "/order/:order_id",
      name: "order/:order_id",
      component: ()=> import()
    },
    {
      path: "/signout",
      name: "signout",
      component: ()=> import()
    },
    {
      path: "/mypage/me",
      name: "mypage.me",
      component: ()=> import()
    },
    {
      path: "/mypage",
      name: "mypage",
      component: ()=> import()
    },
    {
      path: "/search",
      name: "search",
      component: ()=> import()
    },
  ]
})
