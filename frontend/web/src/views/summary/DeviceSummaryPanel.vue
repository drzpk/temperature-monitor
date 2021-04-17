<template>
    <div class="panel">
        <div class="panel-left">
            <div class="panel-left-text">
                <div class="device-name">example device</div>
                <div class="temperature"><span>21.3</span>&nbsp;&#176;C</div>
            </div>
            <div class="temperature-histogram">
                <TemperatureChartWidget/>
            </div>
        </div>
        <div class="panel-right">
            <HumidityWidget/>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import HumidityWidget from "@/views/summary/HumidityWidget.vue";
    import TemperatureChartWidget from "@/views/summary/TemperatureChartWidget.vue";

    @Component({
        components: {HumidityWidget, TemperatureChartWidget}
    })
    export default class DeviceSummaryPanel extends Vue {
    }
</script>

<style lang="scss">
    $border-definition: 1px solid #8e8e8e;

    .panel {
        width: 100%;
        height: 10em;
        border: $border-definition;
        background-color: #f1f1f1;
    }

    .panel-left {
        position: relative;
        display: inline-block;
        overflow: hidden;
        width: 62%;
        height: 100%;

        .panel-left-text {
            position: absolute;
            z-index: 2;
        }

        .device-name {
            font-size: 1.4em;
        }

        .temperature {
            font-size: 1.8em;
        }

        .temperature-histogram {
            position: absolute;
            top: 38%;
            width: 100%;
            height: 62%;
            z-index: 0;
        }
    }

    .panel-right {
        display: inline-block;
        border-left: $border-definition;
        width: 38%;
        height: 100%;
    }
</style>
