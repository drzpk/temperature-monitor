package dev.drzepka.tempmonitor.logger.queue

import java.time.Instant

class QueueItem<T>(val content: T) {
    val createdAt: Instant = Instant.now()
    var serverErrors = 0
}