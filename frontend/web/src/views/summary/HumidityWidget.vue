<template>
    <div class="humidity-widget">
        <!--suppress HtmlUnknownAttribute -->
        <svg viewBox="0 0 300 300" preserveAspectRatio="xMidYMid meet"></svg>
        <div class="humidity-text">
            <div>
                <font-awesome-icon icon="tint"/>
            </div>
            <div>{{ humidity }}%</div>
        </div>
    </div>
</template>

<script lang="ts">
    import * as d3 from "d3";
    import {Arc, DefaultArcObject, Selection} from "d3";
    import {Component, Prop, Vue} from "vue-property-decorator";

    const VIEWBOX_WIDTH = 300;
    const RADIUS = 125;
    const THICKNESS = 20;
    const ANIMATION_DURATION = 200;

    @Component
    export default class HumidityWidget extends Vue {

        @Prop(Number)
        readonly deviceId!: number;

        private svg!: Element;
        private foregroundArc!: Arc<any, DefaultArcObject>;
        private foregroundPath!: Selection<any, unknown, any, unknown>;
        private humidity = 0;

        mounted(): void {
            this.svg = this.$el.getElementsByTagName("svg")[0];
            this.initializeWidget();

            setInterval(() => {
                const old = this.humidity;
                this.humidity = Math.floor(100 * Math.random());
                this.updateWidget(old);
            }, 1000);
        }

        private initializeWidget() {
            const backgroundArc = d3.arc()
                .startAngle(0)
                .endAngle(2 * Math.PI)
                .innerRadius(RADIUS - THICKNESS)
                .outerRadius(RADIUS);

            this.foregroundArc = d3.arc()
                .startAngle(0)
                .endAngle(0)
                .innerRadius(RADIUS - THICKNESS)
                .outerRadius(RADIUS);

            const element = d3.select(this.svg);
            element.selectChildren().remove();

            element.append("path")
                .attr("class", "bar-bg")
                .attr("transform", `translate(${VIEWBOX_WIDTH / 2}, ${VIEWBOX_WIDTH / 2})`)
                // @ts-expect-error: no argument required
                .attr("d", backgroundArc());

            this.foregroundPath = element.append("path")
                .attr("class", "bar-fg")
                .attr("transform", `translate(${VIEWBOX_WIDTH / 2}, ${VIEWBOX_WIDTH / 2})`)
                // @ts-expect-error: no argument required
                .attr("d", this.foregroundArc());
        }

        private updateWidget(oldHumidity: number) {
            this.foregroundPath.transition()
                .duration(ANIMATION_DURATION)
                .attrTween("d", () => {
                    const distance = this.humidity - oldHumidity;
                    return (t: number) => {
                        const fillPercent = (oldHumidity + distance * t) / 100;
                        this.foregroundArc.endAngle(2 * Math.PI * fillPercent);
                        // @ts-expect-error: no argument required
                        return this.foregroundArc();
                    };
                })
        }
    }
</script>

<style lang="scss">
    .humidity-widget {
        position: relative;
        width: 100%;
        height: 100%;
        overflow: auto;

        > svg {
            width: 100%;
            height: 100%;
            background-color: #ffe0a4;
        }

        .bar-bg {
            fill: #c3c3c3;
        }

        .bar-fg {
            fill: black;
        }

        .label {
            font-size: 2em;
            text-anchor: middle;
            dominant-baseline: middle;
        }

        .humidity-text {
            position: absolute;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            z-index: 1;

            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;

            font-size: 1.6em;
        }
    }
</style>