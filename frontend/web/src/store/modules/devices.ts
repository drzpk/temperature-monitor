import {Module} from "vuex";
import {Device} from "@/models/device";

export interface DevicesState {
    devices: Array<Device>;
}

const module: Module<DevicesState, unknown> = {
    state: {
        devices: []
    },
    getters: {},
    mutations: {},
    actions: {
        addDevice(context, device: Device): Promise<number> {
            return Promise.resolve(112);
        },

        updateDevice(context, device: Device): Promise<void> {
            return Promise.resolve();
        },

        getDeviceById(context, deviceId: number): Promise<Device> {
            // todo: connect with the server
            return new Promise((resolve) => {
                // todo: return a copy
                const device: Device = {
                    id: deviceId,
                    name: "example name",
                    description: "example description",
                    createdAt: new Date().getTime()
                };
                resolve(device);
            });
        }
    },
    modules: {}
};

export default module;