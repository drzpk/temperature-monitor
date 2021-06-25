export interface MeasurementData {
    minY: number;
    maxY: number;
    measurements: Array<Measurement>;
}

export class Measurement {
    time: Date;
    value: number;

    constructor(time: Date, value: number) {
        this.time = time;
        this.value = value;
    }
}