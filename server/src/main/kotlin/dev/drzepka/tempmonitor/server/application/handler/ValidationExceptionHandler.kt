package dev.drzepka.tempmonitor.server.application.handler

import dev.drzepka.tempmonitor.server.application.dto.ValidationErrorDTO
import dev.drzepka.tempmonitor.server.application.dto.ValidationErrorsDTO
import dev.drzepka.tempmonitor.server.domain.util.FieldError
import dev.drzepka.tempmonitor.server.domain.util.ObjectError
import dev.drzepka.tempmonitor.server.domain.util.ValidationError
import dev.drzepka.tempmonitor.server.domain.exception.ValidationException
import io.ktor.http.*

class ValidationExceptionHandler : ExceptionHandler<ValidationException> {

    override fun handle(exception: ValidationException): HandlerResult {
        val errors = ValidationErrorsDTO()
        errors.errors.addAll(exception.validationErrors.errors.map { convertError(it) })

        return HandlerResult(HttpStatusCode.UnprocessableEntity, errors)
    }

    private fun convertError(error: ValidationError): ValidationErrorDTO {
        return when (error) {
            is ObjectError -> {
                ValidationErrorDTO().apply {
                    type = "object"
                    message =  error.message
                }
            }
            is FieldError -> {
                ValidationErrorDTO().apply {
                    type = "field"
                    field = error.field
                    message = error.message
                }
            }
        }
    }
}