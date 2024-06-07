package com.darekbx.geotracker.externaldisplay

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Intent
import android.os.IBinder
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.ktx.suspend
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.UUID

class BluetoothService: Service() {

    enum class DeviceStatus {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
    }

    companion object {
        const val DEVICE_NAME = "NightMonitor"
        const val TAG = "NightMonitor"

        var IS_SERVICE_ACTIVE = false
        val SERVICE_UUID: UUID = UUID.fromString("89409171-FE10-40B7-80A3-398A8C219855")
        val WRITE_UUID:UUID = UUID.fromString("89409171-FE10-40A1-80A3-398A8C219855")

        const val DEVICE_STATUS_ACTION = "deviceStatusAction"
        const val DEVICE_STATUS = "deviceStatus"
    }

    private var clientManager: ClientManager? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        IS_SERVICE_ACTIVE = true
        scanForDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScanner()
        disableBleServices()
        IS_SERVICE_ACTIVE = false
    }

    private fun scanForDevices() {
        val scanner = BluetoothLeScannerCompat.getScanner()
        val settings: ScanSettings = ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(5000)
            .setUseHardwareBatchingIfSupported(true)
            .build()
        val filters: MutableList<ScanFilter> = ArrayList()
        filters.add(ScanFilter.Builder().setDeviceName(DEVICE_NAME).build())
        scanner.startScan(filters, settings, scanCallback)
    }

    private val scanCallback = object: ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            Log.v(TAG, "Scan batch result: ${results}")
            results
                .firstOrNull { result ->
                    Log.v(TAG, "Check device ${result.device.name} == $DEVICE_NAME")
                    result.device.name == DEVICE_NAME
                }
                ?.let {
                    addDevice(it.device)
                    stopScanner()
                }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.v(TAG, "Scan result: ${result.device}")
            addDevice(result.device)
            stopScanner()
        }
    }

    private fun stopScanner() {
        val scanner = BluetoothLeScannerCompat.getScanner()
        scanner.stopScan(scanCallback)
    }

    private fun disableBleServices() {
        clientManager?.disconnect()
        clientManager?.close()
    }

    private fun addDevice(device: BluetoothDevice) {
        Log.v(TAG, "Connect device: ${device}")
        clientManager = ClientManager()
        clientManager!!.connect(device).useAutoConnect(true).enqueue()
    }

    private suspend fun write(data: ByteArray) {
        clientManager?.writeData(data)
    }

    private inner class ClientManager: BleManager(this) {

        private var writeCharacteristic: BluetoothGattCharacteristic? = null

        override fun getGattCallback(): BleManagerGattCallback = GattCallback()

        private inner class GattCallback : BleManagerGattCallback() {

            override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
                val service = gatt.getService(SERVICE_UUID)
                writeCharacteristic = service?.getCharacteristic(WRITE_UUID)
                val writeCharacteristicProperties = writeCharacteristic?.properties ?: 0
                return writeCharacteristicProperties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0
            }

            override fun initialize() {
                Log.v(TAG, "Initialize connection")
                notifyStatus(DeviceStatus.CONNECTING)
                beginAtomicRequestQueue()
                    .done {
                        Log.v(TAG, "Notifications connected")
                        notifyStatus(DeviceStatus.CONNECTED)
                    }
                    .enqueue()
            }

            override fun onServicesInvalidated() {
                writeCharacteristic = null
            }
        }

        suspend fun writeData(request: ByteArray): Data {
            return writeCharacteristic(
                writeCharacteristic,
                request,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
                .split(FlagBasedPacketSplitter())
                .suspend()
        }
    }

    private fun notifyStatus(deviceStatus: DeviceStatus) {
        Log.v(TAG, "Notify device status: $deviceStatus")
        sendBroadcast(Intent(DEVICE_STATUS_ACTION).apply {
            when (deviceStatus) {
                DeviceStatus.DISCONNECTED -> putExtra(DEVICE_STATUS, false)
                else -> { /* do nothing */ }
            }
        })
    }
}
