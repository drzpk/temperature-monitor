<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="10" lg="4" offset-lg="3">
                    <h2 v-if="currentDevice">{{currentDevice.name}}</h2>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <h3 v-if="currentDevice">{{currentDevice.description}}</h3>
                </b-col>
            </b-row>
            <br>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <h5>Temperature chart</h5>
                    <Chart color="#277554" unit=" °C"/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <div style="margin-top: 2em;"></div>
                    <h5>Humidity chart</h5>
                    <Chart color="#AA5939" unit="%"/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <div style="margin-top: 2em;"></div>
                    <ChartControls/>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import Chart from "@/views/details/Chart.vue";
    import ChartControls from "@/views/details/ChartControls.vue";
    import {mapState} from "vuex";
    import {DeviceModel} from "@/models/device.model";

    @Component({
        components: {ChartControls, Chart},
        computed: mapState("devices", [
            "currentDevice"
        ])
    })
    export default class DeviceDetails extends Vue {
        currentDevice!: DeviceModel;

        mounted(): void {
            if (!this.currentDevice) {
                const deviceId = parseInt(this.$route.params.id as string);
                this.$store.dispatch("devices/setCurrentDevice", deviceId);
            }
        }
    }
</script>

<style lang="scss" scoped>
    h3 {
        font-size: 1.1em;
        font-style: italic;
    }
</style>