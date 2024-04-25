<template>
    <div class="login">
      <div class="flex justify-center items-center min-h-screen p-4 text-center bg-gray-100">
        <div class="bg-blue-500 h-2/5 w-full fixed top-0"></div>
        <div class="inline-block bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-sm sm:w-full sm:p-6" role="dialog" aria-modal="true" aria-labelledby="modal-headline">
          <form  @submit="login(cred)">
            <h2 class="text-xl font-black font-gray-700 mb-10 mt-2 text-center">Sign in to <span class="text-blue-600">Park</span></h2>
            <label class="block text-gray-600 text-uppercase text-sm font-bold mb-2" for="email">
              Email address
            </label>
            <input v-model="cred.username"  type="text" class="bg-gray-200 appearance-none border-2 border-gray-200 rounded w-full py-4 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-purple-500 mb-4" id="email" placeholder="Email address" />
            <label class="block text-gray-600 text-uppercase text-sm font-bold mb-2" for="password">
              Password
            </label>
            <input v-model="cred.password" type="password" class="bg-gray-200 appearance-none border-2 border-gray-200 rounded w-full py-4 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-purple-500 mb-4" id="password" placeholder="Password" />
            <btn :disabled="loading" class="w-full"  @click.native="login(cred)">Sign in</btn>
<!--            <div class="text-center mt-6 text-blue-500"><a>Forgot password?</a></div>-->
            <router-link to="/register" class="text-center mt-6 text-blue-500"><a>No account yet?</a></router-link>
          </form>
        </div>
      </div>
    </div>
</template>
<script>
import {mapActions} from "vuex";
import Btn from "@/components/general/Btn";
export default {
    name: 'Login',
    components: {
        Btn,
    },
    props: [],
    data: function () {
      return {
          cred: {
              username: '',
              password: ''
          },
          loading: false
      }
    },
    methods: {
        ...mapActions('auth', [
            '_login'
        ]),
        login(cred){
            this.loading = true;
            this._login(cred).then((user)=>{
                this.loading = false;
                if(user.role === "ADMIN") {
                  this.$router.push('/admin');
                }
                else {
                  this.$router.push('/');
                }

            }).catch(()=>{
                this.loading = false;
            });
        }
    }
}
</script>
<style scoped lang="scss"></style>
