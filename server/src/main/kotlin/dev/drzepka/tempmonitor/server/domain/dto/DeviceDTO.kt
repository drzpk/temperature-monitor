package dev.drzepka.tempmonitor.server.domain.dto

import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import java.time.Instant

class DeviceDTO {
    var id = 0
    var name = ""
    var description = ""
    var createdAt: Instant = Instant.now()
    var lastMeasurement: MeasurementDTO? = null

    companion object {
        fun fromEntity(device: Device, lastMeasurement: Measurement? = null): DeviceDTO = DeviceDTO().apply {
            id = device.id.value
            name = device.name
            description = device.description
            createdAt = device.createdAt
            this.lastMeasurement = lastMeasurement?.let { MeasurementDTO.fromEntity(it) }
        }
    }
}