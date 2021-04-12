package dev.drzepka.tempmonitor.server.domain.service

import org.jetbrains.exposed.sql.Database

interface DatabaseProviderService {
    val db: Database
}