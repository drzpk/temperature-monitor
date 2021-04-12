package dev.drzepka.tempmonitor.server.domain.service

import dev.drzepka.tempmonitor.server.AbstractDatabaseTest
import dev.drzepka.tempmonitor.server.domain.dto.CreateMeasurementRequest
import dev.drzepka.tempmonitor.server.domain.dto.DeviceDTO
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.entity.table.MeasurementsTable
import dev.drzepka.tempmonitor.server.domain.exception.ValidationException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.BDDAssertions.assertThatExceptionOfType
import org.assertj.core.api.BDDAssertions.then
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
internal class MeasurementServiceTest : AbstractDatabaseTest() {

    override val tables = arrayOf(MeasurementsTable)

    private val configurationProviderService = mock<ConfigurationProviderService> {}
    private val deviceService = mock<DeviceService> {
        on {getDevice(eq(1))}.thenReturn(DeviceDTO())
    }

    @Test
    fun `should add measurement`() {
        val request = CreateMeasurementRequest().apply {
            deviceId = 1
            temperature = BigDecimal.valueOf(21)
            humidity = 56
            batteryVoltage = BigDecimal("3.125")
            batteryLevel = 84
        }

        transaction {
            getService().addMeasurement(request)
        }

        transaction {
            val measurement = Measurement.all().first()
            then(measurement.deviceId).isEqualTo(1)
            then(measurement.temperature).isEqualTo(BigDecimal("21.00"))
            then(measurement.humidity).isEqualTo(56)
            then(measurement.batteryVoltage).isEqualTo(BigDecimal("3.125"))
            then(measurement.batteryLevel).isEqualTo(84)
        }
    }

    @Test
    fun `should validate add measurement`() {
        val request = CreateMeasurementRequest().apply {
            deviceId = 1
            temperature = BigDecimal.valueOf(100)
        }

        assertThatExceptionOfType(ValidationException::class.java).isThrownBy {
            getService().addMeasurement(request)
        }
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

        transaction {
            getService().addMeasurement(request)
        }

        transaction {
            assertThatIllegalStateException().isThrownBy {
                getService().addMeasurement(request)
            }
                .withMessageContaining("Measurement for device 1 added too quickly, need to wait")
        }
    }

    private fun getService(): MeasurementService = MeasurementService(configurationProviderService, deviceService)
}