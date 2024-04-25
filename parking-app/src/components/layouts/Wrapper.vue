<template>
  <div class="min-h-screen bg-gray-100">
    <div class="bg-blue-600 pb-32">
      <nav class="bg-blue-600 border-b border-blue-300 border-opacity-25 lg:border-none">
        <div class="max-w-7xl mx-auto px-2 sm:px-4 lg:px-8">
          <div class="relative h-16 flex items-center justify-between lg:border-b lg:border-blue-400 lg:border-opacity-25">
            <div class="px-2 flex items-center lg:px-0">
              <div class="flex-shrink-0">
                <h1 class="text-xl font-bold text-white">Park</h1>
              </div>
              <div class="hidden lg:block lg:ml-10">
                <div class="flex space-x-4">
                  <router-link to="/" v-bind:class="{'bg-blue-700': route==='home'}" class="text-white hover:bg-blue-500 hover:bg-opacity-75 rounded-md py-2 px-3 text-sm font-medium">
                    Reservations
                  </router-link>
                  <router-link to="/make-reservation" v-bind:class="{'bg-blue-700': route==='make-reservation'}" class="text-white hover:bg-blue-500 hover:bg-opacity-75 rounded-md py-2 px-3 text-sm font-medium">
                    Make a reservation
                  </router-link>
                </div>
              </div>
            </div>
            <div class="flex lg:hidden">
              <button @click="menu.mobile = !menu.mobile" class="bg-blue-600 p-2 rounded-md inline-flex items-center justify-center text-blue-200 hover:text-white hover:bg-blue-500 hover:bg-opacity-75 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-blue-600 focus:ring-white" aria-expanded="false">
                <span class="sr-only">Open main menu</span>
                <svg class="block h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
                <svg class="hidden h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <div class="hidden lg:block lg:ml-4">
              <div class="flex items-center">
                <!-- Profile dropdown -->

                <div class="ml-3 relative flex-shrink-0">
                  <div>

                    <button @click="menu.profile = !menu.profile" class="flex items-center bg-blue-600 rounded-full flex text-sm text-white focus:outline-none">
                      <span class="sr-only">Open user menu</span>
                      <img class="rounded-full h-8 w-8" src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80" alt="">
                      <div class="ml-3 text-left">
                        <div class="text-base font-medium text-white">{{user.name}}</div>
<!--                        <div class="text-sm font-medium text-blue-300">tom@example.com</div>-->
                      </div>
                    </button>
                  </div>
                  <!--
                    Profile dropdown panel, show/hide based on dropdown state.

                    Entering: "transition ease-out duration-100"
                      From: "transform opacity-0 scale-95"
                      To: "transform opacity-100 scale-100"
                    Leaving: "transition ease-in duration-75"
                      From: "transform opacity-100 scale-100"
                      To: "transform opacity-0 scale-95"
                  -->
                  <div v-if="menu.profile" class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5" role="menu" aria-orientation="vertical" aria-labelledby="user-menu">
                    <a href="#" @click="logout()" class="block py-2 px-4 text-sm text-gray-700 hover:bg-gray-100" role="menuitem">
                      Sign out
                    </a>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </div>
        <div class="lg:hidden" v-bind:class="{'hidden': !menu.mobile, 'block': menu.mobile}">
          <div class="px-2 pt-2 pb-3 space-y-1">
            <router-link to="/foo" v-bind:class="{'bg-blue-700': route==='home'}" class="text-white hover:bg-blue-500 hover:bg-opacity-75 block rounded-md py-2 px-3 text-base font-medium">
              Reservations
            </router-link>
            <router-link to="/foo" v-bind:class="{'bg-blue-700': route==='create-reservation'}" class="text-white hover:bg-blue-500 hover:bg-opacity-75 block rounded-md py-2 px-3 text-base font-medium">
              Make a reservation
            </router-link>
          </div>
          <div class="pt-4 pb-3 border-t border-blue-700">
            <div class="px-5 flex items-center">
              <div class="flex-shrink-0">
                <img class="rounded-full h-10 w-10" src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80" alt="">
              </div>
              <div class="ml-3">
                <div class="text-base font-medium text-white">{{ user.name }}</div>
<!--                <div class="text-sm font-medium text-blue-300">tom@example.com</div>-->
              </div>
            </div>
            <div class="mt-3 px-2 space-y-1">
              <a @click="logout()" href="#" class="block rounded-md py-2 px-3 text-base font-medium text-white hover:bg-blue-500 hover:bg-opacity-75">
                Sign out
              </a>
            </div>
          </div>
        </div>
      </nav>
      <header class="py-10">
        <div class="flex justify-between max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 class="text-3xl font-bold text-white">
            {{title}}
          </h1>
          <router-link to="/make-reservation" v-if="showButton" class="inline-flex items-center p-3 shadow-sm text-base font-medium rounded-md text-blue-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
            <plus-icon/> Reservation
          </router-link>
        </div>
      </header>
    </div>
    <main class="-mt-32">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import {PlusIcon} from "@vue-hero-icons/outline";
import {mapState} from "vuex";

export default {
  name: "Wrapper",
  components: {
    PlusIcon
  },
  data(){
    return {
      menu: {
        profile: false,
        mobile: false
      }
    }
  },
  computed: {
    title(){
      return this.$route.meta.title || this.$route.name;
    },
    route(){
      return this.$route.name;
    },
    showButton() {
      return !!this.$route.meta.showButton;
    },
    ...mapState({
      user: state => state.auth.user
    })
  },
  methods: {
    logout() {
      this.$router.replace("/login");
    }
  }
}
</script>

<style scoped>

</style>
