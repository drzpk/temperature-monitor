package dev.drzepka.tempmonitor.server.presentation

import dev.drzepka.tempmonitor.server.application.configuration.MEASUREMENTS_AUTH
import dev.drzepka.tempmonitor.server.application.dto.measurement.CreateMeasurementRequest
import dev.drzepka.tempmonitor.server.application.dto.measurement.GetMeasurementsRequest
import dev.drzepka.tempmonitor.server.application.service.MeasurementProcessor
import dev.drzepka.tempmonitor.server.application.service.MeasurementService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.get
import java.time.Instant

fun Route.measurementController() {

    val measurementService = get<MeasurementService>()

    route("/measurements") {
        authenticate(MEASUREMENTS_AUTH) {
            post {
                val request = call.receive<CreateMeasurementRequest>()
                transaction {
                    measurementService.addMeasurement(request)
                }

                call.respond(HttpStatusCode.Created)
            }
        }

        get("") {
            val request = GetMeasurementsRequest()
                .apply {
                    deviceId = call.parameters["deviceId"]!!.toInt()
                    page = call.parameters["page"]?.toInt() ?: 1
                    size = call.parameters["size"]?.toInt() ?: 10
                    from = call.parameters["from"]?.let { Instant.ofEpochSecond(it.toLong()) }
                    to = call.parameters["to"]?.let { Instant.ofEpochSecond(it.toLong()) }
                    aggregationInterval = call.parameters["aggregationInterval"]?.let {
                        MeasurementProcessor.AggregationInterval.valueOf(it)
                    }
                }

            newSuspendedTransaction {
                val processor = measurementService.getMeasurements(request)
                call.respondOutputStream(contentType = ContentType.Application.Json) {
                    processor.writeToStream(this@respondOutputStream)
                }
            }
        }
    }
}