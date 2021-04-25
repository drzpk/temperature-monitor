package dev.drzepka.tempmonitor.server.domain.service.measurement

import dev.drzepka.tempmonitor.server.domain.dto.MeasurementDTO
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.util.*

class TestMeasurementDataGenerator(
    startTime: Instant,
    private val endTime: Instant,
    private val interval: Duration
) : Iterator<MeasurementDTO> {

    private val random = Random()
    private var currentTime = startTime

    override fun hasNext(): Boolean = currentTime.isBefore(endTime)

    override fun next(): MeasurementDTO {
        val measurement = createRandomMeasurement()
        currentTime = currentTime.plus(interval)
        return measurement
    }

    private fun createRandomMeasurement(): MeasurementDTO = MeasurementDTO().apply {
        time = currentTime
        temperature = BigDecimal.valueOf((random.nextInt(200) + 50) / 10.0).setScale(1, RoundingMode.FLOOR)
        humidity = random.nextInt(100) + 1
    }

}