<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="10" lg="4" offset-lg="3">
                    <h2 v-if="activeDevice">{{activeDevice.name}}</h2>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <h3 v-if="activeDevice">{{activeDevice.description}}</h3>
                </b-col>
            </b-row>
            <br>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    Temperature chart
                    <Chart/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    Humidity chart
                    <Chart/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
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
        computed: mapState("charts", [
            "activeDevice"
        ])
    })
    export default class DeviceDetails extends Vue {
        activeDevice!: DeviceModel;
    }
</script>

<style lang="scss" scoped>
    h3 {
        font-size: 1.1em;
        font-style: italic;
    }
</style>