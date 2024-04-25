import Vue from 'vue'
import App from './App.vue'
import router from "@/router";
import store from "@/store";
import VueSSE from 'vue-sse';

import "@/assets/scss/styles.scss"


Vue.config.productionTip = false

Vue.use(VueSSE);

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
