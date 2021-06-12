package dev.drzepka.tempmonitor.logger.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Logger : ReadOnlyProperty<Any, Logger> {
    private var value: Logger? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        return value ?: createLogger(thisRef)
    }

    private fun createLogger(thisRef: Any): Logger {
        return LoggerFactory.getLogger(thisRef::class.java)
    }
}