<template>
    <div>
        <!--suppress HtmlUnknownBooleanAttribute -->
        <b-container fluid>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <h2 v-if="newLoggerMode">Add a logger</h2>
                    <h2 v-else>Edit the logger</h2>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="12" lg="6" offset-lg="3">
                    <b-form @submit.stop.prevent>
                        <b-form-group v-if="!newLoggerMode" label-for="logger-id" label="Id" label-cols="3">
                            <b-form-input id="logger-name" name="id" :value="model.id" readonly/>
                        </b-form-group>

                        <b-form-group v-if="!newLoggerMode" label-for="logger-create-date" label="Created at"
                                      label-cols="3">
                            <b-form-input id="logger-name" name="createdAt" type="datetime"
                                          :value="formatDate(model.createdAt)" readonly/>
                        </b-form-group>

                        <b-form-group label-for="logger-name" label="Name" label-cols="3">
                            <b-form-input id="logger-name" name="name" v-model="model.name"
                                          :disabled="!editMode"
                                          :state="validateState('model.name')"
                                          @input="resetState('model.name')"/>

                            <b-form-invalid-feedback v-if="!$v.model.name.required">
                                Logger name is required.
                            </b-form-invalid-feedback>
                            <b-form-invalid-feedback v-if="!$v.model.name.maxLength">
                                Maximum field length is {{$v.model.name.$params.maxLength.max}}.
                            </b-form-invalid-feedback>
                        </b-form-group>

                        <b-form-group label-for="logger-description" label="Description" label-cols="3">
                            <b-form-input id="logger-description" name="description" v-model="model.description"
                                          :disabled="!editMode"
                                          :state="validateState('model.description')"
                                          @input="resetState('model.description')"/>

                            <b-form-invalid-feedback v-if="!$v.model.description.required">
                                Logger description is required.
                            </b-form-invalid-feedback>
                            <b-form-invalid-feedback v-if="!$v.model.description.maxLength">
                                Maximum field length is {{$v.model.description.$params.maxLength.max}}.
                            </b-form-invalid-feedback>
                        </b-form-group>

                        <div class="button-wrapper">
                            <b-button :disabled="formSent || !editMode" @click="sendForm">
                                <span v-if="newLoggerMode">Create</span>
                                <span v-else>Save</span>
                            </b-button>
                            &nbsp;
                            <b-button variant="warning" v-show="!newLoggerMode" @click="resetPassword">
                                Reset password
                            </b-button>
                        </div>

                        <br>
                        <b-alert variant="success" :show="loggerPassword != null" dismissible>
                            Generated new password for logger: <b>{{loggerPassword}}</b>.
                            Use it to configure the logging device.
                            Make sure you saved the password because you won't be able to see it again without resetting
                            it.
                        </b-alert>
                    </b-form>
                </b-col>
            </b-row>
        </b-container>
    </div>
</template>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator";
    import {Validation, validationMixin} from "vuelidate";
    import {maxLength, required} from "vuelidate/lib/validators";
    import DateService from "@/services/Date.service";
    import {LoggerModel} from "@/models/logger.model";
    import {mapGetters} from "vuex";

    @Component({
        mixins: [
            validationMixin
        ],
        computed: {
            ...mapGetters("settings", ["loggerPassword"])
        },
        validations: {
            model: {
                name: {
                    required,
                    maxLength: maxLength(64)
                },
                description: {
                    required,
                    maxLength: maxLength(256)
                }
            }
        }
    })
    export default class EditLogger extends Vue {
        loggerPassword!: string | null;

        model: LoggerModel = {
            id: -1,
            name: "",
            description: "",
            createdAt: -1
        };
        formSent = false;

        get editMode(): boolean {
            return true;
        }

        get newLoggerMode(): boolean {
            return this.loggerId === null;
        }

        get loggerId(): number | null {
            const idString = this.$route.params["id"];
            return idString ? parseInt(idString) : null;
        }

        mounted(): void {
            this.onComponentLoaded();
        }

        destroyed(): void {
            this.$store.commit("settings/setLoggerPassword", null);
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

            if (this.newLoggerMode)
                this.createLogger();
            else
                this.updateLogger();
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

        resetPassword(): void {
            const msg = "Are your sure you want to reset password for this logger? All logging devices " +
                "using this credentials will lose access to th server.";
            this.$bvModal.msgBoxConfirm(msg, {
                okTitle: "Yes",
                cancelTitle: "No",
            }).then(value => {
                if (value)
                    this.doResetPassword();
            });
        }

        private onComponentLoaded(): void {
            if (!this.newLoggerMode) {
                this.$store.dispatch("settings/getLogger", this.loggerId).then((logger: LoggerModel) => {
                    this.model.id = logger.id;
                    this.model.name = logger.name;
                    this.model.description = logger.description;
                    this.model.createdAt = logger.createdAt;
                });
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

        private createLogger() {
            this.$store.dispatch("settings/addLogger", this.model).then((logger: LoggerModel) => {
                this.$store.commit("settings/setLoggerPassword", logger.password);
                this.$router.push(`/loggers/${logger.id}/edit`);
            });
        }

        private updateLogger() {
            this.$store.dispatch("settings/updateLogger", this.model);
        }

        private doResetPassword(): void {
            this.$store.dispatch("settings/resetLoggerPassword", this.model.id).then((logger: LoggerModel) => {
                this.$store.commit("settings/setLoggerPassword", logger.password);
            });
        }
    }
</script>

<style lang="scss" scoped>

    .button-wrapper {
        text-align: right;
    }

</style>