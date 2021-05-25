package dev.drzepka.tempmonitor.server.domain.repository

import dev.drzepka.tempmonitor.server.domain.TimeRangeQuery
import dev.drzepka.tempmonitor.server.domain.entity.Measurement

interface MeasurementRepository {
    fun findLastForDevice(deviceId: Int): Measurement?
    fun findForDevice(deviceId: Int, timeRangeQuery: TimeRangeQuery): Sequence<Measurement>
    fun save(measurement: Measurement)
}