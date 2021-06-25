package dev.drzepka.tempmonitor.server.application.service

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.drzepka.tempmonitor.server.application.dto.measurement.MeasurementResource
import dev.drzepka.tempmonitor.server.domain.value.MeasurementData
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit

/**
 * This class allows to process measurements sequentially without reading them all into memory.
 */
class MeasurementProcessor(private val sequence: Sequence<MeasurementData>) {

    var aggregationInterval: AggregationInterval? = null
    var maxSize: Int? = null

    private var currentAggregationMeasurement: MeasurementResource? = null
    private var currentAggregationCount = 0

    private val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    fun writeToStream(stream: OutputStream) {
        val generator = objectMapper.factory.createGenerator(stream)
        generator.writeStartArray()
        writeSequence(generator)
        generator.writeEndArray()
        generator.close()
    }

    private fun writeSequence(generator: JsonGenerator) {
        val limitedSequence = if (maxSize != null) sequence.take(maxSize!!) else sequence

        if (aggregationInterval != null)
            aggregateAndWriteSequence(limitedSequence, generator)
        else
            limitedSequence.forEach { objectMapper.writeValue(generator, it) }
    }

    private fun aggregateAndWriteSequence(sequence: Sequence<MeasurementData>, generator: JsonGenerator) {
        val iterator = sequence.iterator()
        if (!iterator.hasNext())
            return

        val first = iterator.next()
        resetCurrentAggregation(first.time)
        addToCurrentAggregation(first)

        var currentAggregationTime: Instant = currentAggregationMeasurement!!.time
        while (iterator.hasNext()) {
            val next = iterator.next()
            val time = truncateToAggregationInterval(next.time)

            if (time != currentAggregationTime) {
                completeCurrentAggregation(generator)
                resetCurrentAggregation(time)
                fillTimeGaps(currentAggregationTime, time, generator)
                currentAggregationTime = time
            }

            addToCurrentAggregation(next)
        }

        if (currentAggregationCount > 0)
            completeCurrentAggregation(generator)
    }

    private fun resetCurrentAggregation(time: Instant) {
        currentAggregationMeasurement = MeasurementResource()
        currentAggregationMeasurement!!.time = truncateToAggregationInterval(time)
        currentAggregationCount = 0
    }

    private fun addToCurrentAggregation(data: MeasurementData) {
        val current = currentAggregationMeasurement!!
        current.temperature = current.temperature.add(data.temperature)
        current.humidity = current.humidity.add(data.humidity)
        currentAggregationCount++
    }

    private fun completeCurrentAggregation(generator: JsonGenerator) {
        fun div(value: BigDecimal, items: Int): BigDecimal {
            return if (items > 0)
                value.div(BigDecimal.valueOf(items.toLong())).setScale(2, RoundingMode.HALF_EVEN)
            else
                BigDecimal.ZERO
        }

        val resource = MeasurementResource().apply {
            time = currentAggregationMeasurement!!.time
            temperature = div(currentAggregationMeasurement!!.temperature, currentAggregationCount)
            humidity = div(currentAggregationMeasurement!!.humidity, currentAggregationCount)
        }

        objectMapper.writeValue(generator, resource)
    }

    private fun fillTimeGaps(lastAggregationTime: Instant, endExclusive: Instant, generator: JsonGenerator) {
        // Last aggregation interval had some measurements so we need to start
        // from the next one that possibly didn't have any.
        var current = lastAggregationTime.plus(aggregationInterval!!.value)

        while (current < endExclusive) {
            val resource = MeasurementResource().apply {
                time = current
                temperature = BigDecimal("0.00")
                humidity = BigDecimal("0.00")
            }
            objectMapper.writeValue(generator, resource)
            current = current.plus(aggregationInterval!!.value)
        }
    }

    private fun truncateToAggregationInterval(input: Instant): Instant {
        return when (aggregationInterval!!) {
            AggregationInterval.MINUTE -> input.truncatedTo(ChronoUnit.MINUTES)
            AggregationInterval.FIVE_MINUTES -> truncateToClosest(input, ChronoUnit.HOURS, Duration.ofMinutes(5))
            AggregationInterval.FIVETEEN_MINUTES -> truncateToClosest(input, ChronoUnit.HOURS, Duration.ofMinutes(15))
            AggregationInterval.HOUR -> input.truncatedTo(ChronoUnit.HOURS)
            AggregationInterval.DAY -> input.truncatedTo(ChronoUnit.DAYS)
            AggregationInterval.WEEK -> input.truncatedTo(ChronoUnit.WEEKS)
            AggregationInterval.MONTH -> input.truncatedTo(ChronoUnit.MONTHS)
        }
    }

    @Suppress("SameParameterValue")
    private fun truncateToClosest(input: Instant, unit: TemporalUnit, step: Duration): Instant {
        var current = input.truncatedTo(unit)
        while (true) {
            val next = current.plus(step)
            if (next > input)
                break

            current = next
        }

        return current
    }

    enum class AggregationInterval(val value: TemporalAmount) {
        MINUTE(Duration.ofMinutes(1)),
        FIVE_MINUTES(Duration.ofMinutes(5)),
        FIVETEEN_MINUTES(Duration.ofMinutes(15)),
        HOUR(Duration.ofHours(1)),
        DAY(Period.ofDays(1)),
        WEEK(Period.ofWeeks(1)),
        MONTH(Period.ofMonths(1))
    }
}