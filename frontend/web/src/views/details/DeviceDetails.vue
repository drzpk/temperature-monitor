<template>
    <div id="device-details">
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
                    <Chart :measurements="temperatureMeasurements" color="#277554" unit=" °C"/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <div style="margin-top: 2em;"></div>
                    <h5>Humidity chart</h5>
                    <Chart :measurements="humidityMeasurements" color="#AA5939" unit="%"/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <div style="margin-top: 2em;"></div>
                    <ChartControls/>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <div style="margin-top: 2em;"></div>
                    <h4>Export data</h4>
                    <div class="export-buttons">
                        <b-button @click="exportMeasurements('csv')">Export to CSV</b-button>
                        <b-button @click="exportMeasurements('json')">Export to JSON</b-button>
                        <b-button @click="exportMeasurements('xml')">Export to XML</b-button>
                    </div>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import Chart from "@/views/details/Chart.vue";
    import ChartControls from "@/views/details/ChartControls.vue";
    import {mapState} from "vuex";
    import {DeviceModel} from "@/models/device.model";
    import {AggregationInterval, GetMeasurementsRequest, MeasurementModel} from "@/models/measurement.model";
    import {ChartRange} from "@/store/modules/charts";
    import ApiService from "@/services/Api.service";
    import {Measurement} from "@/models/data";
    import MeasurementExporterService from "@/services/MeasurementExporter.service";

    @Component({
        components: {ChartControls, Chart},
        computed: {
            ...mapState("devices", [
                "currentDevice"
            ]),
            ...mapState("charts", [
                "chartRange",
                "chartAggregation"
            ])
        }
    })
    export default class DeviceDetails extends Vue {
        currentDevice!: DeviceModel;
        chartRange!: ChartRange | null;
        chartAggregation!: AggregationInterval | null;

        temperatureMeasurements: Measurement[] = [];
        humidityMeasurements: Measurement[] = [];

        private measurements: MeasurementModel[] = [];

        mounted(): void {
            const deviceId = parseInt(this.$route.params.id as string);
            this.$store.dispatch("devices/setCurrentDevice", deviceId);
            this.downloadMeasurements();
        }

        beforeDestroy(): void {
            this.$store.dispatch("devices/setCurrentDevice", null);
        }

        exportMeasurements(to: string): void {
            switch (to) {
                case "csv":
                    MeasurementExporterService.exportToCSV(this.measurements);
                    break;
                case "xml":
                    MeasurementExporterService.exportToXML(this.measurements);
                    break;
                case "json":
                    MeasurementExporterService.exportToJSON(this.measurements);
                    break;
            }
        }

        @Watch("currentDevice")
        private onDeviceChanged(): void {
            this.downloadMeasurements();
        }

        @Watch("chartRange")
        private onChartRangeChanged(): void {
            this.downloadMeasurements();
        }

        @Watch("chartAggregation")
        private onChartAggregationChanged(): void {
            this.downloadMeasurements();
        }

        private downloadMeasurements(): void {
            if (!this.currentDevice)
                return;

            const request: GetMeasurementsRequest = {
                deviceId: this.currentDevice.id,
                size: 10_000,
                from: this.chartRange != null ? Math.floor(this.chartRange.startDate.getTime() / 1000) : undefined,
                to: this.chartRange != null ? Math.floor(this.chartRange.endDate.getTime() / 1000) : undefined,
                aggregationInterval: this.chartAggregation || undefined
            };

            ApiService.getMeasurements(request).then(measurements => {
                this.measurements = measurements;
                this.processMeasurements();
            })
        }

        private processMeasurements(): void {
            const temp: Measurement[] = [];
            const hum: Measurement[] = [];

            for (let i = 0; i < this.measurements.length; i++) {
                const item = this.measurements[i];
                const time = new Date(item.time * 1000);

                temp.push(new Measurement(time, item.temperature));
                hum.push(new Measurement(time, item.humidity));
            }

            this.temperatureMeasurements = temp;
            this.humidityMeasurements = hum;
        }
    }
</script>

<style lang="scss" scoped>
    h3 {
        font-size: 1.1em;
        font-style: italic;
    }

    #device-details {
        margin-bottom: 2em;
    }

    .export-buttons button {
        margin: 0 0.5em;

        &:first-child {
            margin-left: 0;
        }
    }
</style>