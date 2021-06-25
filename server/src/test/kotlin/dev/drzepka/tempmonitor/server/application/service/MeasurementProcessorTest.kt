package dev.drzepka.tempmonitor.server.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import dev.drzepka.tempmonitor.server.domain.value.MeasurementData
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class MeasurementProcessorTest {

    private val today = LocalDate.now()
    private val mapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    @Test
    fun `should write sequence to stream`() {
        val measurements = listOf(
            createMeasurement(LocalTime.of(12, 14, 0), 15, 30),
            createMeasurement(LocalTime.of(13, 0, 1), 0, 96)
        )

        val processor = MeasurementProcessor(measurements.asSequence())
        val converted = processAndConvert(processor)
        then(converted).hasSize(2)

        val first = converted[0]
        then(first.time).isEqualTo(localTimeToInstant(LocalTime.of(12, 14, 0)))
        then(first.temperature).isEqualTo(BigDecimal("15.0"))
        then(first.humidity).isEqualTo(BigDecimal("30"))

        val second = converted[1]
        then(second.time).isEqualTo(localTimeToInstant(LocalTime.of(13, 0, 1)))
        then(second.temperature).isEqualTo(BigDecimal("0.0"))
        then(second.humidity).isEqualTo(BigDecimal("96"))
    }

    @Test
    fun `should aggregte measurements`() {
        val measurements = listOf(
            createMeasurement(LocalTime.of(12, 10, 0), 15, 30),
            createMeasurement(LocalTime.of(12, 14, 0), 16, 20),
            createMeasurement(LocalTime.of(12, 20, 0), 16, 20),
            createMeasurement(LocalTime.of(12, 25, 0), 76, 60),
            createMeasurement(LocalTime.of(12, 50, 0), 88, 40)
        )

        val processor = MeasurementProcessor(measurements.asSequence())
        processor.aggregationInterval = MeasurementProcessor.AggregationInterval.FIVETEEN_MINUTES

        val converted = processAndConvert(processor)
        then(converted).hasSize(4)

        val first = converted[0]
        then(first.time).isEqualTo(localTimeToInstant(LocalTime.of(12, 0, 0)))
        then(first.temperature).isEqualTo(BigDecimal("15.50"))
        then(first.humidity).isEqualTo(BigDecimal("25.00"))

        val second = converted[1]
        then(second.time).isEqualTo(localTimeToInstant(LocalTime.of(12, 15, 0)))
        then(second.temperature).isEqualTo(BigDecimal("46.00"))
        then(second.humidity).isEqualTo(BigDecimal("40.00"))

        val third = converted[2]
        then(third.time).isEqualTo(localTimeToInstant(LocalTime.of(12, 30, 0)))
        then(third.temperature).isEqualTo(BigDecimal("0.00"))
        then(third.humidity).isEqualTo(BigDecimal("0.00"))

        val fourth = converted[3]
        then(fourth.time).isEqualTo(localTimeToInstant(LocalTime.of(12, 45, 0)))
        then(fourth.temperature).isEqualTo(BigDecimal("88.00"))
        then(fourth.humidity).isEqualTo(BigDecimal("40.00"))
    }

    private fun createMeasurement(time: LocalTime, temperature: Int, humidity: Int): MeasurementData {
        return MeasurementData().apply {
            this.time = localTimeToInstant(time)
            this.temperature = BigDecimal.valueOf(temperature.toLong()).setScale(1, RoundingMode.UNNECESSARY)
            this.humidity = BigDecimal.valueOf(humidity.toLong())
        }
    }

    private fun localTimeToInstant(localTime: LocalTime): Instant =
        today.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant()

    private fun processAndConvert(processor: MeasurementProcessor): List<MeasurementData> {
        val byteStream = ByteArrayOutputStream()
        processor.writeToStream(byteStream)
        val bytes = byteStream.toByteArray()

        return mapper.readValue(bytes)
    }
}