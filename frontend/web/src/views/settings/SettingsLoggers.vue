<template>
    <div>
        <b-table :items="loggers" :fields="fields">
            <!--suppress HtmlUnknownAttribute -->
            <template #cell(Actions)="data">
                <b-button-group>
                    <b-button variant="warning" @click="editLogger(data.item)">Edit</b-button>
                    <b-button variant="danger" @click="deleteLogger(data.item)">Delete</b-button>
                </b-button-group>
            </template>
        </b-table>

        <br>
        <div style="float: right">
            <b-button variant="primary" @click="addLogger">Add a logger</b-button>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {LoggerModel} from "@/models/logger.model";
    import {mapGetters} from "vuex";
    import DateService from "@/services/Date.service";

    @Component({
        computed: {
            ...mapGetters("settings", ["loggers"])
        }
    })
    export default class SettingsLoggers extends Vue {
        loggers!: LoggerModel[];

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
                key: "Actions"
            }
        ];

        mounted(): void {
            this.$store.dispatch("settings/refreshLoggers");
        }

        addLogger(): void {
            this.$router.push("/loggers/add");
        }

        editLogger(model: LoggerModel): void {
            this.$router.push(`/loggers/${model.id}/edit`);
        }

        deleteLogger(model: LoggerModel): void {
            this.$bvModal.msgBoxConfirm("Are your sure you want to delete this logger?", {
                okTitle: "Yes",
                cancelTitle: "No",
            }).then(value => {
                if (value)
                    this.doDeleteLogger(model);
            });
        }

        private doDeleteLogger(model: LoggerModel): void {
            this.$store.dispatch("settings/deleteLogger", model).catch(error => {
                console.error(error);
                this.$bvToast.toast("An error occurred while deleting this logger.", {
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