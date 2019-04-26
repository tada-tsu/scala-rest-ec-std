<template>
  <v-layout
    justify-center
    mt-3
  >
    <v-flex sm8>
      <v-card>
        <v-card-title>商品</v-card-title>
        <v-card-text :class='{
        "mode-index": this.mode =="index"
      }'>
          <v-layout wrap>
            <v-flex
              v-for="p in $store.state.products"
              xs4
              :key="p.path"
            >
              <v-card tile>
                <v-card-title>
                  <h2>{{ p.title }}</h2>
                </v-card-title>
                <v-card-text>
                  <p>{{ p.description }}</p>
                  <span v-if="p.sale_price == null">{{ p.price }}</span>
                  <span v-else>{{ p.price }} 割引後 <em>{{ p.sale_price }}</em></span>
                  <span>円</span>
                </v-card-text>
                <v-card-actions>
                  <span>在庫数: {{ p.stock }}</span>
                  <v-spacer></v-spacer>
                  <v-btn
                    :to='{
                    name: "product",
                    params: {
                      product_path: p.path
                    }
                  }'
                    depressed
                  >詳しく</v-btn>
                </v-card-actions>
              </v-card>
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-card-actions v-if="this.mode =='index'">
          <v-spacer></v-spacer>
          <v-btn
            :to='{name: "products"}'
            flat
            large
          >詳しく</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  export default {
    props: [
      "mode",
    ],

  }
</script>
<style lang="scss" scoped>
  .mode-index {
    max-height: 300px;
    overflow: hidden;
    position: relative;
    &::after {
      content: "";
      display: block;
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 60px;
      background-image: linear-gradient(to bottom, rgba(#fff, 0), rgba(#fff, 0.8));
    }
  }
</style>

