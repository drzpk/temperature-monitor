package dev.drzepka.tempmonitor.server.domain.exception

import dev.drzepka.tempmonitor.server.domain.util.ValidationErrors


class ValidationException(val validationErrors: ValidationErrors) : RuntimeException("Validation error")