<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <h2 v-if="newDeviceMode">Add a device</h2>
                    <h2 v-else>Edit the device</h2>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <b-form @submit.stop.prevent>
                        <b-form-group v-if="!newDeviceMode" label-for="device-id" label="Id" label-cols="3">
                            <b-form-input id="device-name" name="id" :value="model.id" readonly/>
                        </b-form-group>

                        <b-form-group v-if="!newDeviceMode" label-for="device-create-date" label="Create time" label-cols="3">
                            <b-form-input id="device-name" name="createdAt" type="datetime"
                                          :value="formatDate(model.createdAt)" readonly/>
                        </b-form-group>

                        <b-form-group label-for="device-name" label="Name" label-cols="3">
                            <b-form-input id="device-name" name="name" v-model="model.name"
                                          :disabled="!editMode"
                                          :state="validateState('model.name')"
                                          @input="resetState('model.name')"/>

                            <b-form-invalid-feedback v-if="!$v.model.name.required">
                                Device name is required.
                            </b-form-invalid-feedback>
                            <b-form-invalid-feedback v-if="!$v.model.name.maxLength">
                                Maximum field length is {{$v.model.name.$params.maxLength.max}}.
                            </b-form-invalid-feedback>
                        </b-form-group>

                        <b-form-group label-for="device-description" label="Description" label-cols="3">
                            <b-form-input id="device-description" name="description" v-model="model.description"
                                          :disabled="!editMode"
                                          :state="validateState('model.description')"
                                          @input="resetState('model.description')"/>

                            <b-form-invalid-feedback v-if="!$v.model.description.required">
                                Device description is required.
                            </b-form-invalid-feedback>
                            <b-form-invalid-feedback v-if="!$v.model.description.maxLength">
                                Maximum field length is {{$v.model.description.$params.maxLength.max}}.
                            </b-form-invalid-feedback>
                        </b-form-group>

                        <b-form-group label-for="device-mac" label="Mac address" label-cols="3">
                            <b-form-input id="device-mac" name="mac" v-model="model.mac"
                                          :disabled="!editMode"
                                          :state="validateState('model.mac')"
                                          @input="resetState('model.mac')"/>

                            <b-form-invalid-feedback v-if="!$v.model.mac.required">
                                Device description is required.
                            </b-form-invalid-feedback>
                            <b-form-invalid-feedback v-if="!$v.model.mac.maxLength">
                                Maximum field length is {{$v.model.mac.$params.maxLength.max}}.
                            </b-form-invalid-feedback>
                        </b-form-group>

                        <div class="button-wrapper">
                            <b-button :disabled="formSent || !editMode" @click="sendForm">
                                <span v-if="newDeviceMode">Create</span>
                                <span v-else>Save</span>
                            </b-button>
                        </div>
                    </b-form>

                    <br>
                    <b-alert variant="success" :show="showLoggerHelp">
                        New device has been created. It can be now connected to a physical device in
                        the data logger. Use value <b>{{deviceId}}</b> as the device id.
                    </b-alert>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {Validation, validationMixin} from "vuelidate";
    import {maxLength, required} from "vuelidate/lib/validators";
    import {DeviceModel} from "@/models/device.model";
    import DateService from "@/services/Date.service";

    @Component({
        mixins: [
            validationMixin
        ],
        validations: {
            model: {
                name: {
                    required,
                    maxLength: maxLength(64)
                },
                description: {
                    required,
                    maxLength: maxLength(256)
                },
                mac: {
                    required,
                    maxLength: maxLength(64)
                }
            }
        }
    })
    export default class EditDevice extends Vue {
        model: DeviceModel = {
            id: -1,
            name: "",
            description: "",
            mac: "",
            createdAt: -1
        };
        formSent = false;

        get editMode(): boolean {
            return true;
        }

        get newDeviceMode(): boolean {
            return this.deviceId === null;
        }

        get deviceId(): number | null {
            const idString = this.$route.params["id"];
            return idString ? parseInt(idString) : null;
        }

        get showLoggerHelp(): boolean {
            return !this.newDeviceMode && this.$route.query["show-logger-help"] != null;
        }

        mounted(): void {
            this.onComponentLoaded();
        }

        @Watch("$route.params")
        paramsChanged() {
            this.onComponentLoaded();
        }

        sendForm() {
            this.formSent = true;
            this.$v.$touch();

            if (this.$v.$invalid)
                return;

            if (this.newDeviceMode)
                this.createDevice();
            else
                this.updateDevice();
        }

        validateState(name: string) {
            const element = this.getVuelidateElement(name);
            return element?.$dirty ? !element?.$error : null;
        }

        resetState(name: string) {
            const element = this.getVuelidateElement(name);
            element?.$reset();
            this.formSent = false;
        }

        formatDate(value: number): string {
            return DateService.formatDate(value);
        }

        private onComponentLoaded(): void {
            if (!this.newDeviceMode) {
                this.$store.dispatch("devices/setCurrentDevice", this.deviceId).then((device: DeviceModel) => {
                    this.model.id = device.id;
                    this.model.name = device.name;
                    this.model.description = device.description;
                    this.model.mac = device.mac;
                    this.model.createdAt = device.createdAt;
                });
            } else {
                this.$store.dispatch("devices/setCurrentDevice", null);
            }
        }

        private getVuelidateElement(name: string): Validation | null {
            const parts = name.split(".");
            let current: any = this.$v;
            for (let part of parts) {
                current = current?.[part];
            }

            return current;
        }

        private createDevice() {
            this.$store.dispatch("devices/addDevice", this.model).then((device) => {
                this.$router.push({path: `/devices/${device.id}/edit`, query: {"show-logger-help": ""}});
            });
        }

        private updateDevice() {
            this.$store.dispatch("devices/updateDevice", this.model);
        }
    }
</script>

<style lang="scss" scoped>

    .button-wrapper {
        text-align: right;
    }

</style>