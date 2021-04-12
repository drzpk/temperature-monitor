package dev.drzepka.tempmonitor.server.domain.service

abstract class ConfigurationProviderService {

    fun getInt(path: String, default: Int? = null): Int {
        return getOptionalInt(path, default) ?: reportPropertyNotFound(path)
    }

    abstract fun getOptionalInt(path: String, default: Int? = null): Int?

    private fun reportPropertyNotFound(path: String): Nothing {
        throw IllegalStateException("Property '$path' wasn't found")
    }
}