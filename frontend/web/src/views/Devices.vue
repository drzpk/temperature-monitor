<template>
    <div id="overview">
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="6" sm="5" offset-sm="1" md="4" offset-md="2" xl="5" offset-xl="1">
                    <h2>Device list</h2>
                </b-col>
                <b-col cols="6" sm="5" md="4" xl="5">
                    <div style="width: 100%; text-align: right">
                        <b-button @click="settings">Settings</b-button>
                    </div>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" sm="10" offset-sm="1" md="8" offset-md="2" xl="10" offset-xl="1">
                    <div id="panels">
                        <div class="panel-wrapper" v-for="x in 10" :key="x" @click="goToDevice(x)">
                            <DeviceSummaryPanel/>
                        </div>
                    </div>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import DeviceSummaryPanel from "@/views/summary/DeviceSummaryPanel.vue";
    import {DeviceModel} from "@/models/device.model";

    @Component({
        components: {
            DeviceSummaryPanel
        }
    })
    export default class Devices extends Vue {

        mounted(): void {
            this.$store.commit("charts/setActiveDevice", null);
        }

        settings() {
            this.$router.push("/settings");
        }

        goToDevice(id: number) {
            this.$store.dispatch("getDeviceById", id).then((device: DeviceModel) => {
                this.$store.commit("charts/setActiveDevice", device);
                this.$router.push(`/devices/${id}/details`);
            });
        }
    }
</script>

<style lang="scss">
    @import "~bootstrap/scss/bootstrap-grid";

    #overview {
        padding-top: 0.8em;
    }

    #panels {
        display: flex;
        flex-flow: row wrap;

        .panel-wrapper {
            box-sizing: border-box;
            margin-top: 1em;
            margin-bottom: 1em;

            cursor: pointer;
            transition: transform 0.1s ease-in-out;

            @include media-breakpoint-down(xl) {
                margin-left: 1em;
                margin-right: 1em;
                flex-basis: 100%;
            }

            @include media-breakpoint-up(xl) {
                margin-left: 2em;
                margin-right: 2em;
                flex-basis: calc(50% - 2 * 2em);

                &:nth-child(1), &:nth-child(2) {
                    margin-top: 1em;
                }
            }

            &:hover {
                transform: scale(1.02);
            }
        }
    }
</style>