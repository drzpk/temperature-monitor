package dev.drzepka.tempmonitor.server.application.dto.device

import dev.drzepka.tempmonitor.server.application.dto.measurement.MeasurementResource
import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import java.time.Instant

class DeviceResource {
    var id = 0
    var name = ""
    var description = ""
    var mac = ""
    var createdAt: Instant = Instant.now()
    var lastMeasurement: MeasurementResource? = null

    companion object {
        fun fromEntity(device: Device, lastMeasurement: Measurement? = null): DeviceResource = DeviceResource().apply {
            id = device.id!!
            name = device.name
            description = device.description
            mac = device.mac
            createdAt = device.createdAt
            this.lastMeasurement = lastMeasurement?.let {
                MeasurementResource.fromEntity(
                    it
                )
            }
        }
    }
}