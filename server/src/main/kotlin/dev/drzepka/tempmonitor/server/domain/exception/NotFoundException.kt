package dev.drzepka.tempmonitor.server.domain.exception

import java.lang.RuntimeException

class NotFoundException(message: String) : RuntimeException(message)