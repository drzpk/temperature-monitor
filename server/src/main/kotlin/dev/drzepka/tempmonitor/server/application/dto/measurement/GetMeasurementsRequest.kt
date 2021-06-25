package dev.drzepka.tempmonitor.server.application.dto.measurement

import dev.drzepka.tempmonitor.server.application.TimeRangeRequest
import dev.drzepka.tempmonitor.server.application.service.MeasurementProcessor
import java.time.Instant
import java.time.temporal.ChronoUnit

class GetMeasurementsRequest : TimeRangeRequest() {
    var deviceId = 0
    override var from: Instant? = Instant.now().minus(1, ChronoUnit.HOURS)
    override var to: Instant? = Instant.now()
    var aggregationInterval: MeasurementProcessor.AggregationInterval? = null
}