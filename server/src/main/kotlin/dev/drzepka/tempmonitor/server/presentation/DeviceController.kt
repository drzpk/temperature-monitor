package dev.drzepka.tempmonitor.server.presentation

import dev.drzepka.tempmonitor.server.application.dto.device.CreateDeviceRequest
import dev.drzepka.tempmonitor.server.application.service.DeviceService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get

fun Route.deviceController() {

    val deviceService = get<DeviceService>()

    route("/devices") {
        post("") {
            val request = call.receive<CreateDeviceRequest>()
            val dto = transaction {
                deviceService.createDevice(request)
            }

            call.respond(dto)
        }

        get("") {
            val list = transaction {
                deviceService.getDevices()
            }

            call.respond(list)
        }

        get("/{id}") {
            val deviceId = call.parameters["id"]!!.toInt()
            val dto = transaction {
                deviceService.getDevice(deviceId)
            }

            call.respond(dto)
        }

        delete("/{id}") {
            val deviceId = call.parameters["id"]!!.toInt()
            transaction {
                deviceService.deleteDevice(deviceId)
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}