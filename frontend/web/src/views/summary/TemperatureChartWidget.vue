import {AggregationInterval} from "@/models/measurement.model";
<template>
    <div class="temperature-chart-widget">
        <!--suppress HtmlUnknownAttribute -->
        <svg class="temperature-chart-svg" preserveAspectRatio="xMidYMid meet"></svg>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from "vue-property-decorator";
    import * as d3 from "d3";
    import {AggregationInterval, GetMeasurementsRequest, MeasurementModel} from "@/models/measurement.model";
    import ApiService from "@/services/Api.service";

    @Component
    export default class TemperatureChartWidget extends Vue {
        @Prop({required: true})
        readonly deviceId!: number;

        private svgElement!: Element;
        private intervalHandle: number | null = null;
        private data: MeasurementModel[] = [];

        mounted(): void {
            this.initializeWidget();
            this.downloadData();
            this.intervalHandle = setInterval(this.downloadData, 15_000);
        }

        beforeDestroy(): void {
            if (this.intervalHandle)
                clearInterval(this.intervalHandle);
        }

        @Watch("deviceId")
        onDeviceChanged(): void {
            this.downloadData();
        }

        private downloadData(): void {
            const request: GetMeasurementsRequest = {
                deviceId: this.deviceId,
                size: 1000,
                aggregationInterval: AggregationInterval.FIVE_MINUTES
            };
            ApiService.getMeasurements(request).then(data => {
                this.data = data;
                this.updateSvg();
            })
        }

        private initializeWidget() {
            // https://www.d3-graph-gallery.com/graph/line_basic.html
            // http://bl.ocks.org/benjchristensen/2579599
            this.svgElement = this.$el.getElementsByClassName("temperature-chart-svg")[0];
            this.updateSvg();
            d3.select(window).on("resize", () => this.updateSvg());
        }

        private updateSvg() {
            const size = this.getSvgViewboxSize();
            this.svgElement.setAttribute("viewBox", `0 0 ${size[0]} ${size[1]}`);
            this.redrawSvg(size);
        }

        private getSvgViewboxSize(): [number, number] {
            const rect = this.svgElement.getBoundingClientRect();
            const ratio = rect.width / rect.height;

            const heightPoints = 300;
            const widthPoints = Math.floor(ratio * heightPoints);

            return [widthPoints, heightPoints];
        }

        private redrawSvg(viewboxSize: [number, number]) {
            const svg = d3.select(this.svgElement);

            const minTemperature = d3.min<MeasurementModel, number>(this.data, d => d.temperature);
            const maxTemperature = d3.max<MeasurementModel, number>(this.data, d => d.temperature);

            const x = d3.scaleLinear([0, this.data.length - 1], [0, viewboxSize[0]]);
            const y = d3.scaleLinear([minTemperature as number, maxTemperature as number], [viewboxSize[1], 10]);

            const d = d3.line<MeasurementModel>()
                .curve(d3.curveMonotoneX)
                .x((d, i) => x(i))
                .y(d => y(d.temperature));

            svg.selectChildren().remove();
            svg.append("path")
                .datum(this.data)
                .attr("class", "line")
                .attr("d", d);
        }
    }
</script>

<style lang="scss">
    .temperature-chart-widget {
        width: 100%;
        height: 100%;
    }

    svg.temperature-chart-svg {
        width: 100%;
        height: 100%;

        .line {
            fill: none;
            stroke: #4a4a4a;
            stroke-width: 8;
        }
    }
</style>