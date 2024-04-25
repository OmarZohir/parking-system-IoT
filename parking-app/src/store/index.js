import Vue from 'vue';
import Vuex from 'vuex';

import auth from "./modules/auth";
import parking from "./modules/parking";
import events from "./modules/events";
import modal from "./modules/modal";

Vue.use(Vuex);


export default new Vuex.Store({
    modules: {
        auth,
        parking,
        modal,
        events
    },
    state: {}
})
