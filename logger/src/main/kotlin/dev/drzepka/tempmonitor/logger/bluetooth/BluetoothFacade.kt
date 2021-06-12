package dev.drzepka.tempmonitor.logger.bluetooth

interface BluetoothFacade {
    fun startListening()
    fun stopListening()

    fun addBroadcastListener(listener: BroadcastListener)
}