package dev.drzepka.tempmonitor.logger.model

import dev.drzepka.tempmonitor.logger.model.server.CreateMeasurementRequest
import java.time.Instant

class MeasurementPendingRequest(val actualRequest: CreateMeasurementRequest) {
    val createdAt = Instant.now()
    var trials = 0
}