import axios from "axios";
import {AddDeviceRequest, DeviceModel, UpdateDeviceRequest} from "@/models/device.model";
import {CreateLoggerRequest, LoggerModel, UpdateLoggerRequest} from "@/models/logger.model";

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

    addDevice(request: AddDeviceRequest): Promise<DeviceModel> {
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

    getLoggers(): Promise<LoggerModel[]> {
        return axios.get<LoggerModel[]>("/api/loggers")
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    getLogger(loggerId: number): Promise<LoggerModel> {
        return axios.get<LoggerModel>(`/api/loggers/${loggerId}`)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    createLogger(request: CreateLoggerRequest): Promise<LoggerModel> {
        return axios.post<LoggerModel>("/api/loggers", request)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    updateLogger(request: UpdateLoggerRequest): Promise<LoggerModel> {
        return axios.patch(`/api/loggers/${request.id}`, request)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }

    deleteLogger(loggerId: number): Promise<any> {
        return axios.delete(`/api/loggers/${loggerId}`)
            .catch(responseErrorHandler);
    }

    resetLoggerPassword(loggerId: number): Promise<LoggerModel> {
        return axios.delete<LoggerModel>(`/api/loggers/${loggerId}/password`)
            .then(response => response.data)
            .catch(responseErrorHandler);
    }
}

export default new ApiService();