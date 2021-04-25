export interface MeasurementData {
    minY: number;
    maxY: number;
    measurements: Array<Measurement>;
}

export interface Measurement {
    time: Date;
    value: number;
}