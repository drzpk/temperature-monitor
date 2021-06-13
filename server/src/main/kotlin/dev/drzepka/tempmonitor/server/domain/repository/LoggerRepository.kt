package dev.drzepka.tempmonitor.server.domain.repository

import dev.drzepka.tempmonitor.server.domain.entity.Logger

interface LoggerRepository {
    fun findById(id: Int): Logger?
    fun findByNameAndActive(name: String, active: Boolean): Logger?
    fun findAll(active: Boolean? = null): Collection<Logger>
    fun save(logger: Logger)
}