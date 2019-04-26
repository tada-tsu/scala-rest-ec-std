<template>
  <v-app>
    <v-toolbar app>
      <v-toolbar-title class="headline text-uppercase">
<v-btn 
        :to='{
          name: "home"
        }' 
        flat
        exact
        v-if="$route.name != 'home'"
        >
          <span class="font-weight-light">サンプルECサイト</span>
        </v-btn>
          <span v-else class="font-weight-light">サンプルECサイト</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn flat large dark color="blue darken-2" :to='{
        name: "cart"
      }'>
        カート
        <v-icon>mdi-cart</v-icon>
      </v-btn>
      <v-btn
        flat
        :to='{
          name: $store.state.loggedin ? "mypage.me" : "login"
        }'
      >
        <span class="mr-2" v-if="$store.state.loggedin">マイページ</span>
        <span class="mr-2" v-else>ログイン</span>
        <v-icon>mdi-open-in-new</v-icon>
      </v-btn>
    </v-toolbar>

    <v-content>
      <router-view/>
    </v-content>
    <notifications group="default" position="bottom right" />
  </v-app>
</template>

<script>

export default {
  name: 'App',
  data () {
    return {
      //
    }
  },
  mounted(){
    this.$store.dispatch("getProducts")
    this.$store.dispatch("checkLogin").then(loggedIn=>{
      if(loggedIn){
        this.$store.dispatch("getCart")
      }
    })
    
  },
}
</script>
