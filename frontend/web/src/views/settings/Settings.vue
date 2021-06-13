<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="6" sm="5" offset-sm="1" md="4" offset-md="2" xl="8" offset-xl="2">
                    <b-row>
                        <b-col>
                            <h2>Settings</h2>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-card title="Devices">
                                <SettingsDevices/>
                            </b-card>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-card title="Loggers">
                                <SettingsLoggers/>
                            </b-card>
                        </b-col>
                    </b-row>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import SettingsDevices from "@/views/settings/SettingsDevices.vue";
    import SettingsLoggers from "@/views/settings/SettingsLoggers.vue";

    @Component({
        components: {SettingsLoggers, SettingsDevices}
    })
    export default class Settings extends Vue {

    }
</script>

<style lang="scss" scoped>
    .card {
        margin: 1em;
    }
</style>