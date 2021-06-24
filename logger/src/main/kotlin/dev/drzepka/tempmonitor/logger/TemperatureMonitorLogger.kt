package dev.drzepka.tempmonitor.logger

import dev.drzepka.tempmonitor.logger.configuration.ConfigurationLoader
import dev.drzepka.tempmonitor.logger.executor.RequestExecutor
import dev.drzepka.tempmonitor.logger.queue.LoggerQueue
import java.time.Duration
import kotlin.concurrent.thread

fun main() {
    ConfigurationLoader.loadConfiguration()
    val executor = RequestExecutor(ConfigurationLoader.getConfiguration())

    val manager = LoggerManager(executor, LoggerQueue(Duration.ofHours(24)))
    val orchestrator = LoggerOrchestrator(manager)

    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        orchestrator.stop()
    })

    orchestrator.start()
}