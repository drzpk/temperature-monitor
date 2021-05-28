import axios from "axios";
import {DeviceModel, UpdateDeviceRequest} from "@/models/device.model";

function responseErrorHandler(error: Error): never {
    // nothing here yet
    throw error;
}

class ApiService {
    getDevices(): Promise<Array<DeviceModel>> {
        return axios.get<Array<DeviceModel>>("/api/devices")
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    getDevice(deviceId: number): Promise<DeviceModel> {
        return axios.get<DeviceModel>(`/api/devices/${deviceId}`)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    addDevice(request: UpdateDeviceRequest): Promise<DeviceModel> {
        return axios.post<DeviceModel>("/api/devices", request)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    updateDevice(deviceId: number, request: UpdateDeviceRequest): Promise<DeviceModel> {
        return axios.patch<DeviceModel>(`/api/devices/${deviceId}`, request)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    deleteDevice(deviceId: number): Promise<unknown> {
        return axios.delete<void>(`/api/devices/${deviceId}`)
            .catch(responseErrorHandler);
    }
}

export default new ApiService();