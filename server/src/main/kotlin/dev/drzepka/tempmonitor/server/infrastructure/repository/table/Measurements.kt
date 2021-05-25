package dev.drzepka.tempmonitor.server.infrastructure.repository.table

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant

object Measurements : IdTable<Instant>("measurements") {
    override val id = timestamp("time").entityId()
    val deviceId = integer("device_id")
    val temperature = decimal("temperature", 4, 2)
    val humidity = integer("humidity")
    val batteryVoltage = decimal("battery_voltage", 5, 3)
    val batteryLevel = integer("battery_level")
}