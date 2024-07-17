package com.dimaster.bluetoothkotlintest

import android.bluetooth.BluetoothDevice

data class ListItem(
    val device: BluetoothDevice,
    val isChecked: Boolean
)