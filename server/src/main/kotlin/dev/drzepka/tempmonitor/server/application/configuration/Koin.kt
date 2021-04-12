package dev.drzepka.tempmonitor.server.application.configuration

import dev.drzepka.tempmonitor.server.domain.service.ConfigurationProviderService
import dev.drzepka.tempmonitor.server.domain.service.DatabaseProviderService
import dev.drzepka.tempmonitor.server.domain.service.DeviceService
import dev.drzepka.tempmonitor.server.domain.service.MeasurementService
import dev.drzepka.tempmonitor.server.infrastructure.service.ConfigurationProviderServiceImpl
import dev.drzepka.tempmonitor.server.infrastructure.service.DatabaseProviderServiceImpl
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.temperatureMonitorKoinModule(): Module = module {

    // Domain
    single { DeviceService() }
    single { MeasurementService(get(), get()) }

    // Infrastructure
    val databaseProviderServiceImpl = DatabaseProviderServiceImpl(environment.config)
    single<DatabaseProviderService> { databaseProviderServiceImpl }
    single<ConfigurationProviderService> { ConfigurationProviderServiceImpl(environment.config) }
}