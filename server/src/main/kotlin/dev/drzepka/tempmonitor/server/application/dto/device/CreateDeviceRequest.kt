package dev.drzepka.tempmonitor.server.application.dto.device

class CreateDeviceRequest : UpdateDeviceRequest() {
    var mac: String? = null
}