package dev.drzepka.tempmonitor.logger

import dev.drzepka.tempmonitor.logger.bluetooth.BroadcastListener
import dev.drzepka.tempmonitor.logger.bluetooth.bluetoothctl.BluetoothCtlBluetoothFacade
import dev.drzepka.tempmonitor.logger.model.BluetoothServiceData

fun main() {
    val facade = BluetoothCtlBluetoothFacade()
    facade.addBroadcastListener(object : BroadcastListener {
        override fun onDataReceived(data: BluetoothServiceData) {
            System.out.println("data received")
        }
    })

    facade.startListening()
    Thread.sleep(600_000)
}