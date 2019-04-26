<template>
  <v-form @submit.prevent="submit">
    <v-container>
      <v-layout
        fluid
        justify-center
      >
        <v-flex xs10>

          <v-card>
            <v-card-text>
              <v-flex xs12>
                <v-text-field
                  v-model="login"
                  label="ログインID / メールアドレス"
                  required
                ></v-text-field>
              </v-flex>

              <v-flex
                xs12
                mt-4
              >
                <v-text-field
                  v-model="password"
                  label="パスワード"
                  required
                ></v-text-field>
              </v-flex>

              <v-layout>
                <v-flex xs5>
                  <v-btn
                    block
                    color="primary"
                    @click.prevent="submit"
                  >ログイン</v-btn>
                </v-flex>
                <v-flex
                  xs5
                  offset-xs2
                >
                  <v-btn
                    block
                    color="secondary"
                  >新規登録</v-btn>
                </v-flex>
              </v-layout>
            </v-card-text>

          </v-card>
        </v-flex>

      </v-layout>
    </v-container>
  </v-form>
</template>

<script>
  export default {
    data() {
      return {
        login: "",
        password: "",
      }
    },
    methods: {
      submit(e){
        this.$store.dispatch("login", {
          login: this.login,
          password: this.password,
          })
      }
    }
  }
</script>

<style lang="scss" scoped>
</style>
