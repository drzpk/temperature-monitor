package dev.drzepka.tempmonitor.server.application.dto.measurement

import java.math.BigDecimal

class CreateMeasurementRequest {
    var deviceId = 0
    var temperature: BigDecimal = BigDecimal.ZERO
    var humidity = 0
    var batteryVoltage: BigDecimal = BigDecimal.ZERO
    var batteryLevel = 0
}