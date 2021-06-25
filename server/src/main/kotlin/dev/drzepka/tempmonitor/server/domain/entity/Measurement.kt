package dev.drzepka.tempmonitor.server.domain.entity

import java.math.BigDecimal
import java.time.Instant

class Measurement(var device: Device) : Entity<Instant>() {
    var temperature: BigDecimal = BigDecimal.ZERO
    var humidity: BigDecimal = BigDecimal.ZERO
    var batteryVoltage: BigDecimal = BigDecimal.ZERO
    var batteryLevel = 0
}