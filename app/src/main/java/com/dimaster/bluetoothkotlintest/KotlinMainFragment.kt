package com.dimaster.bluetoothkotlintest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dimaster.bluetoothkotlintest.bluetooth.BluetoothController
import com.dimaster.bluetoothkotlintest.databinding.FragmentKotlinMainBinding

class KotlinMainFragment : Fragment(), BluetoothController.Listener {

    private lateinit var binding: FragmentKotlinMainBinding
    private lateinit var bluetoothController: BluetoothController
    private lateinit var btAdapter: BluetoothAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKotlinMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtAdapter()
        val pref = activity?.getSharedPreferences(
            BtConstKotlin.PREFERENCES, Context.MODE_PRIVATE)

        val mac = pref?.getString(BtConstKotlin.MAC, "");

        bluetoothController = BluetoothController(btAdapter)
        binding.kotlinConnetcFromFragment.setOnClickListener {
            bluetoothController.connect(mac ?: "", this)
        }
    }
    private fun initBtAdapter()
    {
        val btManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
    }

    override fun onReceive(message: String) {
        activity?.runOnUiThread {
            when(message){
                BluetoothController.BLUETOOTH_CONNECTED -> {

                }
                BluetoothController.BLUETOOTH_NO_CONNECTED ->{

                }
                else ->{
                    binding.GetData.text = message
                }
            }
        }
    }
}