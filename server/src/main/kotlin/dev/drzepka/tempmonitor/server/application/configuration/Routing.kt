package dev.drzepka.tempmonitor.server.application.configuration

import dev.drzepka.tempmonitor.server.presentation.deviceController
import io.ktor.application.*
import io.ktor.routing.*

fun Application.setupRouting() {

    routing {
        route("/api") {
            deviceController()
        }
    }
}