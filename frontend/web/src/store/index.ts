import Vue from 'vue'
import Vuex from 'vuex'
import charts from './modules/charts';
import devices from './modules/devices';
import settings from './modules/settings';

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
    modules: {
        devices: devices,
        charts: charts,
        settings: settings
    }
})
