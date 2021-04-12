package dev.drzepka.tempmonitor.server.infrastructure.service

import dev.drzepka.tempmonitor.server.domain.service.ConfigurationProviderService
import io.ktor.config.*

class ConfigurationProviderServiceImpl(private val config: ApplicationConfig) : ConfigurationProviderService() {

    override fun getOptionalInt(path: String, default: Int?): Int? {
        val property =config.propertyOrNull(path) ?: return null
        val value = property.getString()
        return value.toInt()
    }
}