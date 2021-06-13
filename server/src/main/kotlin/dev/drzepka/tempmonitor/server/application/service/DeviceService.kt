package dev.drzepka.tempmonitor.server.application.service

import dev.drzepka.tempmonitor.server.application.ValidationErrors
import dev.drzepka.tempmonitor.server.application.dto.device.CreateDeviceRequest
import dev.drzepka.tempmonitor.server.application.dto.device.DeviceResource
import dev.drzepka.tempmonitor.server.application.dto.device.UpdateDeviceRequest
import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.exception.NotFoundException
import dev.drzepka.tempmonitor.server.domain.repository.DeviceRepository
import dev.drzepka.tempmonitor.server.domain.repository.MeasurementRepository
import dev.drzepka.tempmonitor.server.domain.util.Logger
import dev.drzepka.tempmonitor.server.domain.util.Mockable
import java.time.Instant

@Mockable
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val measurementRepository: MeasurementRepository
) {

    private val log by Logger()

    fun getDevices(): Collection<DeviceResource> {
        return deviceRepository.findAll(active = true)
            .map {
                val lastMeasurement = measurementRepository.findLastForDevice(it.id!!)
                DeviceResource.fromEntity(it, lastMeasurement)
            }
    }

    fun createDevice(request: CreateDeviceRequest): DeviceResource {
        validateCreateDevice(request)

        log.info("Creating new device with name '{}' and description '{}'", request.name, request.description)
        val device = Device().apply {
            name = request.name!!
            description = request.description!!
            mac = request.mac!!
            createdAt = Instant.now()
            active = true
        }

        deviceRepository.save(device)

        log.info("Created new device with id {}", device.id)
        return DeviceResource.fromEntity(device)
    }

    fun updateDevice(request: UpdateDeviceRequest): DeviceResource {
        validateUpdateDevice(request)

        val device =
            deviceRepository.findById(request.id) ?: throw NotFoundException("Device ${request.id} wasn't found")

        if (request.name != null && request.name != device.name && deviceWithNameExists(request.name!!))
            throw IllegalStateException("Device with name \"${request.name!!}\" already exists.")

        request.name?.let { device.name = it }
        request.description?.let { device.description = it }
        request.mac?.let { device.mac = it }

        deviceRepository.save(device)

        log.info("Updated device {}", device.id)
        return DeviceResource.fromEntity(device)
    }

    fun getDevice(deviceId: Int): DeviceResource {
        val device = getDeviceEntity(deviceId)
        return DeviceResource.fromEntity(device)
    }

    fun deleteDevice(deviceId: Int) {
        log.info("Deleting device {}", deviceId)
        val device = getDeviceEntity(deviceId)
        device.active = false
        deviceRepository.save(device)
    }

    private fun validateCreateDevice(request: CreateDeviceRequest) {
        val validation = ValidationErrors()
        if (request.name == null || request.name!!.isEmpty() || request.name!!.length > 64)
            validation.addFieldError("name", "Name must have length between 1 and 64 characters.")
        if (request.description == null || request.description!!.isEmpty() || request.description!!.length > 256)
            validation.addFieldError("description", "Description must have length between 1 and 256 characters.")
        if (request.mac == null || request.mac!!.isBlank() || request.mac!!.length > 64)
            validation.addFieldError("mac", "Mac must have length between 1 and 64 characters.")

        if (request.name != null && deviceWithNameExists(request.name!!))
            validation.addObjectError("Device with name \"${request.name!!}\" already exists.")

        validation.verify()
    }

    private fun validateUpdateDevice(request: UpdateDeviceRequest) {
        val validation = ValidationErrors()
        if (request.name != null && (request.name!!.isEmpty() || request.name!!.length > 64))
            validation.addFieldError("name", "Name must have length between 1 and 64 characters.")
        if (request.description != null && (request.description!!.isEmpty() || request.description!!.length > 256))
            validation.addFieldError("description", "Description must have length between 1 and 256 characters")
        if (request.mac != null && (request.mac!!.isBlank() || request.mac!!.length > 64))
            validation.addFieldError("mac", "Mac must have length between 1 and 64 characters.")

        validation.verify()
    }

    private fun deviceWithNameExists(name: String): Boolean {
        return deviceRepository.findByNameAndActive(name, true) != null
    }

    private fun getDeviceEntity(deviceId: Int): Device {
        var device = deviceRepository.findById(deviceId)
        if (device?.active != true)
            device = null

        if (device == null)
            throw NotFoundException("Device with id $deviceId wasn't found")

        return device
    }
}