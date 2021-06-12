package dev.drzepka.tempmonitor.logger.model

class MacAddress(private val value: String) {

    override fun equals(other: Any?): Boolean {
        if (other !is MacAddress)
            return false

        return value.toLowerCase() == other.value.toLowerCase()
    }
}