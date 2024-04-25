const state = {
    modal: {
        show: false,
        type: 'success',
        subject: null,
        content: null,
    }
};

// getters
const getters = {};

// actions
const actions = {
    _showModal({commit}, options){
        console.log(options);
        options.show = true;
        if(!("type" in options)) {
            options.type = "success";
        }
        commit('_setModal', options);
        setTimeout(()=>{
            commit('_setModal', {
                show: false
            });
        }, 3000)
    },
    _closeModal({commit}){
        commit('_setModal', {
            show: false
        });
    }
};

// mutations
const mutations = {
    _setModal(state, modal){
        state.modal = {
            ...state.modal,
            ...modal
        };
    },
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
