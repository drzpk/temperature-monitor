package dev.drzepka.tempmonitor.server.domain.dto

import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import java.math.BigDecimal
import java.time.Instant

class MeasurementDTO {
    var time = Instant.now()
    var temperature = BigDecimal.ZERO
    var humidity = 0

    companion object {
        fun fromEntity(entity: Measurement): MeasurementDTO {
            return MeasurementDTO().apply {
                time = entity.id.value
                temperature = entity.temperature
                humidity = entity.humidity
            }
        }
    }
}