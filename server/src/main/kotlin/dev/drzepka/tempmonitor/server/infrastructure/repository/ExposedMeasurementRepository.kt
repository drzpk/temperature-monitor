package dev.drzepka.tempmonitor.server.infrastructure.repository

import dev.drzepka.tempmonitor.server.domain.TimeRangeQuery
import dev.drzepka.tempmonitor.server.domain.entity.Measurement
import dev.drzepka.tempmonitor.server.domain.repository.DeviceRepository
import dev.drzepka.tempmonitor.server.domain.repository.MeasurementRepository
import dev.drzepka.tempmonitor.server.infrastructure.repository.table.Measurements
import dev.drzepka.tempmonitor.server.infrastructure.repository.util.timeRangeQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder

class ExposedMeasurementRepository(private val deviceRepository: DeviceRepository) : MeasurementRepository {

    override fun findLastForDevice(deviceId: Int): Measurement? {
        return Measurements.select { Measurements.deviceId eq deviceId }
            .orderBy(Measurements.id to SortOrder.DESC)
            .limit(1)
            .firstOrNull()
            ?.let { rowToEntity(it) }
    }

    override fun findForDevice(deviceId: Int, timeRangeQuery: TimeRangeQuery): kotlin.sequences.Sequence<Measurement> {
        return Measurements.select { Measurements.deviceId eq deviceId }
            .timeRangeQuery(timeRangeQuery, Measurements.id)
            .asSequence()
            .map { rowToEntity(it) }

    }

    override fun save(measurement: Measurement) {
        Measurements.insert {
            entityToRow(measurement, it)
        }
    }

    private fun entityToRow(entity: Measurement, stmt: UpdateBuilder<Int>) {
        stmt[Measurements.id] = entity.id!!
        stmt[Measurements.deviceId] = entity.device.id!!
        stmt[Measurements.temperature] = entity.temperature
        stmt[Measurements.humidity] = entity.humidity
        stmt[Measurements.batteryVoltage] = entity.batteryVoltage
        stmt[Measurements.batteryLevel] = entity.batteryLevel
    }

    private fun rowToEntity(row: ResultRow): Measurement {
        val device = deviceRepository.findById(row[Measurements.deviceId])!!
        return Measurement(device).apply {
            id = row[Measurements.id].value
            temperature = row[Measurements.temperature]
            humidity = row[Measurements.humidity]
            batteryVoltage = row[Measurements.batteryVoltage]
            batteryLevel = row[Measurements.batteryLevel]
        }
    }
}