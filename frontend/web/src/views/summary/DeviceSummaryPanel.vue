<template>
    <div class="panel">
        <div class="panel-left">
            <div class="panel-left-text">
                <div class="device-name">{{device.name}}</div>
                <div v-if="device.lastMeasurement" class="temperature">
                    <span>{{device.lastMeasurement.temperature}}</span>&nbsp;&#176;C
                </div>
                <div v-else>no measurements available</div>
            </div>
            <div class="temperature-histogram">
                <TemperatureChartWidget :device-id="device.id"/>
            </div>
        </div>
        <div class="panel-right">
            <HumidityWidget :humidity="device.lastMeasurement != null && device.lastMeasurement.humidity || null"/>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator";
    import HumidityWidget from "@/views/summary/HumidityWidget.vue";
    import TemperatureChartWidget from "@/views/summary/TemperatureChartWidget.vue";
    import {DeviceModel} from "@/models/device.model";

    @Component({
        components: {HumidityWidget, TemperatureChartWidget}
    })
    export default class DeviceSummaryPanel extends Vue {
        @Prop({required: true})
        readonly device!: DeviceModel;
    }
</script>

<style lang="scss">
    $border-definition: 1px solid #8e8e8e;

    .panel {
        width: 100%;
        height: 10em;
        border: $border-definition;
        background-image: linear-gradient(169deg, #618fd4c9, #54D0AC);
    }

    .panel-left {
        position: relative;
        display: inline-block;
        overflow: hidden;
        width: 72%;
        height: 100%;

        .panel-left-text {
            position: absolute;
            z-index: 2;
            margin-top: 0.7em;
            margin-left: 1.1em;
            padding: 0.5em 2.5em 0.5em 0.5em;
            background-color: #ffffffc4;
            box-shadow: 0 0 11px 5px #ffffffc4;
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
        width: 28%;
        height: 100%;
    }
</style>
