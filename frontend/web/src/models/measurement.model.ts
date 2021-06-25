export interface MeasurementModel {
    temperature: number;
    humidity: number;
}

export interface GetMeasurementsRequest {
    deviceId: number;
    size: number;
    aggregationInterval?: AggregationInterval
}

export enum AggregationInterval {
    MINUTE = "MINUTE",
    FIVE_MINUTES = "FIVE_MINUTES",
    FIVETEEN_MINUTES = "FIVETEEN_MINUTES",
    HOUR = "HOUR",
    DAY = "DAY",
    WEEK = "WEEK",
    MONTH = "MONTH"
}