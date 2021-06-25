package dev.drzepka.tempmonitor.server.domain.service.measurement

import dev.drzepka.tempmonitor.server.application.dto.measurement.MeasurementResource
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.util.*

class TestMeasurementDataGenerator(
    startTime: Instant,
    private val endTime: Instant,
    private val interval: Duration
) : Iterator<MeasurementResource> {

    private val random = Random()
    private var currentTime = startTime

    override fun hasNext(): Boolean = currentTime.isBefore(endTime)

    override fun next(): MeasurementResource {
        val measurement = createRandomMeasurement()
        currentTime = currentTime.plus(interval)
        return measurement
    }

    private fun createRandomMeasurement(): MeasurementResource = MeasurementResource().apply {
        time = currentTime
        temperature = BigDecimal.valueOf((random.nextInt(200) + 50) / 10.0).setScale(1, RoundingMode.FLOOR)
        humidity = BigDecimal.valueOf(random.nextInt(100) + 1L)
    }

}