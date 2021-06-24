package dev.drzepka.tempmonitor.logger.configuration

data class LoggerConfiguration(
    val serverUrl: String,
    val loggerId: Int,
    val loggerSecret: String
)