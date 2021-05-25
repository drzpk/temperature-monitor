package dev.drzepka.tempmonitor.server.application.dto.measurement

import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import java.math.BigDecimal
import java.time.Instant

class MeasurementResource {
    var time = Instant.now()
    var temperature = BigDecimal.ZERO
    var humidity = 0

    companion object {
        fun fromEntity(entity: Measurement): MeasurementResource {
            return MeasurementResource().apply {
                time = entity.id
                temperature = entity.temperature
                humidity = entity.humidity
            }
        }
    }
}