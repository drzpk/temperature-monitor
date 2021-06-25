package dev.drzepka.tempmonitor.server.domain.repository

import dev.drzepka.tempmonitor.server.domain.TimeRangeQuery
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.value.MeasurementData

interface MeasurementRepository {
    fun findLastForDevice(deviceId: Int): Measurement?
    fun findForDevice(deviceId: Int, timeRangeQuery: TimeRangeQuery): Sequence<MeasurementData>
    fun save(measurement: Measurement)
}