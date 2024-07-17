package com.dimaster.bluetoothkotlintest.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID

class ConnectionThread(device: BluetoothDevice, private val listener: BluetoothController.Listener) : Thread() {
    private val uuid = "00001101-0000-1000-8000-00805F9B34FB"

    private var mSocet: BluetoothSocket? = null
    init {
        try {
            mSocet = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        } catch (_: IOException){} catch (_: SecurityException){}
    }

    override fun run() {
        try {
            Log.d("my_log", "Connecting...")
            mSocet?.connect()
            listener.onReceive(BluetoothController.BLUETOOTH_CONNECTED)
            readMessage()
            Log.d("my_log", "Connected")
        } catch (_:IOException){
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
        } catch (_: SecurityException){}
    }

    fun readMessage()
    {
        var  buffer = ByteArray(256)
        while (true) {
            try {
                var length = mSocet?.inputStream?.read(buffer)
                val message = String(buffer, 0, length  ?: 0)
                listener.onReceive(message)
            } catch (_: IOException){
                break
            }
        }
    }

    public fun closeConnection(){
        try {
            mSocet?.close()
        } catch (_:IOException){}
    }
}