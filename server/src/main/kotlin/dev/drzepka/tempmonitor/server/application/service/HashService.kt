package dev.drzepka.tempmonitor.server.application.service

interface HashService {
    fun createHash(password: String): String
    fun compareHashes(existingHash: String, password: String): Boolean
}