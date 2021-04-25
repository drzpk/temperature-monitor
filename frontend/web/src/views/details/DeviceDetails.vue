<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="10" lg="4" offset-lg="3">
                    <h2 v-if="activeDevice">{{activeDevice.name}}</h2>
                </b-col>
                <b-col cols="2" lg="2">
                    <b-button-group style="float: right">
                        <b-button variant="primary" @click="editDevice">Edit</b-button>
                        <b-button variant="danger" v-b-modal.device-delete-modal>Delete</b-button>
                    </b-button-group>
                    <b-modal id="device-delete-modal" title="Delete confirmation" ok-title="Yes" cancel-title="No"
                             @ok="deleteDevice">
                        <p>Are you sure you want to delete this device?</p>
                    </b-modal>
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
    import {Device} from "@/models/device";

    @Component({
        components: {ChartControls, Chart},
        computed: mapState("charts", [
            "activeDevice"
        ])
    })
    export default class DeviceDetails extends Vue {
        activeDevice!: Device;

        editDevice(): void {
            this.$router.push(`/devices/${this.activeDevice.id}/edit`);
        }

        deleteDevice(): void {
            this.$store.dispatch("deleteDevice", this.activeDevice.id).then(() => {
                this.$router.push("/devices");
            }).catch(error => {
                console.error(error);
                this.$bvToast.toast("An error occurred while deleting this device", {
                    title: "Error",
                    variant: "danger",
                    appendToast: true
                });
            });
        }
    }
</script>

<style lang="scss" scoped>
    h3 {
        font-size: 1.1em;
        font-style: italic;
    }
</style>