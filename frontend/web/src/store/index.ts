import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

export interface State {
    placeholder?: boolean;
    humidity: number;
}

export default new Vuex.Store<State>({
    state: {
        humidity: 55
    },
    getters: {
        humidity(deviceId) {
            return this.humidity;
        }
    },
    mutations: {

    },
    actions: {

    },
    modules: {}
})
