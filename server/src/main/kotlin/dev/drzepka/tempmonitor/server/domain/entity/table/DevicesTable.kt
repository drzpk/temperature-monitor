package dev.drzepka.tempmonitor.server.domain.entity.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp

object DevicesTable : IntIdTable("devices") {
    val name = varchar("name", 64)
    val description = varchar("description", 256)
    val createdAt = timestamp("created_at")
    var active = bool("active")
}