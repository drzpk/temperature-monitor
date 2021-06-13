package dev.drzepka.tempmonitor.server.application

import dev.drzepka.tempmonitor.server.domain.entity.Logger
import io.ktor.auth.*

interface TemperatureMonitorPrincipal : Principal

class LoggerPrincipal(val logger: Logger) : TemperatureMonitorPrincipal