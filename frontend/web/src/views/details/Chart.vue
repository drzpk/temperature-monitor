<template>
    <div class="device-chart">
        <!--suppress HtmlUnknownAttribute -->
        <svg></svg>
        <div class="legend-popup">
            {{legendText}}
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import * as d3 from "d3";
    import {Axis, ScaleLinear, ScaleTime, Selection} from "d3";
    import {Measurement, MeasurementData} from "@/models/data";

    const CHART_VIEWBOX = [600, 350];
    const CHART_MARGINS = [10, 30, 30, 50];
    const TRANSITION_DURATION = 1000;

    @Component
    export default class Chart extends Vue {
        @Prop({default: "black"})
        color!: string;

        @Prop({default: ""})
        unit!: string;

        legendText = "placeholder";

        private data!: MeasurementData;

        private svgElement!: SVGSVGElement;
        private svg!: Selection<SVGSVGElement, unknown, null, undefined>;
        private path!: Selection<any, unknown, null, undefined>;
        private scaleX!: ScaleTime<number, number, never>;
        private scaleY!: ScaleLinear<number, number, never>;
        private axisX!: Axis<any>;
        private axisY!: Axis<any>;

        private legendPopup!: Selection<any, unknown, null, undefined>;
        private legendPopupHeight!: number;
        private legendPoint!: SVGPoint;

        mounted(): void {
            const timedData: Array<Measurement> = [];
            const startTime = new Date().getTime();
            for (let i = 0; i < 100; i++) {
                timedData.push({
                    time: new Date(startTime + i * 10000),
                    value: 15 + 10 * Math.random()
                });
            }

            this.data = {
                minY: 0,
                maxY: 30,
                measurements: timedData
            };

            this.initializeChart();
            this.updateChart();
        }

        private initializeChart() {
            this.svgElement = this.$el.getElementsByTagName("svg")[0];
            this.svg = d3.select(this.svgElement);
            this.svg.selectChildren().remove();
            this.svg.attr("viewBox", `0 0 ${CHART_VIEWBOX[0]} ${CHART_VIEWBOX[1]}`);

            this.scaleX = d3.scaleTime([CHART_MARGINS[3], CHART_VIEWBOX[0] - CHART_MARGINS[1]]);
            this.scaleY = d3.scaleLinear([CHART_VIEWBOX[1] - CHART_MARGINS[2], CHART_MARGINS[0]]);

            this.axisX = d3.axisBottom(this.scaleX);
            this.axisY = d3.axisLeft(this.scaleY);

            this.path = this.svg.append("g")
                .append("path")
                .attr("class", "chart-path")
                .style("stroke", this.color);

            this.svg.append("g")
                .attr("transform", `translate(0, ${CHART_VIEWBOX[1] - CHART_MARGINS[2]})`)
                .attr("class", "axis-x");

            this.svg.append("g")
                .attr("transform", `translate(${CHART_MARGINS[3]}, 0)`)
                .attr("class", "axis-y");

            const legendPopupElement = this.$el.getElementsByClassName("legend-popup")[0];
            this.legendPopup = d3.select(legendPopupElement);
            this.legendPopupHeight = legendPopupElement.getBoundingClientRect().height;
            this.legendPoint = this.svgElement.createSVGPoint();

            this.initializeValueOverlay();
        }

        private initializeValueOverlay() {
            const overlayCircle = this.svg.append("g")
                .append("circle")
                .attr("class", "overlay-circle")
                .attr("r", 6);

            const bisector = d3.bisector<Measurement, Date>((d) => d.time).left;
            const onMouseMove = (e: MouseEvent) => {
                const xPosition = d3.pointer(e)[0] + CHART_MARGINS[3];
                const xPositionDate = this.scaleX.invert(xPosition);
                const measurementIndex = bisector(this.data.measurements, xPositionDate);

                const measurement = this.data.measurements[measurementIndex];
                const x = this.scaleX(measurement.time);
                const y = this.scaleY(measurement.value);

                overlayCircle
                    .attr("cx", x)
                    .attr("cy", y);

                this.legendPoint.x = x;
                this.legendPoint.y = y;
                const transformed = this.legendPoint.matrixTransform(this.svgElement.getScreenCTM() as any);

                const rect = this.svg.node()!.getBoundingClientRect();
                const popupLeft = transformed.x - rect.x + 15;
                const popupTop = transformed.y - rect.y - this.legendPopupHeight / 2;
                this.legendPopup
                    .style("left", popupLeft + "px")
                    .style("top", popupTop + "px");

                this.legendText = `${measurement.value.toFixed(1)}${this.unit}`;
            };

            const onMouseEnter = () => {
              overlayCircle.style("opacity", 1);
              this.legendPopup.style("opacity", 1);
            };

            const onMouseExit = () => {
                overlayCircle.style("opacity", 0);
                this.legendPopup.style("opacity", 0);
            };

            this.svg.append("rect")
                .style("fill", "none")
                .style("pointer-events", "all")
                .attr("width", CHART_VIEWBOX[0] - CHART_MARGINS[1] - CHART_MARGINS[3])
                .attr("height", CHART_VIEWBOX[1] - CHART_MARGINS[0] - CHART_MARGINS[2])
                .attr("transform", `translate(${CHART_MARGINS[3]}, ${CHART_MARGINS[0]})`)
                .on("mouseover", onMouseEnter)
                .on("mousemove", onMouseMove)
                .on("mouseout", onMouseExit);

            onMouseExit();
        }

        private updateChart() {
            this.scaleX.domain(d3.extent(this.data.measurements, (m: Measurement) => m.time) as unknown as [number, number]);
            this.scaleY.domain([this.data.minY, this.data.maxY]);

            this.svg
                .selectAll(".axis-x")
                .transition()
                .duration(TRANSITION_DURATION)
                .call(this.axisX as unknown as any);

            this.svg
                .selectAll(".axis-y")
                .transition()
                .duration(TRANSITION_DURATION)
                .call(this.axisY as unknown as any);

            this.path
                .datum(this.data.measurements)
                .transition()
                .duration(TRANSITION_DURATION)
                .attr("d", d3.line<Measurement>()
                    .x(d => this.scaleX(d.time))
                    .y(d => this.scaleY(d.value))
                );
        }
    }
</script>

<style lang="scss">
    .device-chart {
        position: relative;

        svg {
            background-color: #f9f9f9;
        }

        path.chart-path {
            stroke-width: 1.2;
            fill: none;
        }

        .overlay-circle {
            fill: none;
            stroke-width: 1;
            stroke: black;
        }

        .overlay-text {
            font-size: 0.9em;
            background-color: white;
        }

        .legend-popup {
            position: absolute;
            top: 0;
            left: 0;
            padding: 0.5em 1em 0.5em 0.5em;
            font-weight: bold;
            white-space: nowrap;
            color: white;
            background-color: #2f2f2f;
            border-radius: 5px;
            pointer-events: none;
        }
    }
</style>