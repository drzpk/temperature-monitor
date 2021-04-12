package dev.drzepka.tempmonitor.server.domain.entity

import dev.drzepka.tempmonitor.server.domain.entity.table.MeasurementsTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.Serializable
import java.time.Instant

class Measurement(id: EntityID<Instant>) : Entity<Instant>(id), Serializable {
    companion object : EntityClass<Instant, Measurement>(MeasurementsTable)

    var deviceId by MeasurementsTable.deviceId
    var temperature by MeasurementsTable.temperature
    var humidity by MeasurementsTable.humidity
    var batteryVoltage by MeasurementsTable.batteryVoltage
    var batteryLevel by MeasurementsTable.batteryLevel
}