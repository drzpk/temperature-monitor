<template>
    <div class="temperature-chart-widget">
        <!--suppress HtmlUnknownAttribute -->
        <svg class="temperature-chart-svg" preserveAspectRatio="xMidYMid meet"></svg>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import * as d3 from "d3";

    interface test {
        temperature: number;
    }

    @Component
    export default class TemperatureChartWidget extends Vue {

        private svgElement!: Element;
        private data!: Array<test>;

        mounted(): void {
            this.initializeWidget();
        }

        private initializeWidget() {
            // https://www.d3-graph-gallery.com/graph/line_basic.html
            // http://bl.ocks.org/benjchristensen/2579599

            this.data = [
                {temperature: 15},
                {temperature: 18},
                {temperature: 20},
                {temperature: 17},
                {temperature: 16},
                {temperature: 16},
                {temperature: 17}
            ];

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

            const minTemperature = d3.min<test, number>(this.data, d => d.temperature);
            const maxTemperature = d3.max<test, number>(this.data, d => d.temperature);

            const x = d3.scaleLinear([0, this.data.length - 1], [0, viewboxSize[0]]);
            const y = d3.scaleLinear([minTemperature as number, maxTemperature as number], [viewboxSize[1], 0]);

            const d = d3.line<test>()
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
        background-color: cornflowerblue;

        .line {
            fill: none;
            stroke: #3fff7b;
            stroke-width: 3;
        }
    }
</style>