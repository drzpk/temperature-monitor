<template>
    <div>
        <h4>Range selection</h4>
        <b-form @submit.prevent.stop>
            <b-form-group label-for="chart-mode" label="Range type" label-cols="3">
                <b-form-select id="chart-mode" v-model="chartMode">
                    <b-form-select-option value="relative">Relative</b-form-select-option>
                    <b-form-select-option value="absolute">Absolute</b-form-select-option>
                </b-form-select>
            </b-form-group>
            <hr/>

            <!-- Relative -->
            <b-form-group label-for="chart-range" label="Time range" label-cols="3" v-if="chartMode === 'relative'">
                <b-form-select id="chart-range" v-model="timeRange">
                    <b-form-select-option value="5m">Last 5 minutes</b-form-select-option>
                    <b-form-select-option value="15m">Last 15 minutes</b-form-select-option>
                    <b-form-select-option value="30m">Last 30 minutes</b-form-select-option>
                    <b-form-select-option value="1h">Last 1 hour</b-form-select-option>
                    <b-form-select-option value="3h">Last 3 hours</b-form-select-option>
                    <b-form-select-option value="6h">Last 6 hours</b-form-select-option>
                    <b-form-select-option value="12h">Last 12 hours</b-form-select-option>
                    <b-form-select-option value="24h">Last 24 hours</b-form-select-option>
                    <b-form-select-option value="2d">Last 2 days</b-form-select-option>
                    <b-form-select-option value="7d">Last 7 days</b-form-select-option>
                    <b-form-select-option value="30d">Last 30 days</b-form-select-option>
                    <b-form-select-option value="90d">Last 90 days</b-form-select-option>
                </b-form-select>
            </b-form-group>
            <b-form-group label-for="chart-refresh-rate" label="Refresh interval" label-cols="3"
                          v-if="chartMode === 'relative'">
                <b-form-select id="chart-refresh-rate" v-model="refreshInterval">
                    <b-form-select-option value="15">15 seconds</b-form-select-option>
                    <b-form-select-option value="30">30 seconds</b-form-select-option>
                    <b-form-select-option value="60">1 minute</b-form-select-option>
                    <b-form-select-option value="300">5 minutes</b-form-select-option>
                    <b-form-select-option value="-1">disabled</b-form-select-option>
                </b-form-select>
            </b-form-group>
            <!-- end Relative -->

            <!-- Absolute -->
            <b-form-group label-for="chart-time-from" label="Start time" label-cols="3" v-if="chartMode === 'absolute'">
                <b-form-input type="datetime-local" v-model="timeFrom"/>
            </b-form-group>
            <b-form-group label-for="chart-time-to" label="End time" label-cols="3" v-if="chartMode === 'absolute'">
                <b-form-input type="datetime-local" v-model="timeTo"/>
            </b-form-group>
            <!-- end Absolute -->
        </b-form>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";

    @Component
    export default class ChartControls extends Vue {
        chartMode = "relative";
        timeRange = "1h";
        refreshInterval = 60;
        timeFrom = "";
        timeTo = "";

        private timerHandle: number | null = null;

        mounted(): void {
            this.updateTimer();
        }

        destroyed(): void {
            if (this.timerHandle != null)
                clearInterval(this.timerHandle);
        }

        @Watch("chartMode")
        private onChartModeChanged() {
            this.updateTimer();
        }

        @Watch("timeRange")
        private onTimeRangeChanged() {
            this.updateTimer();
            this.onTimeFromToChanged();
        }

        @Watch("timeFrom")
        private onTimeFromChanged() {
           this.onTimeFromToChanged();
        }

        @Watch("timeTo")
        private onTimeToChanged() {
           this.onTimeFromToChanged();
        }

        @Watch("refreshInterval")
        private onRefreshIntervalChanged() {
            this.updateTimer();
        }

        private updateTimer() {
            if (this.timerHandle != null)
                clearInterval(this.timerHandle);

            if (this.chartMode == "relative" && this.refreshInterval > 0) {
                const executor = () => {
                    const range = this.getAbsoluteTimeRange();
                    this.notifyTimeRangeChanged(range[0], range[1]);
                };

                this.timerHandle = setInterval(executor, this.refreshInterval * 1000);
                executor();
            }
        }

        private getAbsoluteTimeRange(): [Date, Date] {
            const factors: { [symbol: string]: number } = {
                m: 1000 * 60,
                h: 1000 * 60 * 60,
                d: 1000 * 60 * 60 * 24
            };

            const baseValue = parseInt(this.timeRange.substring(0, this.timeRange.length - 1));
            const factor = factors[this.timeRange.substring(this.timeRange.length - 1, this.timeRange.length)];
            const diff = baseValue * factor;

            const dateTo = new Date();
            return [new Date(dateTo.getTime() - diff), dateTo];
        }

        private onTimeFromToChanged() {
            if (this.chartMode != "absolute" || this.timeFrom.length == 0 || this.timeTo.length == 0)
                return;

            this.notifyTimeRangeChanged(new Date(this.timeFrom), new Date(this.timeTo));
        }

        private notifyTimeRangeChanged(from: Date, to: Date) {
            const range = {
                startDate: from,
                endDate: to
            };
            this.$store.commit("charts/updateChartRange", range);
        }
    }
</script>

<style lang="scss">

</style>