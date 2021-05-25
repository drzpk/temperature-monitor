package dev.drzepka.tempmonitor.server.application.dto.measurement

import dev.drzepka.tempmonitor.server.application.TimeRangeRequest

class GetMeasurementsRequest : TimeRangeRequest() {
    var deviceId = 0
}