package dev.drzepka.tempmonitor.logger.bluetooth.bluetoothctl

import dev.drzepka.tempmonitor.logger.model.BluetoothData
import dev.drzepka.tempmonitor.logger.model.BluetoothServiceData
import dev.drzepka.tempmonitor.logger.model.MacAddress

class OutputAnalyzer {

    fun analyze(tokens: Collection<String>): List<BluetoothData> {
        return tokens.mapNotNull { extractBluetoothData(it) }
    }

    private fun extractBluetoothData(text: String): BluetoothData? {
        return extractBluetoothServiceData(text)
    }

    private fun extractBluetoothServiceData(text: String): BluetoothServiceData? {
        val match = SERVICE_DATA_REGEX.find(text) ?: return null
        return BluetoothServiceData(MacAddress(match.groupValues[1]), match.groupValues[2])
    }

    companion object {
        private val SERVICE_DATA_REGEX = Regex("\\[CHG]\\sDevice\\s((?:[A-F0-9]{2}:){5}[A-F0-9]{2})\\sServiceData Value:\\n\\s+((?:[a-f0-9]{2}\\s)+)")
    }
}