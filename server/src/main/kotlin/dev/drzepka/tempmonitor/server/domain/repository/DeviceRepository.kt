package dev.drzepka.tempmonitor.server.domain.repository

import dev.drzepka.tempmonitor.server.domain.entity.Device

interface DeviceRepository {
    fun findById(id: Int): Device?
    fun findByNameAndActive(name: String, active: Boolean): Device?
    fun findAll(active: Boolean? = null): Collection<Device>
    fun save(device: Device)
}