package dev.drzepka.tempmonitor.logger

import dev.drzepka.tempmonitor.logger.bluetooth.BluetoothFacade
import dev.drzepka.tempmonitor.logger.bluetooth.BroadcastListener
import dev.drzepka.tempmonitor.logger.bluetooth.MockBluetoothFacade
import dev.drzepka.tempmonitor.logger.bluetooth.bluetoothctl.BluetoothCtlBluetoothFacade
import dev.drzepka.tempmonitor.logger.model.BluetoothServiceData
import dev.drzepka.tempmonitor.logger.util.ExceptionTracker
import dev.drzepka.tempmonitor.logger.util.Logger
import dev.drzepka.tempmonitor.logger.util.StopWatch
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LoggerOrchestrator(private val manager: LoggerManager) : BroadcastListener {
    private val log by Logger()
    private val executorService = Executors.newScheduledThreadPool(2)

    private var bluetoothFacade: BluetoothFacade? = null

    private var deviceRefreshExceptionTracker = ExceptionTracker()
    private var measurementUploadExceptionTracker = ExceptionTracker()

    fun start() {
        log.info("Starting logger orchestrator")
        initializeDevices()


        log.info("Launching bluetooth interface")
        createBluetoothFacade()
        bluetoothFacade!!.addBroadcastListener(this)
        bluetoothFacade!!.startListening()

        log.info("Scheduling device refresh at interval {}", DEVICE_REFRESH_INTERVAL)
        executorService.scheduleAtFixedRate(
            { executeDeviceRefresh() },
            0L,
            DEVICE_REFRESH_INTERVAL.seconds,
            TimeUnit.SECONDS
        )

        log.info("Scheduling measurement uploading at interval {}", MEASUREMENT_SEND_INTERVAL)
        executorService.scheduleAtFixedRate(
            this::executeMeasurementUpload,
            0L,
            MEASUREMENT_SEND_INTERVAL.seconds,
            TimeUnit.SECONDS
        )

        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
    }

    fun stop() {
        log.info("Stopping logger orchestrator")
        bluetoothFacade?.stopListening()
        executorService.shutdown()
    }

    override fun onDataReceived(data: BluetoothServiceData) {
        manager.decodeBluetoothData(data)
    }

    private fun initializeDevices() {
        log.info("Initializing devices")

        var status: Boolean
        var trialNo = 1
        do {
            status = executeDeviceRefresh()
            if (!status) {
                log.info("Initializing unsuccessful, waiting {} before another trial", DEVICE_REFRESH_INTERVAL)
                Thread.sleep(DEVICE_REFRESH_INTERVAL.toMillis())
                trialNo++
            }
        } while (!status)

        log.info("Devices initialized after {} trials", trialNo)
    }

    private fun executeDeviceRefresh(): Boolean {
        return try {
            manager.refreshDevices()
            resetTracker(deviceRefreshExceptionTracker, "Refreshing devices")
            true
        } catch (e: Exception) {
            handleExceptionWithTracker(deviceRefreshExceptionTracker, e, "Error while refreshing devices")
            false
        }
    }

    private fun createBluetoothFacade() {
        val isTest = System.getProperty("TEST") != null
        bluetoothFacade = if (isTest) {
            log.info("Test is active, creating mock bluetooth facade")
            MockBluetoothFacade()
        } else {
            BluetoothCtlBluetoothFacade()
        }
    }

    private fun executeMeasurementUpload() {
        try {
            val timeLimit = MEASUREMENT_SEND_INTERVAL.minusSeconds(10L)
            val stopWatch = StopWatch(true)
            manager.sendMeasurementsToServer(timeLimit)

            stopWatch.stop()
            if (stopWatch.elapsed() > timeLimit)
                log.warn("Measurement upload took longer than time limit ({} > {})", stopWatch.elapsed(), timeLimit)

            resetTracker(measurementUploadExceptionTracker, "Uploading measurements")
        } catch (e: Exception) {
            handleExceptionWithTracker(measurementUploadExceptionTracker, e, "Error while uploading measurements")
        }
    }

    private fun resetTracker(tracker: ExceptionTracker, info: String) {
        if (tracker.exceptionCount > 0)
            log.info("{} was successful after {} exceptions", info, tracker.exceptionCount)

        tracker.reset()
    }

    private fun handleExceptionWithTracker(tracker: ExceptionTracker, exception: Exception, errorMessage: String) {
        if (tracker.exceptionCount < CONSECUTIVE_EXCEPTION_THRESHOLD)
            log.error("{}", errorMessage, exception)
        else
            log.error("{}: {}", errorMessage, exception.message)

        tracker.setLastException(exception)

        if (tracker.exceptionCount == CONSECUTIVE_EXCEPTION_THRESHOLD)
            log.error("Upload error threshold has been reachced, consecutive identical errors will be presented without a stacktrace")
    }

    companion object {
        private val DEVICE_REFRESH_INTERVAL = Duration.ofSeconds(60)
        private val MEASUREMENT_SEND_INTERVAL = Duration.ofSeconds(30)
        private const val CONSECUTIVE_EXCEPTION_THRESHOLD = 3
    }
}