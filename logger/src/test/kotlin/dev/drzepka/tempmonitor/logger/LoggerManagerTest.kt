package dev.drzepka.tempmonitor.logger

import dev.drzepka.tempmonitor.logger.executor.RequestExecutor
import dev.drzepka.tempmonitor.logger.model.BluetoothServiceData
import dev.drzepka.tempmonitor.logger.model.MacAddress
import dev.drzepka.tempmonitor.logger.model.server.CreateMeasurementRequest
import dev.drzepka.tempmonitor.logger.model.server.Device
import dev.drzepka.tempmonitor.logger.queue.LoggerQueue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class LoggerManagerTest {

    @Test
    fun `should decode data of known device`() {
        val device = Device().apply { id = 1; mac = "ab:cd:12" }
        val executor = mock<RequestExecutor> {
            on { getDevices() } doReturn listOf(device)
        }
        val queue = mock<LoggerQueue<CreateMeasurementRequest>>()

        val manager = LoggerManager(executor, queue)
        manager.refreshDevices()
        manager.decodeBluetoothData(
            BluetoothServiceData(
                MacAddress("AB:CD:12"),
                "11 11 11 11 11 11 11 11 11 11 11 11 11 11"
            )
        )
        manager.decodeBluetoothData(BluetoothServiceData(MacAddress("99:33:11"), ""))

        verify(queue, times(1)).enqueue(any())
    }


    @Test
    fun `should send measurements to server`() {

    }

    @Test
    fun `should stop sending measurements if time limit has been exceeded`() {

    }

}