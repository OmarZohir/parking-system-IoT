/*** API model
 *   General api object where automatically errors are displayed in a toast and Authorization header is added.
 */
import axios from 'axios';
import store from '../store/index';

export const baseUrl = 'http://localhost:8082/api';

const api = axios.create({
    baseURL: baseUrl
});
// intercept all requests to check for any errors
api.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    const { response: { status } } = error;
    if (status === 401) {
            store.dispatch('auth/_logout').then();
    }
    return Promise.reject(error);
});
export default api
