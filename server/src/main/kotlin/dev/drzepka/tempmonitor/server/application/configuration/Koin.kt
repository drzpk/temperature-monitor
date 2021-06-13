package dev.drzepka.tempmonitor.server.application.configuration

import dev.drzepka.tempmonitor.server.application.service.*
import dev.drzepka.tempmonitor.server.domain.repository.DeviceRepository
import dev.drzepka.tempmonitor.server.domain.repository.LoggerRepository
import dev.drzepka.tempmonitor.server.domain.repository.MeasurementRepository
import dev.drzepka.tempmonitor.server.infrastructure.DatabaseInitializer
import dev.drzepka.tempmonitor.server.infrastructure.repository.ExposedDeviceRepository
import dev.drzepka.tempmonitor.server.infrastructure.repository.ExposedLoggerRepository
import dev.drzepka.tempmonitor.server.infrastructure.repository.ExposedMeasurementRepository
import dev.drzepka.tempmonitor.server.infrastructure.service.PBKDF2HashService
import io.ktor.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun Application.temperatureMonitorKoinModule(): Module = module {

    // Application
    single { DeviceService(get(), get()) }
    single { MeasurementService(get(), get(), get()) }
    single { LoggerService(get(), get(), get()) }
    single { PasswordGeneratorService(get()) }

    // Infrastructure
    val databaseInitializer = DatabaseInitializer(environment.config)
    single { databaseInitializer }
    single { ConfigurationProviderService(environment.config) }
    single<HashService> { PBKDF2HashService() }

    single<DeviceRepository> { ExposedDeviceRepository() }
    single<MeasurementRepository> { ExposedMeasurementRepository(get()) }
    single<LoggerRepository> { ExposedLoggerRepository() }
}