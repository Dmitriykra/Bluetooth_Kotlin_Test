package com.dimaster.bluetoothkotlintest.bluetooth

import android.bluetooth.BluetoothAdapter

class BluetoothController(val adapter: BluetoothAdapter) {
    private var connectThread: ConnectionThread? = null
    fun connect(mac: String,listener: Listener){
        if(adapter.isEnabled && mac.isNotEmpty()){
            val device = adapter.getRemoteDevice(mac)
            connectThread = ConnectionThread(device, listener)
            connectThread?.start()
        }
    }
    companion object{
        const val BLUETOOTH_CONNECTED = "bluetooth_connected"
        const val BLUETOOTH_NO_CONNECTED = "bluetooth_no_connected"
    }
    interface Listener{
        fun onReceive(message:  String)
    }
}