package dev.drzepka.tempmonitor.server.domain.entity

import dev.drzepka.tempmonitor.server.domain.entity.table.DevicesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.Serializable
import java.time.Instant

class Device(id: EntityID<Int>) : IntEntity(id), Serializable {
    companion object : IntEntityClass<Device>(DevicesTable)

    var name by DevicesTable.name
    var description by DevicesTable.description
    var createdAt: Instant by DevicesTable.createdAt
    var active by DevicesTable.active

    override fun delete() {
        throw UnsupportedOperationException("Device cannot be deleted, only set as inactive")
    }
}