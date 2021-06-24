package dev.drzepka.tempmonitor.logger.model.server

import java.math.BigDecimal

class CreateMeasurementRequest {
    var deviceId = 0
    var temperature: BigDecimal = BigDecimal.ZERO
    var humidity: BigDecimal = BigDecimal.ZERO
    var batteryVoltage: BigDecimal = BigDecimal.ZERO
    var batteryLevel = 0
    var timestampOffset: Long? = null
}