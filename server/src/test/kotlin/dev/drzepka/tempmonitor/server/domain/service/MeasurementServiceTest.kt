package dev.drzepka.tempmonitor.server.domain.service

import dev.drzepka.tempmonitor.server.application.dto.measurement.CreateMeasurementRequest
import dev.drzepka.tempmonitor.server.application.service.ConfigurationProviderService
import dev.drzepka.tempmonitor.server.application.service.MeasurementService
import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.exception.ValidationException
import dev.drzepka.tempmonitor.server.domain.repository.DeviceRepository
import dev.drzepka.tempmonitor.server.domain.repository.MeasurementRepository
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.BDDAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class MeasurementServiceTest {

    private val configurationProviderService = mock<ConfigurationProviderService>()
    private val deviceRepository = mock<DeviceRepository>()
    private val measurementRepository = mock<MeasurementRepository>()

    @Test
    fun `should add measurement`() {
        val request = CreateMeasurementRequest().apply {
            deviceId = 1
            temperature = BigDecimal("21.00")
            humidity = 56
            batteryVoltage = BigDecimal("3.125")
            batteryLevel = 84
        }

        val device = Device().apply { active = true }
        whenever(deviceRepository.findById(eq(1))).thenReturn(device)

        getService().addMeasurement(request)

        val captor = argumentCaptor<Measurement>()
        verify(measurementRepository, times(1)).save(captor.capture())

        val entity = captor.firstValue
        BDDAssertions.then(entity.device).isSameAs(device)
        BDDAssertions.then(entity.temperature).isEqualTo(BigDecimal("21.00"))
        BDDAssertions.then(entity.humidity).isEqualTo(56)
        BDDAssertions.then(entity.batteryVoltage).isEqualTo(BigDecimal("3.125"))
        BDDAssertions.then(entity.batteryLevel).isEqualTo(84)
    }

    @Test
    fun `should validate add measurement`() {
        val request = CreateMeasurementRequest().apply {
            deviceId = 1
            temperature = BigDecimal.valueOf(500)
            humidity = 200
            batteryLevel = 101
            batteryVoltage = BigDecimal.valueOf(99)
        }

        val device = Device().apply { active = true }
        whenever(deviceRepository.findById(eq(1))).thenReturn(device)

        val caught = catchThrowable { getService().addMeasurement(request) }
        BDDAssertions.then(caught).isInstanceOf(ValidationException::class.java)

        val validationException = caught as ValidationException
        BDDAssertions.then(validationException.validationErrors.errors).hasSize(4)
    }

    @Test
    fun `should wait before adding another measurement`() {
        given(configurationProviderService.getInt("measurements.minimumCreationIntervalSeconds"))
            .willReturn(5)

        val request = CreateMeasurementRequest().apply {
            deviceId = 1
            temperature = BigDecimal.valueOf(21)
            humidity = 56
            batteryVoltage = BigDecimal("3.125")
            batteryLevel = 84
        }

        val device = Device().apply { active = true }
        whenever(deviceRepository.findById(eq(1))).thenReturn(device)

        val existingMeasurement = Measurement(device).apply { id = Instant.now() }
        whenever(measurementRepository.findLastForDevice(eq(1))).thenReturn(existingMeasurement)

        assertThatIllegalStateException().isThrownBy {
            getService().addMeasurement(request)
        }.withMessageContaining("Measurement for device 1 added too quickly, need to wait")

    }

    private fun getService(): MeasurementService = MeasurementService(
        configurationProviderService,
        deviceRepository,
        measurementRepository
    )
}