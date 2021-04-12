package dev.drzepka.tempmonitor.server.domain.service

import dev.drzepka.tempmonitor.server.AbstractDatabaseTest
import dev.drzepka.tempmonitor.server.domain.dto.CreateDeviceRequest
import dev.drzepka.tempmonitor.server.domain.entity.Device
import dev.drzepka.tempmonitor.server.domain.entity.table.DevicesTable
import dev.drzepka.tempmonitor.server.domain.exception.ValidationException
import org.assertj.core.api.BDDAssertions.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

internal class DeviceServiceTest : AbstractDatabaseTest() {

    override val tables = arrayOf(DevicesTable)

    @Test
    fun `should create device`() {
        val request = CreateDeviceRequest().apply {
            name = "name"
            description = "description"
        }

        val dto = transaction {
            getService().createDevice(request)
        }

        then(dto.name).isEqualTo(request.name)
        then(dto.description).isEqualTo(request.description)

        transaction {
            val entity = Device.findById(dto.id)!!
            then(entity.name).isEqualTo(request.name)
            then(entity.description).isEqualTo(request.description)
            then(entity.active).isTrue()
        }
    }

    @Test
    fun `should validate request when creating device`() {
        val emptyRequest = CreateDeviceRequest()

        assertThatExceptionOfType(ValidationException::class.java).isThrownBy {
            getService().createDevice(emptyRequest)
        }
    }

    @Test
    fun `should get device`() {
        val request = CreateDeviceRequest().apply {
            name = "name"
            description = "description"
        }

        val dto = transaction {
            getService().createDevice(request)
        }

        transaction {
            assertThatCode {
                getService().getDevice(dto.id)
            }.doesNotThrowAnyException()
        }
    }

    @Test
    fun `should delete device`() {
        val request = CreateDeviceRequest().apply {
            name = "name"
            description = "description"
        }

        transaction {
            getService().apply {
                val created = createDevice(request)
                deleteDevice(created.id)
            }

            val found = Device.all().firstOrNull()
            then(found).isNotNull
            then(found?.active).isFalse()
        }
    }

    private fun getService(): DeviceService = DeviceService()
}