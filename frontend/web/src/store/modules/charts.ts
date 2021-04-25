import {Module} from "vuex";
import {Device} from "@/models/device";

export interface ChartRange {
    startDate: Date;
    endDate: Date;
}

export interface ChartsState {
    activeDevice: Device | null;
    chartRange: ChartRange | null;
}

const module: Module<ChartsState, unknown> = {
    namespaced: true,
    state: {
        activeDevice: null,
        chartRange: null
    },
    getters: {},
    mutations: {
        setActiveDevice(state: ChartsState, device: Device | null) {
            state.activeDevice = device;
        },
        updateChartRange(state: ChartsState, newRange: ChartRange) {
            state.chartRange = newRange;
        }
    },
    actions: {

    },
    modules: {}
};

export default module;