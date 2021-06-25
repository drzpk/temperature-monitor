package dev.drzepka.tempmonitor.server.application.service

import dev.drzepka.tempmonitor.server.application.ValidationErrors
import dev.drzepka.tempmonitor.server.application.dto.measurement.CreateMeasurementRequest
import dev.drzepka.tempmonitor.server.application.dto.measurement.GetMeasurementsRequest
import dev.drzepka.tempmonitor.server.application.dto.measurement.MeasurementResource
import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.repository.DeviceRepository
import dev.drzepka.tempmonitor.server.domain.repository.MeasurementRepository
import dev.drzepka.tempmonitor.server.domain.service.measurement.MeasurementProcessor
import dev.drzepka.tempmonitor.server.domain.service.measurement.TestMeasurementDataGenerator
import dev.drzepka.tempmonitor.server.domain.util.Logger
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class MeasurementService(
    configurationProviderService: ConfigurationProviderService,
    private val deviceRepository: DeviceRepository,
    private val measurementRepository: MeasurementRepository
) {
    private val log by Logger()
    private val minimumCreationInterval =
        configurationProviderService.getInt("measurements.minimumCreationIntervalSeconds")

    fun addMeasurement(request: CreateMeasurementRequest) {
        val device = validateAddMeasurement(request)
        checkMeasurementNotAddedBeforeInterval(request.deviceId)

        val measurement = Measurement(device).apply {
            id = if (request.timestampOffset != null)
                Instant.now().minusSeconds(request.timestampOffset!!) else Instant.now()
            temperature = request.temperature
            humidity = request.humidity
            batteryVoltage = request.batteryVoltage
            batteryLevel = request.batteryLevel
        }

        measurementRepository.save(measurement)
        log.debug("Created measurement {} for device {}", measurement.id!!.epochSecond, device.id)
    }

    fun getMeasurements(request: GetMeasurementsRequest): MeasurementProcessor {
        val sequence = if (request.deviceId == -1) {
            val testGenerator = TestMeasurementDataGenerator(
                Instant.now().minus(3, ChronoUnit.HOURS),
                Instant.now(),
                Duration.ofSeconds(15)
            )
            testGenerator.asSequence()
        } else {
            // Check if device exists
            deviceRepository.findById(request.deviceId)!!
            measurementRepository.findForDevice(request.deviceId, request)
                .map { MeasurementResource.fromEntity(it) }
        }

        return MeasurementProcessor(sequence)
    }

    private fun validateAddMeasurement(request: CreateMeasurementRequest): Device {
        val validation = ValidationErrors()

        if (request.temperature < MIN_ALLOWED_TEMPERATURE || request.temperature > MAX_ALLOWED_TEMPERATURE)
            validation.addFieldError("temperature", "Temperature is out of allowed bounds: [-30; 60].")
        if (request.humidity < BigDecimal.ZERO || request.humidity > HUNDRED)
            validation.addFieldError("humidity", "Humidity is out of allowed bounds: [0; 100]")
        if (request.batteryLevel < MIN_ALLOWED_BATTERY_LEVEL || request.batteryLevel > MAX_ALLOWED_BATTERY_LEVEL)
            validation.addFieldError("batteryLevel", "Battery level is out of allowed bounds: [0; 100].")
        if (request.batteryVoltage < MIN_ALLOWED_BATTERY_VOLTAGE || request.batteryVoltage > MAX_ALLOWED_BATTERY_VOLTAGE)
            validation.addFieldError("batteryVoltage", "Battery voltage is out of allowed bounds: [0; 10].")

        val device = deviceRepository.findById(request.deviceId)
        if (device == null || !device.active)
            validation.addFieldError("deviceId", "Device wasn't found")

        validation.verify()

        return device!!
    }

    private fun checkMeasurementNotAddedBeforeInterval(deviceId: Int) {
        val lastMeasurement = measurementRepository.findLastForDevice(deviceId) ?: return

        val lastTime = lastMeasurement.id!!
        val now = Instant.now()
        if (!lastTime.plusSeconds(minimumCreationInterval.toLong()).isBefore(now)) {
            val remainingCooldown = Duration.between(lastTime, now)
            throw IllegalStateException("Measurement for device $deviceId added too quickly, need to wait another $remainingCooldown")
        }
    }

    companion object {
        private val MIN_ALLOWED_TEMPERATURE = BigDecimal.valueOf(-30)
        private val MAX_ALLOWED_TEMPERATURE = BigDecimal.valueOf(60)
        private val MIN_ALLOWED_BATTERY_VOLTAGE = BigDecimal.valueOf(0)
        private val MAX_ALLOWED_BATTERY_VOLTAGE = BigDecimal.valueOf(10)
        private const val MIN_ALLOWED_BATTERY_LEVEL = 0
        private const val MAX_ALLOWED_BATTERY_LEVEL = 100

        private val HUNDRED = BigDecimal.valueOf(100L)
    }

}