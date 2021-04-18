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
                        <b-button @click="newDevice">New device</b-button>
                    </div>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" sm="10" offset-sm="1" md="8" offset-md="2" xl="10" offset-xl="1">
                    <div id="panels">
                        <div class="panel-wrapper" v-for="x in 10" :key="x">
                            <DeviceSummaryPanel/>
                        </div>
                    </div>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script>
    import {Component, Vue} from "vue-property-decorator";
    import DeviceSummaryPanel from "@/views/summary/DeviceSummaryPanel";

    @Component({
        components: {
            DeviceSummaryPanel
        }
    })
    export default class Devices extends Vue {
        newDevice() {
            this.$router.push("/devices/add");
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
            padding: 2em;

            @include media-breakpoint-down(xl) {
             flex-basis: 100%;
            }

            @include media-breakpoint-up(xl) {
                flex-basis: 50%;
            }
        }
    }
</style>