package com.dimaster.bluetoothkotlintest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//This is MainActivity in example code
class BluetoothListKotlin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_list_kotlin)

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, DeviceListFragment()).commit()
    }
}