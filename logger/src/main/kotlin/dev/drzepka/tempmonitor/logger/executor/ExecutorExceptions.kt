package dev.drzepka.tempmonitor.logger.executor

sealed class ExecutorException(message: String, cause: Throwable) : RuntimeException(message, cause)

class ConnectionException(url: String, cause: Throwable) : ExecutorException("Cannot to $url", cause)

class ResponseException(url: String, cause: Throwable) : ExecutorException("Server responded with error at $url", cause)
