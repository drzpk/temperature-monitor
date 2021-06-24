package dev.drzepka.tempmonitor.logger.configuration

import dev.drzepka.tempmonitor.logger.util.Logger
import java.io.File
import java.util.*

object ConfigurationLoader {

    private const val CONFIGURATION_FILENAME = "configuration.properties"

    private val log by Logger()
    private var configuration: LoggerConfiguration? = null

    fun getConfiguration(): LoggerConfiguration {
        if (configuration == null)
            throw IllegalStateException("Logger configuration wasn't loaded")
        return configuration!!
    }

    fun loadConfiguration() {
        val properties = getClasspathProperties() ?: getFilesystemProperties()
        configuration = LoggerConfiguration(
            getStringProperty(properties, "serverUrl"),
            getStringProperty(properties, "loggerId").toInt(),
            getStringProperty(properties, "loggerSecret")
        )
    }

    private fun getClasspathProperties(): Properties? {
        val isTest = System.getProperty("TEST") != null
        if (isTest)
            log.info("Test property is defined, loading configuration from classpath")
        else
            return null

        val resource = javaClass.classLoader.getResourceAsStream("configuration.properties")!!
        val properties = Properties()
        properties.load(resource)
        resource.close()
        return properties
    }

    private fun getFilesystemProperties(): Properties {
        val file = File(CONFIGURATION_FILENAME)
        if (!file.isFile)
            throw IllegalStateException("Configuration file '$CONFIGURATION_FILENAME' wasn't found in the current directory")

        val reader = file.reader()
        val properties = Properties()
        properties.load(reader)
        reader.close()
        return properties
    }

    private fun getStringProperty(properties: Properties, name: String): String {
        return properties.getProperty(name) ?: throw IllegalArgumentException("Property '$name' wasn't found")
    }
}