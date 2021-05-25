package dev.drzepka.tempmonitor.server.domain.entity

import java.time.Instant

class Device : Entity<Int>() {
    var name = ""
    var description = ""
    var createdAt: Instant = Instant.now()
    var active = false
}