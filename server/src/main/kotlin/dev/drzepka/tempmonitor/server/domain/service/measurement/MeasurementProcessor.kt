package dev.drzepka.tempmonitor.server.domain.service.measurement

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.drzepka.tempmonitor.server.application.dto.measurement.MeasurementResource
import java.io.OutputStream

/**
 * This class allows to process measurements sequentially without reading them all into memory.
 */
class MeasurementProcessor(private val sequence: Sequence<MeasurementResource>) {

    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun writeToStream(stream: OutputStream) {
        val generator = objectMapper.factory.createGenerator(stream)
        generator.writeStartArray()
        sequence.forEach { objectMapper.writeValue(generator, it) }
        generator.writeEndArray()
        generator.close()
    }
}