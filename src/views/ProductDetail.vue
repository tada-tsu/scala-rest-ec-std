<template>
  <v-layout
    justify-center
    mt-3
  >
    <v-flex
      sm8
      v-if="p"
    >
      <v-card>
        <v-card-title>商品</v-card-title>
        <v-card-text>
          <v-layout wrap>
            <v-flex xs12>
              <v-card tile>
                <v-card-title>
                  <h2>{{ this.p.title }}</h2>
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
                  <v-form @submit.prevent="submit">
                    <v-layout nowrap>
                      <v-flex mr-3>
                        <v-text-field
                          label="カートに入れる数"
                          type="number"
                          v-model="number"
                        ></v-text-field>

                      </v-flex>
                      <v-btn
                        :to='{
                    name: "product",
                    params: {
                      product_path: p.path
                    }
                  }'
                        depressed
                        large
                        color="orange"
                        dark
                        @click="submit"
                      >カートに入れる</v-btn>
                    </v-layout>
                  </v-form>
                </v-card-actions>
              </v-card>
            </v-flex>
          </v-layout>
        </v-card-text>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  export default {
    data() {
      return {
        number: 0,
      }
    },
    computed: {
      p() {
        return "products" in this.$store.state ? this.$store.state.products.filter(e => {
          return e.path == this.$route.params.product_path
        })[0] : null
      }
    },
    methods: {
      submit(e){
        this.$http({
          method: "POST",
          url: `/v1/store/cart/${this.p.id}`,
          data: {
            quantity: this.number
          }
        }).then(res=>{
          this.$store.dispatch("cartin", res)
          this.$notify({
                    group: 'default',
          title: 'カートに商品を追加しました',
          text: `${this.p.title} : ${this.p.sale_price || this.p.price}円 \n${this.number}個`,
          type: "success",
                })

                this.number=0

                this.$router.push({
                  name: "products"
                })
        })
      }
    }
  }
</script>
<style lang="scss" scoped>
</style>
