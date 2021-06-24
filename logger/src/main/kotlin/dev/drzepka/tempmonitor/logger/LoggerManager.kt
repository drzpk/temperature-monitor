package dev.drzepka.tempmonitor.logger

import dev.drzepka.tempmonitor.logger.converter.BluetoothServiceDataToMeasurementConverter
import dev.drzepka.tempmonitor.logger.executor.ConnectionException
import dev.drzepka.tempmonitor.logger.executor.RequestExecutor
import dev.drzepka.tempmonitor.logger.executor.ResponseException
import dev.drzepka.tempmonitor.logger.model.BluetoothServiceData
import dev.drzepka.tempmonitor.logger.model.MacAddress
import dev.drzepka.tempmonitor.logger.model.server.CreateMeasurementRequest
import dev.drzepka.tempmonitor.logger.model.server.Device
import dev.drzepka.tempmonitor.logger.queue.LoggerQueue
import dev.drzepka.tempmonitor.logger.queue.ProcessingResult
import dev.drzepka.tempmonitor.logger.queue.QueueItem
import dev.drzepka.tempmonitor.logger.util.Logger
import dev.drzepka.tempmonitor.logger.util.StopWatch
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

open class LoggerManager(
    private val executor: RequestExecutor,
    private val queue: LoggerQueue<CreateMeasurementRequest>
) {

    private val log by Logger()
    private val devices = ConcurrentHashMap<MacAddress, Device>()

    fun refreshDevices() {
        log.debug("Refreshing device list")

        val serverDevices = executor.getDevices()

        synchronized(devices) {
            devices.clear()
            serverDevices.forEach { devices[MacAddress(it.mac)] = it }
        }
    }

    fun decodeBluetoothData(bluetoothData: BluetoothServiceData) {
        val device = devices[bluetoothData.mac]
        if (device == null) {
            log.trace("Received bluetooth service data for unknown mac {}", bluetoothData.mac.value)
            return
        }

        val request = BluetoothServiceDataToMeasurementConverter.convertCustomFormat(bluetoothData)
        request.deviceId = device.id
        queue.enqueue(request)
    }

    fun sendMeasurementsToServer(timeLimit: Duration): Status {
        val stopWatch = StopWatch(true)

        var hasServerUnavailableError = false
        while (queue.itemsCount() > 0) {
            queue.processQueue {

                log.trace("Processing item {}", it.createdAt)
                val status = sendMeasurement(it)
                if (status == ProcessingResult.Status.SERVER_UNAVAILABLE)
                    hasServerUnavailableError = true

                val `continue` = if (stopWatch.current() > timeLimit && queue.itemsCount() > 0) {
                    stopWatch.stop()
                    log.warn(
                        "Some measurements ({}) couldn't be sent. Sending took {}, but time limit is {}",
                        queue.itemsCount(), stopWatch.elapsed(), timeLimit
                    )
                    false
                } else {
                    true
                }

                ProcessingResult(`continue` && !hasServerUnavailableError, status)
            }
        }

        return Status(hasServerUnavailableError)
    }

    private fun sendMeasurement(item: QueueItem<CreateMeasurementRequest>): ProcessingResult.Status {
        return try {
            doSendMeasurement(item)
        } catch (e: ConnectionException) {
            log.error("Connection exception", e)
            ProcessingResult.Status.SERVER_UNAVAILABLE
        } catch (e: ResponseException) {
            log.error("Error while sending measurement {}", item.createdAt, e)
            ProcessingResult.Status.OK
        }
    }

    private fun doSendMeasurement(item: QueueItem<CreateMeasurementRequest>): ProcessingResult.Status {
        log.debug("Sending measurement to server")
        val request = item.content
        request.timestampOffset = Duration.between(item.createdAt, Instant.now()).seconds

        executor.sendMeasurement(request)
        return ProcessingResult.Status.OK
    }

    data class Status(val serverUnavailable: Boolean)
}
