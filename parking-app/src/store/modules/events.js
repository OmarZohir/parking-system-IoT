import Vue from 'vue'
import {baseUrl} from "@/api";

const state = {
    server: null,
};

// getters
const getters = {};

// actions
const actions = {
    //Get all themes
    _startServer ({ commit }) {
        return new Promise((resolve, reject)=> {
            Vue.SSE(baseUrl + '/events', { format: 'json' })
                .then(sse => {
                    // Store SSE object at a higher scope
                    commit('_setServer', sse);

                    // Catch any errors (ie. lost connections, etc.)
                    sse.onError(e => {
                        console.error('lost connection; giving up!', e);
                    });

                    // Listen for messages without a specified event
                    sse.subscribe('', (message, rawEvent) => {
                        console.warn('Received a message w/o an event!', rawEvent);
                        console.warn('Received a message w/o an event!', message);
                    });

                    // Listen for messages based on their event (in this case, "chat")
                    sse.subscribe('spot_added', (spot) => {
                       commit('parking/_addSpot', spot, {root:true});
                    });
                    sse.subscribe('spot_updated', (spot) => {
                        commit('parking/_updateSpot', spot, {root:true});
                    });
                    sse.subscribe('spot_removed', (spot) => {
                        commit('parking/_removeSpot', spot, {root:true});
                    });
                    sse.subscribe('reservation_updated', (reservation) => {
                        commit('parking/_updateReservation', reservation, {root:true});
                    });
                    sse.subscribe('entry_added', (entry) => {
                        commit('parking/_addEntry', entry, {root:true});
                    });
                    sse.subscribe('entry_updated', (entry) => {
                        commit('parking/_updateEntry', entry, {root:true});
                    });
                    resolve(sse);
                    //sse.unsubscribe('chat');
                })
                .catch(err => {
                    console.error('Failed to connect to server', err);
                    reject(err);
                });
        });
    },
    _stopServer ({ state }) {
        state.server.close();
    },
};


// mutations
const mutations = {
    _setServer(state, server){
        state.server = server;
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
