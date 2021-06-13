import {ActionContext, Module} from "vuex";
import {DeviceModel} from "@/models/device.model";
import ApiService from "@/services/Api.service";

export interface DevicesState {
    devices: Array<DeviceModel> | null;
    currentDevice: DeviceModel | null;
}

const module: Module<DevicesState, unknown> = {
    namespaced: true,
    state: {
        devices: [],
        currentDevice: null
    },
    getters: {
        currentDevice(state): DeviceModel | null {
            return state.currentDevice;
        }
    },
    mutations: {
        setDevices(state, devices: Array<DeviceModel>) {
          state.devices = devices;
        },
        setCurrentDevice(state, device: DeviceModel | null) {
            state.currentDevice = device;
        }
    },
    actions: {
        refreshDevices(context: ActionContext<any, any>) {
            ApiService.getDevices().then(devices => context.commit("setDevices", devices));
        },

        addDevice(context, device: DeviceModel): Promise<DeviceModel> {
            const request = {
                name: device.name,
                description: device.description
            };
            return ApiService.addDevice(request).then(device => {
                return context.dispatch("refreshDevices").then(() => {
                    return device;
                });
            });
        },

        updateDevice(context, device: DeviceModel): Promise<DeviceModel> {
            const request = {
                name: device.name,
                description: device.description,
                mac: device.mac
            };
            return ApiService.updateDevice(device.id, request).then((device) => {
                return context.dispatch("refreshDevices").then(() => {
                    return device;
                });
            });
        },

        deleteDevice(context: ActionContext<any, any>, device: DeviceModel): Promise<unknown> {
            return ApiService.deleteDevice(device.id).then(() => {
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("refreshDevices");
                // noinspection JSIgnoredPromiseFromCall
                context.dispatch("settings/refreshDevices", null, {root: true});
            });
        },

        setCurrentDevice(context: ActionContext<any, any>, deviceId: number | null): Promise<DeviceModel | null> {
            if (deviceId == null) {
                context.commit("setCurrentDevice", null);
                return Promise.resolve(null);
            }

            if (context.state.devices) {
                for (let i = 0; i < context.state.devices.length; i++) {
                    const model = context.state.devices[i];
                    if (model.id === deviceId)
                        return Promise.resolve(model);
                }
            }

            return ApiService.getDevice(deviceId).then(device => {
                context.commit("setCurrentDevice", device);
                return device;
            });
        }
    },
    modules: {}
};

export default module;