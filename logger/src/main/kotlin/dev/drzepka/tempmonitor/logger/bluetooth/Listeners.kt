package dev.drzepka.tempmonitor.logger.bluetooth

import dev.drzepka.tempmonitor.logger.model.BluetoothServiceData

@FunctionalInterface
interface BroadcastListener {
    fun onDataReceived(data: BluetoothServiceData)
}