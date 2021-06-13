package dev.drzepka.tempmonitor.server.domain.entity

import java.time.Instant

class Logger : Entity<Int>() {
    var name = ""
    var description = ""
    var password = ""
    var createdAt: Instant = Instant.now()
    var active = true
}