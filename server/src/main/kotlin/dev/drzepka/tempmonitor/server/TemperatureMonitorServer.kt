package dev.drzepka.tempmonitor.server

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.drzepka.tempmonitor.server.application.configuration.setupRouting
import dev.drzepka.tempmonitor.server.application.configuration.setupSecurity
import dev.drzepka.tempmonitor.server.application.configuration.setupStatusPages
import dev.drzepka.tempmonitor.server.application.configuration.temperatureMonitorKoinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.sessions.*
import org.koin.ktor.ext.Koin

fun Application.temperatureMonitorServer() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }

    install(Sessions) {

    }

    install(Koin) {
        modules(temperatureMonitorKoinModule())
    }

    setupSecurity()
    setupRouting()
    setupStatusPages()
}