import api from '../../api'
import router from '../../router';
import {encrypt, decrypt} from "@/helpers/encryption";


const state = {
    user: null,
    token: null,
    loading: {
        loggingIn: false,
        resettingPassword: false
    }
};

// getters
const getters = {};

// actions
const actions = {
    //Check if user is still logged in and refresh the access token.
    _loggedIn({commit, dispatch}){
        return new Promise((resolve, reject) => {
            if (state.token !== null) {
                resolve();
            } else {
                const encryptedAccessToken = localStorage.getItem('access_token');
                if (encryptedAccessToken !== null) {
                    const accessToken = decrypt(encryptedAccessToken);
                    dispatch('_init', accessToken).then(resolve).catch(reject);

                } else {
                    commit('_setToken', false);
                    reject();
                }
            }
        });
    },
    _fetchAccessToken({dispatch}){
        return new Promise((resolve, reject) => {
            const encryptedRefreshToken = localStorage.getItem("refresh_token");
            if (encryptedRefreshToken !== null) {
                const refreshToken = decrypt(encryptedRefreshToken);
                //Prepare the data for token request
                const data = {
                    grant_type: 'refresh_token',
                    client_id: process.env.VUE_APP_CLIENTID,
                    client_secret: process.env.VUE_APP_CLIENTSECRET,
                    refresh_token: refreshToken
                };
                api.post( '/oauth/v2/token', data).then(response => {
                    console.log(response);
                    //Store encrypted tokens in local storage
                    const encryptedRefreshToken = encrypt(response.data.refresh_token);
                    const encryptedAccessToken = encrypt(response.data.access_token);
                    localStorage.setItem("refresh_token", encryptedRefreshToken);
                    localStorage.setItem("access_token", encryptedAccessToken);
                    //Set Authorization header for future requests
                    dispatch('_init', response.data.access_token).then(resolve).catch(reject);
                }).catch(error => {
                    reject(error);
                });
            }
            else {
                reject();
            }

        });
    },
    //Login user
    _login ({dispatch, state}, cred) {
        console.log(cred);
        return new Promise((resolve, reject) => {
            //Request token with credentials from server
            api.post('/auth/login', cred).then(response => {
                console.log(response);
                const accessToken = response.data;

                //Save encrypted refresh token in localstorage
                const encryptedAccessToken = encrypt(accessToken.token);
                localStorage.setItem("access_token", encryptedAccessToken);
                //Add authorization header for future requests
                dispatch('_init', accessToken.token).then(()=>{
                    resolve(state.user);
                }).catch(reject);

            }).catch((error) => {
                console.log(error);
                reject(error.response.data);
            });
        });
    },
    _register({dispatch}, cred){
        return new Promise((resolve, reject) => {
            api.post('/auth/register', cred).then(response => {
                //Save encrypted refresh token in localstorage
                const encryptedAccessToken = encrypt(response.data.token);
                localStorage.setItem("access_token", encryptedAccessToken);
                //Add authorization header for future requests
                dispatch('_init', response.data.token).then(()=>{
                    resolve(response.data);
                }).catch(reject);

            }).catch((error) => {
                console.log(error);
                reject(error.response.data);
            });
        });
    },
    //Forgot your password
    _resetPassword ({commit}, username) {
        return new Promise((resolve, reject) => {
            commit('_updateLoading', {
                loggingIn: true
            })
            const data = {
                username: username,
            };
            api.post('/reset', data).then(response => {
                if(response.status === 204) {
                    resolve(response.data);
                }
            }).catch((error) => {
                reject(error.response.data);
            });
        });
    },
    //Logout user
    _logout({commit}){
        return new Promise((resolve) => {
            //Remove from local storage
            localStorage.removeItem("refresh_token");
            localStorage.removeItem("access_token");
            //Remove user from store
            commit('_setUser', null);
            commit('_setToken', false);
            router.replace('/login');
            resolve(true);
        });
    },
    //Retrieve logged in user
    _getUser({commit, state}, force = false){
        return new Promise((resolve, reject) => {
            if(state.user === null || force) {
                api.get('http://localhost:8082/api/auth/me').then(response => {
                    //Set the user state
                    const user = response.data;
                    commit('_setUser', user);
                    resolve(user);
                }).catch((error) => {
                    console.log(error);
                    reject(error.response.data);
                });
            }
            else{
                resolve(state.user);
            }
        });
    },
    _init({commit, dispatch}, token){
        console.log("Initializing", token);
        api.defaults.headers.common['Accept'] = 'application/json';
        api.defaults.headers.common['Authorization'] = token;
        commit('_setToken', token);
        const promises = [
            dispatch('_getUser')
        ];
        return Promise.all(promises);
    }
};

// mutations
const mutations = {
    _setUser(state, user){
        state.user = user;
    },
    _setToken(state, token){
        state.token = token;
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
