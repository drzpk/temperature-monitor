import {ActionContext, Module} from "vuex";
import {DeviceModel} from "@/models/device.model";
import ApiService from "@/services/Api.service";
import {CreateLoggerRequest, LoggerModel, UpdateLoggerRequest} from "@/models/logger.model";

interface SettingsState {
    devices: Array<DeviceModel> | null;
    loggers: Array<LoggerModel> | null;
    loggerPassword: string | null;
}

const module: Module<SettingsState, unknown> = {
    namespaced: true,
    state: {
        devices: null,
        loggers: null,
        loggerPassword: null
    },
    getters: {
        devices(state): Array<DeviceModel> {
            if (state.devices)
                return state.devices;
            else
                return [];
        },
        loggers(state): Array<LoggerModel> {
            if (state.loggers)
                return state.loggers;
            else
                return [];
        },
        loggerPassword(state): string | null {
            return state.loggerPassword;
        }
    },
    mutations: {
        setSettingsDevices(state, devices: Array<DeviceModel>) {
            state.devices = devices;
        },
        setLoggers(state, loggers: Array<LoggerModel>) {
            state.loggers = loggers;
        },
        setLoggerPassword(state, password: string | null) {
            state.loggerPassword = password;
        }
    },
    actions: {
        refreshDevices(context: ActionContext<any, any>) {
            ApiService.getDevices().then(devices => context.commit("setSettingsDevices", devices));
        },

        deleteDevice(context, device: DeviceModel): Promise<unknown> {
            return ApiService.deleteDevice(device.id).then(() => {
                context.dispatch("refreshDevices");
            });
        },

        refreshLoggers(context: ActionContext<any, any>) {
            ApiService.getLoggers().then(loggers => context.commit("setLoggers", loggers));
        },

        getLogger(context: ActionContext<any, any>, loggerId: number) {
            if (context.state.loggers) {
                for (let i = 0; i < context.state.loggers.length; i++) {
                    const model = context.state.loggers[i];
                    if (model.id === loggerId)
                        return Promise.resolve(model);
                }
            }

            return ApiService.getLogger(loggerId);
        },

        addLogger(context: ActionContext<any, any>, logger: LoggerModel): Promise<LoggerModel> {
            const request: CreateLoggerRequest = {
                name: logger.name,
                description: logger.description
            };

            return ApiService.createLogger(request).then(logger => {
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("refreshLoggers");
                return logger;
            })
        },

        updateLogger(context: ActionContext<any, any>, logger: LoggerModel) {
            const request: UpdateLoggerRequest = {
                id: logger.id,
                name: logger.name,
                description: logger.description
            };

            return ApiService.updateLogger(request);
        },

        deleteLogger(context: ActionContext<any, any>, logger: LoggerModel) {
            return ApiService.deleteLogger(logger.id).then(() => {
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("refreshLoggers");
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("settings/refreshDevices", null, {root: true});
            });
        },

        resetLoggerPassword(context: ActionContext<any, any>, loggerId): Promise<LoggerModel> {
            return ApiService.resetLoggerPassword(loggerId);
        }
    },
    modules: {}
};

export default module;