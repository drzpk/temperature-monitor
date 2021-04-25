package dev.drzepka.tempmonitor.server.domain.service

import dev.drzepka.tempmonitor.server.domain.dto.CreateDeviceRequest
import dev.drzepka.tempmonitor.server.domain.dto.DeviceDTO
import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.entity.table.DevicesTable
import dev.drzepka.tempmonitor.server.domain.entity.table.MeasurementsTable
import dev.drzepka.tempmonitor.server.domain.exception.NotFoundException
import dev.drzepka.tempmonitor.server.domain.util.Logger
import dev.drzepka.tempmonitor.server.domain.util.Mockable
import dev.drzepka.tempmonitor.server.domain.util.ValidationErrors
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

@Mockable
class DeviceService {

    private val log by Logger()

    fun getDevices(): Collection<DeviceDTO> {
        return transaction {
            Device.find { DevicesTable.active eq true }
                .map {
                    val lastMeasurement = Measurement
                        .find { MeasurementsTable.deviceId eq it.id.value }
                        .orderBy(MeasurementsTable.id to SortOrder.DESC)
                        .limit(1)
                        .firstOrNull()
                    DeviceDTO.fromEntity(it, lastMeasurement)
                }
        }
    }

    fun createDevice(request: CreateDeviceRequest): DeviceDTO {
        validateCreateDevice(request)

        log.info("Creating new device with name '{}' and description '{}'", request.name, request.description)
        val created = Device.new {
            name = request.name!!
            description = request.description!!
            createdAt = Instant.now()
            active = true
        }

        log.info("Created new device with id {}", created.id.value)
        return DeviceDTO.fromEntity(created)
    }

    fun getDevice(deviceId: Int): DeviceDTO {
        val device = getDeviceEntity(deviceId)
        return DeviceDTO.fromEntity(device)
    }

    fun deleteDevice(deviceId: Int) {
        log.info("Deleting device {}", deviceId)
        val device = getDeviceEntity(deviceId)
        device.active = false
    }

    private fun validateCreateDevice(request: CreateDeviceRequest) {
        val validation = ValidationErrors()
        if (request.name == null || request.name!!.isEmpty() || request.name!!.length > 64)
            validation.addFieldError("name", "Name must have length between 1 and 64 characters.")
        if (request.description == null || request.description!!.isEmpty() || request.description!!.length > 256)
            validation.addFieldError("description", "Description must have length between 1 and 256 characters")

        if (request.name != null) {
            val found = !Device.find { (DevicesTable.active eq true) and (DevicesTable.name eq request.name!!) }.empty()
            if (found)
                validation.addObjectError("Device with name \"${request.name!!}\" already exists.")
        }

        validation.verify()
    }

    private fun getDeviceEntity(deviceId: Int): Device {
        var device = Device.findById(deviceId)
        if (device?.active != true)
            device = null

        if (device == null)
            throw NotFoundException("Device with id $deviceId wasn't found")

        return device
    }
}