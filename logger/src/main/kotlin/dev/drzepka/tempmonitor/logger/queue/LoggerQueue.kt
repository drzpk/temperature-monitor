package dev.drzepka.tempmonitor.logger.queue

import dev.drzepka.tempmonitor.logger.util.Logger
import dev.drzepka.tempmonitor.logger.util.Mockable
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

@Mockable
class LoggerQueue<T>(private val maxAge: Duration) {
    private val log by Logger()
    private val queue = ConcurrentLinkedQueue<QueueItem<T>>()

    fun itemsCount(): Int = queue.size

    fun enqueue(item: T) {
        if (queue.size >= MAX_ELEMENTS) {
            val oldest = queue.poll()
            log.warn(
                "Queue has maximum size of {} elements, dropping the oldest element from {}",
                MAX_ELEMENTS,
                oldest.createdAt
            )
        }

        queue.add(QueueItem(item))
    }


    @Synchronized
    fun processQueue(handler: ((item: QueueItem<T>) -> ProcessingResult)) {
        val iterator = queue.iterator()
        while (iterator.hasNext()) {
            val current = iterator.next()

            if (isExpired(current)) {
                log.warn("Item {} has expired and will be removed from the queue.", current.createdAt)
                iterator.remove()
                continue
            }

            val result = processItem(handler, current)
            when (result.status) {
                ProcessingResult.Status.OK -> {
                    if (current.serverErrors > 0) {
                        log.info(
                            "Processing of item {} completed successfully after {} server errors",
                            current.createdAt,
                            current.serverErrors
                        )
                    }

                    iterator.remove()
                }
                ProcessingResult.Status.SERVER_UNAVAILABLE -> {
                    log.error("Processing of item {} finished with SERVER_UNAVAILABLE error", current.createdAt)
                    current.serverErrors++
                    break
                }
            }

            if (!result.`continue`)
                break
        }
    }

    private fun isExpired(item: QueueItem<T>): Boolean = item.createdAt.plus(maxAge).isBefore(Instant.now())

    private fun processItem(handler: ((item: QueueItem<T>) -> ProcessingResult), item: QueueItem<T>): ProcessingResult {
        return try {
            handler.invoke(item)
        } catch (e: Exception) {
            throw IllegalStateException("Unhandled exception while processing item ${item.createdAt}", e)
        }
    }

    companion object {
        private const val MAX_ELEMENTS = 15_000
    }
}