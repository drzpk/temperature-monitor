package dev.drzepka.tempmonitor.logger.executor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import dev.drzepka.tempmonitor.logger.configuration.LoggerConfiguration
import dev.drzepka.tempmonitor.logger.model.server.CreateMeasurementRequest
import dev.drzepka.tempmonitor.logger.model.server.Device
import dev.drzepka.tempmonitor.logger.util.Mockable
import java.io.OutputStreamWriter
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@Mockable
class RequestExecutor(configuration: LoggerConfiguration) {
    private val authorization = createAuthorization(configuration)
    private val objectMapper = ObjectMapper()
    private val serverUrl = URL(configuration.serverUrl)

    init {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    fun getDevices(): List<Device> {
        val type = objectMapper.typeFactory.constructCollectionType(List::class.java, Device::class.java)
        return executeRequest<Any, List<Device>>("GET", "/api/devices", null, type)!!
    }

    fun sendMeasurement(request: CreateMeasurementRequest) {
        val type = objectMapper.typeFactory.constructType(Any::class.java)
        executeRequest<CreateMeasurementRequest, Any>("POST", "/api/measurements", request, type)
    }

    private fun createAuthorization(configuration: LoggerConfiguration): String {
        val credentials = "${configuration.loggerId}:${configuration.loggerSecret}"
        val encoded = Base64.getEncoder().encode(credentials.encodeToByteArray())
        return "Basic ${encoded.decodeToString()}"
    }

    private fun <Req, Res> executeRequest(method: String, url: String, requestBody: Req?, type: JavaType): Res? {
        try {
            return doExecuteRequest(method, url, requestBody, type)
        } catch (e: ConnectException) {
            throw ConnectionException(url, e)
        } catch (e: Exception) {
            throw ResponseException(url, e)
        }
    }

    private fun <Req, Res> doExecuteRequest(method: String, url: String, requestBody: Req?, type: JavaType): Res? {
        val fullUrl = URL(serverUrl.toString() + url)
        val connection = fullUrl.openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        connection.setRequestProperty("Authorization", authorization)
        connection.requestMethod = method
        connection.doOutput = true

        if (requestBody != null) {
            connection.setRequestProperty("Content-Type", "application/json")

            val writer = OutputStreamWriter(connection.outputStream)
            objectMapper.writeValue(writer, requestBody)
            writer.close()
        } else {
            connection.connect()
        }

        val responseStream = connection.inputStream
        val bytes = responseStream.readBytes()
        responseStream.close()

        return if (bytes.isNotEmpty())
            objectMapper.readValue<Res>(bytes, type)
        else
            null
    }
}