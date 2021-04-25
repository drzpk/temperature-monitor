package dev.drzepka.tempmonitor.server.domain.service

import dev.drzepka.tempmonitor.server.domain.dto.CreateMeasurementRequest
import dev.drzepka.tempmonitor.server.domain.dto.GetMeasurementsRequest
import dev.drzepka.tempmonitor.server.domain.dto.MeasurementDTO
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.entity.table.MeasurementsTable
import dev.drzepka.tempmonitor.server.domain.service.measurement.MeasurementProcessor
import dev.drzepka.tempmonitor.server.domain.service.measurement.TestMeasurementDataGenerator
import dev.drzepka.tempmonitor.server.domain.util.Logger
import dev.drzepka.tempmonitor.server.domain.util.ValidationErrors
import org.jetbrains.exposed.sql.SortOrder
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class MeasurementService(
    configurationProviderService: ConfigurationProviderService,
    private val deviceService: DeviceService
) {
    private val log by Logger()
    private val minimumCreationInterval =
        configurationProviderService.getInt("measurements.minimumCreationIntervalSeconds")

    fun addMeasurement(request: CreateMeasurementRequest) {
        validateAddMeasurement(request)
        checkDeviceExists(request.deviceId)
        checkMeasurementNotAddedBeforeInterval(request.deviceId)

        val measurement = Measurement.new(Instant.now()) {
            deviceId = request.deviceId
            temperature = request.temperature
            humidity = request.humidity
            batteryVoltage = request.batteryVoltage
            batteryLevel = request.batteryLevel
        }
        log.debug("Created measurement {} for device {}", measurement.id.value.epochSecond, measurement.deviceId)
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
            deviceService.getDevice(request.deviceId)
            getData(request)
        }

        return MeasurementProcessor(sequence)
    }

    private fun validateAddMeasurement(request: CreateMeasurementRequest) {
        val validation = ValidationErrors()

        if (request.temperature < MIN_ALLOWED_TEMPERATURE || request.temperature > MAX_ALLOWED_TEMPERATURE)
            validation.addFieldError("temperature", "Temperature is out of allowed bounds: [-30; 60].")
        if (request.humidity < 0 || request.humidity > 100)
            validation.addFieldError("humidity", "Humidity is out of allowed bounds: [0; 100]")
        if (request.batteryLevel < MIN_ALLOWED_BATTERY_LEVEL || request.batteryLevel > MAX_ALLOWED_BATTERY_LEVEL)
            validation.addFieldError("batteryLevel", "Battery level is out of allowed bounds: [0; 100].")
        if (request.batteryVoltage < MIN_ALLOWED_BATTERY_VOLTAGE || request.batteryVoltage > MAX_ALLOWED_BATTERY_VOLTAGE)
            validation.addFieldError("batteryVoltage", "Battery voltage is out of allowed bounds: [0; 10].")

        validation.verify()
    }

    private fun checkDeviceExists(deviceId: Int) {
        deviceService.getDevice(deviceId)
    }

    private fun checkMeasurementNotAddedBeforeInterval(deviceId: Int) {
        val lastMeasurement = Measurement.find { MeasurementsTable.deviceId eq deviceId }
            .orderBy(MeasurementsTable.id to SortOrder.DESC)
            .firstOrNull() ?: return

        val lastTime = lastMeasurement.id.value
        val now = Instant.now()
        if (!lastTime.plusSeconds(minimumCreationInterval.toLong()).isBefore(now)) {
            val remainingCooldown = Duration.between(lastTime, now)
            throw IllegalStateException("Measurement for device $deviceId added too quickly, need to wait another $remainingCooldown")
        }
    }

    private fun getData(request: GetMeasurementsRequest): Sequence<MeasurementDTO> {
        return Measurement.find { MeasurementsTable.deviceId eq request.deviceId }
            .orderBy(MeasurementsTable.id to SortOrder.ASC)
            .asSequence()
            .map { MeasurementDTO.fromEntity(it) }
    }

    companion object {
        private val MIN_ALLOWED_TEMPERATURE = BigDecimal.valueOf(-30)
        private val MAX_ALLOWED_TEMPERATURE = BigDecimal.valueOf(60)
        private val MIN_ALLOWED_BATTERY_VOLTAGE = BigDecimal.valueOf(0)
        private val MAX_ALLOWED_BATTERY_VOLTAGE = BigDecimal.valueOf(10)
        private const val MIN_ALLOWED_BATTERY_LEVEL = 0
        private const val MAX_ALLOWED_BATTERY_LEVEL = 100
    }

}