import Vue from 'vue'
import Vuex from 'vuex'

import Cookies from "js-cookie";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        loggedin: false,
        token: "",
        token_expired_at: "",
        user: "",
        products: [],
        cart: {},
    },
    mutations: {
        getProducts(state, res) {
            state.products = res.data
        },
        loggedIn(state) {
            state.loggedin = true
        },
        loggedOut(state) {
            state.loggedin = false
        },

        cartin(state, res) {
            state.cart = res.data
        }
    },
    actions: {
        getProducts({
            commit
        }) {
            this._vm.$http({
                method: "GET",
                url: "/v1/store/product"
            }).then(res => {
                commit("getProducts", res)
            })
        },

        login({
            commit,
            dispatch,
            state,
        }, {
            login,
            password,
        }) {
            this._vm.$http({
                url: "/v1/login/",
                method: "POST",
                data: {
                    "login": login,
                    "pass": password,
                },

            }).then(res => {
                state.token = res.data.token
                state.token_expired_at = res.data.token_expired_at
                state.user = res.data.user

                this._vm.$http.defaults.headers.common["Authorization"] = `Bearer ${res.data.token}`
                this._vm.$http.defaults.headers.common["Authorization-User-Screen"] = `${res.data.user.screen_name}`

                Cookies.set("token", res.data.token, {
                    expires: new Date(res.data.token_expired_at)
                })
                Cookies.set("screen_name", res.data.user.screen_name)

                dispatch("checkLogin")



            })
        },

        checkLogin({
            commit,
            dispatch,
        }) {
            return new Promise(resolve=>{
                if (
                    Cookies.get("token") &&
                    Cookies.get("screen_name")
                ) {
                    this._vm.$http.defaults.headers.common["Authorization"] = `Bearer ${Cookies.get("token")}`
                    this._vm.$http.defaults.headers.common["Authorization-User-Screen"] = `${Cookies.get("screen_name")}`
                }
    
                this._vm.$http({
                    method: "GET",
                    url: "/v1/me/",
                }).then(res => {
                    if (res.status == 200) {
                        commit("loggedIn")
                        resolve(true)
                    } else {
                        commit("loggedOut")
                        resolve(false)
                    }
                })
            })
        },

        getCart({commit,dispatch}){
            this._vm.$http({
                method: "GET",
                url: "/v1/store/cart"
            }).then(res=>{
                dispatch("cartin", res)
                this._vm.$notify({
                    group: 'default',
          title: 'カートを更新しました',
          type: "success",
                })
            })
        },

        cartin({
            commit,
            dispatch,
        }, res) {
            commit("cartin", res)
        },
    }
})
