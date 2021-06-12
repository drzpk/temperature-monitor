package dev.drzepka.tempmonitor.server.domain.entity

abstract class Entity<T> {
    open var id: T? = null

    fun isStored(): Boolean = id != null
}