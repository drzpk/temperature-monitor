<template>
    <div id="overview">
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="12" sm="10" offset-sm="1" md="8" offset-md="2" xl="10" offset-xl="1">
                    <h2>Device list</h2>
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