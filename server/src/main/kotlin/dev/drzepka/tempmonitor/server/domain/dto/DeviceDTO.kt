package dev.drzepka.tempmonitor.server.domain.dto

import dev.drzepka.tempmonitor.server.domain.entity.Device
import java.time.Instant

class DeviceDTO {
    var id = 0
    var name = ""
    var description = ""
    var createdAt: Instant = Instant.now()

    companion object {
        fun fromEntity(source: Device): DeviceDTO = DeviceDTO().apply {
            id = source.id.value
            name = source.name
            description = source.description
            createdAt = source.createdAt
        }
    }
}