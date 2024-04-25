<template>
    <div class="login">
      <div class="flex justify-center items-center min-h-screen p-4 text-center bg-gray-100">
        <div class="bg-blue-500 h-2/5 w-full fixed top-0"></div>
        <div class="inline-block bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-sm sm:w-full sm:p-6" role="dialog" aria-modal="true" aria-labelledby="modal-headline">
          <form  @submit="login(cred)">
            <h2 class="text-xl font-black font-gray-700 mb-10 mt-2 text-center">Sign in to <span class="text-blue-600">Park</span></h2>
            <label class="block text-gray-600 text-uppercase text-sm font-bold mb-2" for="username">
              Username
            </label>
            <input v-model="cred.username"  type="text" class="bg-gray-200 appearance-none border-2 border-gray-200 rounded w-full py-4 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-purple-500 mb-4" id="username" placeholder="Username" />
            <label class="block text-gray-600 text-uppercase text-sm font-bold mb-2" for="name">
              Name
            </label>
            <input v-model="cred.name"  type="text" class="bg-gray-200 appearance-none border-2 border-gray-200 rounded w-full py-4 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-purple-500 mb-4" id="name" placeholder="Name" />
            <label class="block text-gray-600 text-uppercase text-sm font-bold mb-2" for="password">
              Password
            </label>
            <input v-model="cred.password" type="password" class="bg-gray-200 appearance-none border-2 border-gray-200 rounded w-full py-4 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-purple-500 mb-4" id="password" placeholder="Password" />
            <btn :disabled="loading" class="w-full"  @click.native="register(cred)">Register</btn>
          </form>
        </div>
      </div>
    </div>
</template>
<script>
import {mapActions} from "vuex";
import Btn from "@/components/general/Btn";
export default {
    name: 'Register',
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
            '_register'
        ]),
        register(cred){
            this.loading = true;
            this._register(cred).then(()=>{
                this.loading = false;
                this.$router.push('/');
            }).catch(()=>{
                this.loading = false;
            });
        }
    }
}
</script>
<style scoped lang="scss"></style>
