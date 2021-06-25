import {Module} from "vuex";

export interface ChartRange {
    startDate: Date;
    endDate: Date;
}

export interface ChartsState {
    chartRange: ChartRange | null;
}

const module: Module<ChartsState, unknown> = {
    namespaced: true,
    state: {
        chartRange: null
    },
    getters: {
        chartRange(state): ChartRange | null {
            return state.chartRange;
        }
    },
    mutations: {
        updateChartRange(state: ChartsState, newRange: ChartRange) {
            state.chartRange = newRange;
        }
    },
    actions: {

    },
    modules: {}
};

export default module;