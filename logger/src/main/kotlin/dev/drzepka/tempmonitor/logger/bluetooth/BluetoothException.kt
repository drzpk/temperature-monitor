package dev.drzepka.tempmonitor.logger.bluetooth

class BluetoothException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}