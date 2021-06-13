package dev.drzepka.tempmonitor.server.application.service

import dev.drzepka.tempmonitor.server.application.ValidationErrors
import dev.drzepka.tempmonitor.server.application.dto.logger.CreateLoggerRequest
import dev.drzepka.tempmonitor.server.application.dto.logger.LoggerResource
import dev.drzepka.tempmonitor.server.application.dto.logger.UpdateLoggerRequest
import dev.drzepka.tempmonitor.server.domain.entity.Logger
import dev.drzepka.tempmonitor.server.domain.exception.NotFoundException
import dev.drzepka.tempmonitor.server.domain.repository.LoggerRepository
import java.time.Instant

class LoggerService(
    private val loggerRepository: LoggerRepository,
    private val passwordGeneratorService: PasswordGeneratorService
) {

    private val log by dev.drzepka.tempmonitor.server.domain.util.Logger()

    fun listLoggers(): Collection<LoggerResource> {
        return loggerRepository.findAll(active = true)
            .map { LoggerResource.fromEntity(it) }
    }

    fun createLogger(request: CreateLoggerRequest): LoggerResource {
        validateCreateLogger(request)

        log.info("Creating new logger with name '{}' and description '{}'", request.name, request.description)
        val generatedPassword = generatePassword()
        val logger = Logger().apply {
            name = request.name!!
            description = request.description!!
            password = generatedPassword.hash
            createdAt = Instant.now()
            active = true
        }

        loggerRepository.save(logger)

        log.info("Created new logger with id {}", logger.id)
        return LoggerResource.fromEntity(logger, generatedPassword.plainText)
    }

    fun updateLogger(request: UpdateLoggerRequest): LoggerResource {
        validateUpdateLogger(request)

        val logger = getLogger(request.id)
        if (request.name != null && request.name != logger.name && loggerWithNameExists(request.name!!))
            throw IllegalStateException("Logger with name \"${request.name}\" already exists.")

        request.name?.let { logger.name = it }
        request.description?.let { logger.description = it }

        loggerRepository.save(logger)

        log.info("Updated logger {}", logger.id)
        return LoggerResource.fromEntity(logger)
    }

    fun resetPassword(loggerId: Int): LoggerResource {
        log.info("Resetting password of logger {}", loggerId)

        val logger = getLogger(loggerId)
        val generatedPassword = generatePassword()
        logger.password = generatedPassword.hash

        loggerRepository.save(logger)
        return LoggerResource.fromEntity(logger, generatedPassword.plainText)
    }

    fun deleteLogger(loggerId: Int) {
        log.info("Deleting logger {}", loggerId)
        val logger = getLogger(loggerId)
        logger.active = false
        loggerRepository.save(logger)
    }

    private fun validateCreateLogger(request: CreateLoggerRequest) {
        val validation = ValidationErrors()
        if (request.name == null || request.name!!.isEmpty() || request.name!!.length > 64)
            validation.addFieldError("name", "Logger name must have length between 1 and 64 characters.")
        if (request.description == null || request.description!!.isEmpty() || request.description!!.length > 256)
            validation.addFieldError("description", "Logger description must have length between 1 and 256 characters.")

        if (request.name != null && loggerWithNameExists(request.name!!))
            validation.addObjectError("Device with name \"${request.name!!}\" already exists.")

        validation.verify()
    }

    private fun validateUpdateLogger(request: UpdateLoggerRequest) {
        val validation = ValidationErrors()
        if (request.name != null && (request.name!!.isEmpty() || request.name!!.length > 64))
            validation.addFieldError("name", "Logger name must have length between 1 and 64 characters.")
        if (request.description != null && (request.description!!.isEmpty() || request.description!!.length > 256))
            validation.addFieldError("description", "Logger description must have length between 1 and 256 characters.")

        validation.verify()
    }

    private fun getLogger(id: Int): Logger {
        var found = loggerRepository.findById(id)
        if (found != null && !found.active) {
            log.warn("Found logger {} but it isn't active", found.id)
            found = null
        }

        if (found == null)
            throw NotFoundException("Logger $id wasn't found")
        return found
    }

    private fun loggerWithNameExists(name: String): Boolean {
        return loggerRepository.findByNameAndActive(name, true) != null
    }

    private fun generatePassword() = passwordGeneratorService.generatePassword(20, 25, true)

}