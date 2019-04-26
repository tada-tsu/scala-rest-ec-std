<template>
  <v-layout>
    <v-flex
      xs10
      offset-xs1
    >
      <v-card>
        <v-card-title>
          カート
          <v-spacer></v-spacer>
          <span v-if="'items' in $store.state.cart">{{ $store.state.cart.amount }}円</span>
        </v-card-title>
        <template v-if="'items' in $store.state.cart">
          <v-card-text>

            <v-card
              v-for='c in $store.state.cart.items'
              :key="c.product_id"
            >

              <v-card-text>
                id: {{ c.product_id }} の商品 x {{ c.quantity }}
              </v-card-text>
            </v-card>
          </v-card-text>

          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              :to='{
            
//            name: "order"
          }'
              @click="disabled"
              color="orange"
              dark
            >購入</v-btn>
          </v-card-actions>
        </template>
        <v-card-text v-else>
          <p
            m-4
            class="text-xs-center"
          >
            カートに商品が存在しません
          </p>
        </v-card-text>

      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  export default {
    methods: {
      disabled() {
        this.$notify({
          group: 'default',
          title: '利用不可能',
          text: '未実装です',
          type: "warn",
        });

      }
    }
  }
</script>

<style>
</style>
