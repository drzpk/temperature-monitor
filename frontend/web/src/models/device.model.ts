export interface DeviceModel {
    id: number;
    name: string;
    description: string
    createdAt: number;
}

export interface AddDeviceRequest {
    name: string | null;
    description: string | null;
}

export interface UpdateDeviceRequest {
    name: string | null;
    description: string | null;
}
