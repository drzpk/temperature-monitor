import {MeasurementModel} from "@/models/measurement.model";

export interface DeviceModel {
    id: number;
    name: string;
    description: string;
    mac: string;
    createdAt: number;
    lastMeasurement: MeasurementModel | null;
}

export interface AddDeviceRequest {
    name: string | null;
    description: string | null;
}

export interface UpdateDeviceRequest {
    name: string | null;
    description: string | null;
}
