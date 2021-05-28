<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="6" sm="5" offset-sm="1" md="4" offset-md="2" xl="8" offset-xl="2">
                    <b-row>
                        <b-col>
                            <h2>Settings</h2>
                            <br>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-card title="Devices">
                                <SettingsDevices/>
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

    @Component({
        components: {SettingsDevices}
    })
    export default class Settings extends Vue {

    }
</script>

<style lang="scss">

</style>