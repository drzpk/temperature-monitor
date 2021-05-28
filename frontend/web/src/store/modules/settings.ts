import {ActionContext, Module} from "vuex";
import {DeviceModel} from "@/models/device.model";
import ApiService from "@/services/Api.service";

interface SettingsState {
    devices: Array<DeviceModel> | null;
}

const module: Module<SettingsState, unknown> = {
    namespaced: true,
    state: {
        devices: null
    },
    getters: {
        devices(state): Array<DeviceModel> {
            if (state.devices)
                return state.devices;
            else
                return [];
        }
    },
    mutations: {
        setSettingsDevices(state, devices: Array<DeviceModel>) {
            state.devices = devices;
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
    },
    modules: {}
};

export default module;