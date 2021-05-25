package dev.drzepka.tempmonitor.server.application.configuration

import dev.drzepka.tempmonitor.server.application.service.ConfigurationProviderService
import dev.drzepka.tempmonitor.server.application.service.DeviceService
import dev.drzepka.tempmonitor.server.application.service.MeasurementService
import dev.drzepka.tempmonitor.server.domain.repository.DeviceRepository
import dev.drzepka.tempmonitor.server.domain.repository.MeasurementRepository
import dev.drzepka.tempmonitor.server.infrastructure.DatabaseInitializer
import dev.drzepka.tempmonitor.server.infrastructure.repository.ExposedDeviceRepository
import dev.drzepka.tempmonitor.server.infrastructure.repository.ExposedMeasurementRepository
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.temperatureMonitorKoinModule(): Module = module {

    // Domain
    single { DeviceService(get(), get()) }
    single { MeasurementService(get(), get(), get()) }

    // Infrastructure
    single { DatabaseInitializer(environment.config) }
    single { ConfigurationProviderService(environment.config) }

    single<DeviceRepository> { ExposedDeviceRepository() }
    single<MeasurementRepository> { ExposedMeasurementRepository(get()) }
}