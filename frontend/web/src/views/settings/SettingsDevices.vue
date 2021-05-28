<!--suppress HtmlUnknownAttribute -->
<template>
    <div>
        <b-table :items="devices" :fields="fields">
            <template #cell(Actions)="data">
                <b-button-group>
                    <b-button variant="primary" @click="showData(data.item)">Show data</b-button>
                    <b-button variant="warning" @click="editDevice(data.item)">Edit</b-button>
                    <b-button variant="danger" @click="deleteDevice(data.item)">Delete</b-button>
                </b-button-group>
            </template>
        </b-table>

        <br>
        <div style="float: right">
            <b-button variant="primary" @click="addDevice">Add a device</b-button>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {mapGetters} from "vuex";
    import {DeviceModel} from "@/models/device.model";
    import DateService from "@/services/Date.service";

    @Component({
        computed: {
            ...mapGetters("settings", ["devices"])
        }
    })
    export default class SettingsDevices extends Vue {
        devices!: Array<DeviceModel>;

        fields = [
            {
                key: "id"
            },
            {
                key: "name"
            },
            {
                key: "description"
            },
            {
                key: "createdAt",
                label: "Creation date",
                formatter: DateService.formatDate
            },
            {
                key: "lastMeasurement.time",
                label: "Last measurement date",
                formatter: (value: number) => value ? DateService.formatDate(value) : "-"
            },
            {
                key: "Actions"
            }
        ];

        mounted(): void {
            this.$store.dispatch("settings/refreshDevices");
        }

        addDevice(): void {
            this.$router.push("/devices/add");
        }

        showData(device: DeviceModel): void {
            this.$router.push(`/devices/${device.id}/details`);
        }

        editDevice(device: DeviceModel): void {
            this.$router.push(`/devices/${device.id}/edit`);
        }

        deleteDevice(device: DeviceModel): void {
            this.$bvModal.msgBoxConfirm("Are your sure you want to delete this device?", {
                okTitle: "Yes",
                cancelTitle: "No",
            }).then(value => {
                if (value)
                    this.doDeleteDevice(device);
            });
        }

        private doDeleteDevice(device: DeviceModel): void {
            this.$store.dispatch("devices/deleteDevice", device).catch(error => {
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

<style lang="scss">

</style>