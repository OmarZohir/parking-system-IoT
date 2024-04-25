import api from '../../api'
const state = {
    reservations: [],
    spots: [],
    entries: [],
    loading: {
        spots: false,
        reservations: false,
        entries: false,
        rate: false,
        makeReservation: false,
        deleteReservation: false
    }
};

// getters
const getters = {
    availableSpots: state => {
        return state.spots.filter(spot=>spot.state === "Free");
    }
};

// actions
const actions = {
    //Get all themes
    _getParkingSpots ({ commit }) {
        return new Promise((resolve, reject)=> {
            commit('_updateLoading', {
                spots: true
            })
            api
                .get('/spots')
                .then(response => {
                    console.log(response);
                    commit('_setSpots', response.data);
                    commit('_updateLoading', {
                        spots: false
                    })
                }).catch(error => {
                reject();
                commit('_updateLoading', {
                    spots: false
                })
                console.error(error);
            });
        });
    },
    _getReservations ({ commit }) {
        return new Promise((resolve, reject)=> {
            commit('_updateLoading', {
                reservations: true
            })
            api
                .get('/reservations')
                .then(response => {
                    commit('_setReservations', response.data);
                    commit('_updateLoading', {
                        reservations: false
                    })
                }).catch(error => {
                commit('_updateLoading', {
                    reservations: false
                });
                reject();
                console.error(error);
            });
        });
    },
    _deleteReservation({commit, dispatch}, reservation) {
        return new Promise((resolve, reject)=> {
            commit('_updateLoading', {
                deleteReservation: true
            });
            commit('_deleteReservation', reservation.id);
            api
                .delete('/reservations/'+reservation.id)
                .then(response => {
                    commit('_updateLoading', {
                        deleteReservation: false
                    })
                    dispatch('modal/_showModal', {
                        title: "Reservation is deleted",
                        content: "",
                    }, {root: true})
                    resolve(response.data);
                }).catch(error => {
                    let message = "Unknown error"
                    if (error.response) {
                        message = error.response.data.message;
                    }
                    dispatch('modal/_showModal', {
                        title: "Error when deleting reservation",
                        content: message,
                        type: "error"
                    }, {root: true})
                    commit('_updateLoading', {
                        deleteReservation: false
                    });
                    reject();
                });
        });
    },
    _makeReservation ({ commit, dispatch }, reservation) {
        return new Promise((resolve, reject)=> {
            commit('_updateLoading', {
                reservations: true
            })
            api
                .post('/reservations', reservation)
                .then(response => {
                    commit('_updateLoading', {
                        reservations: false
                    })
                    dispatch('modal/_showModal', {
                        title: "Reservation is made",
                        content: "",
                    }, {root: true})
                    resolve(response.data);
                }).catch(error => {
                let message = "Unknown error"
                if(error.response) {
                    message = error.response.data.cause1;
                }
                dispatch('modal/_showModal', {
                    title: "Error when making reservation",
                    content: message,
                    type: "error"
                }, {root: true})

                commit('_updateLoading', {
                    reservations: false
                });

                reject();
            });
        });
    },
    _getEntries({ commit }) {
        return new Promise((resolve, reject)=> {
            commit('_updateLoading', {
                entries: true
            })
            api
                .get('/reservations/entries')
                .then(response => {
                    commit('_setEntries', response.data);
                    commit('_updateLoading', {
                        entries: false
                    })
                }).catch(error => {
                commit('_updateLoading', {
                    entries: false
                });
                reject();
                console.error(error);
            });
        });
    },
    _updateRate({commit, dispatch}, spot) {
        return new Promise((resolve, reject)=> {
            commit('_updateLoading', {
                rate: true
            });
            commit('_updateSpot', spot);
            const data = {
                spotId: spot.id,
                pricePerMinute: spot.rate
            }
            api
                .post('/reservations/rate', data)
                .then(() => {
                    dispatch('modal/_showModal', {
                        title: "Rate is updated",
                        content: "",
                    }, {root: true})
                    commit('_updateLoading', {
                        rate: false
                    })
                }).catch(error => {
                commit('_updateLoading', {
                    rate: false
                });
                dispatch('modal/_showModal', {
                    title: "Error when updating rate",
                    type: "error"
                }, {root: true})
                reject();
                console.error(error);
            });
        });
    }
};


// mutations
const mutations = {
    _setSpots(state, spots){
        console.log(spots);
        state.spots = spots;
    },
    _addSpot(state, spot) {
        state.spots.push(spot);
    },
    _updateSpot(state, spot) {
        const item = state.spots.find(item => item.endpoint === spot.endpoint);
        if(item) {
            Object.assign(item, spot);
        }
    },
    _removeSpot(state, spot) {
        let index = state.spots.map(s=>s.id).indexOf(spot.id)
        if(index !== -1){
            state.spots.splice(index, 1);
        }

    },
    _setReservations(state, reservations){
        state.reservations = reservations;
    },
    _deleteReservation(state, reservationId){
        let index = state.reservations.map(r=>r.id).indexOf(reservationId)
        if(index !== -1){
            state.reservations.splice(index, 1);
        }
    },
    _updateReservation(state, reservation) {
        const item = state.reservations.find(item => item.id === reservation.id);
        if (item) {
            Object.assign(item, reservation);
        }
    },
    _setEntries(state, entries) {
        state.entries = entries;
    },
    _updateEntry(state, entry) {
        console.log("Updating entry", entry);
        const item = state.entries.find(item => item.id === entry.id);
        console.log(item);
        if (item) {
            Object.assign(item, entry);
        }
    },
    _addEntry(state, entry) {
        state.entries.push(entry);
    },
    _updateLoading(state, loading){
        state.loading = {
            ...state.loading,
            ...loading
        };
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
