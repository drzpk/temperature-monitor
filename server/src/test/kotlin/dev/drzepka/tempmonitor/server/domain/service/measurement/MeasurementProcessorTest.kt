package dev.drzepka.tempmonitor.server.domain.service.measurement

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import dev.drzepka.tempmonitor.server.domain.dto.MeasurementDTO
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
        then(first.humidity).isEqualTo(30)

        val second = converted[1]
        then(second.time).isEqualTo(localTimeToInstant(LocalTime.of(13, 0, 1)))
        then(second.temperature).isEqualTo(BigDecimal("0.0"))
        then(second.humidity).isEqualTo(96)
    }

    private fun createMeasurement(time: LocalTime, temperature: Int, humidity: Int): MeasurementDTO {
        return MeasurementDTO().apply {
            this.time = localTimeToInstant(time)
            this.temperature = BigDecimal.valueOf(temperature.toLong()).setScale(1, RoundingMode.UNNECESSARY)
            this.humidity = humidity
        }
    }

    private fun localTimeToInstant(localTime: LocalTime): Instant =
        today.atTime(localTime).atZone(ZoneId.systemDefault()).toInstant()

    private fun processAndConvert(processor: MeasurementProcessor): List<MeasurementDTO> {
        val byteStream = ByteArrayOutputStream()
        processor.writeToStream(byteStream)
        val bytes = byteStream.toByteArray()

        return mapper.readValue(bytes)
    }
}