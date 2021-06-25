import {Module} from "vuex";
import {AggregationInterval} from "@/models/measurement.model";

export interface ChartRange {
    startDate: Date;
    endDate: Date;
}

export interface ChartsState {
    chartRange: ChartRange | null;
    chartAggregation: AggregationInterval | null;
}

const module: Module<ChartsState, unknown> = {
    namespaced: true,
    state: {
        chartRange: null,
        chartAggregation: null
    },
    getters: {
        chartRange(state): ChartRange | null {
            return state.chartRange;
        },
        chartAggregation(state): AggregationInterval | null {
            return state.chartAggregation;
        }
    },
    mutations: {
        updateChartRange(state: ChartsState, newRange: ChartRange | null) {
            state.chartRange = newRange;
        },
        updateChartAggregation(state: ChartsState, newAggregation: AggregationInterval | null) {
            state.chartAggregation = newAggregation;
        }
    },
    actions: {

    },
    modules: {}
};

export default module;