package com.dimaster.bluetoothkotlintest

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.dimaster.bluetoothkotlintest.bluetooth.BluetoothController
import com.dimaster.bluetoothkotlintest.databinding.FragmentDeviceListBinding

@Suppress("DEPRECATION")
class DeviceListFragment : Fragment(), ItemAdapter.Listener, BluetoothController.Listener {
    private var preference: SharedPreferences? = null
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var discoveryAdapter: ItemAdapter
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var binding: FragmentDeviceListBinding
    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var bluetoothController:  BluetoothController
    private val MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN = 1


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = activity?.getSharedPreferences(BtConstKotlin.PREFERENCES, Context.MODE_PRIVATE)

        binding.kotlinBluetoothOn.setOnClickListener {
            btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
        binding.kotlinBluetoothSearch.setOnClickListener {
            try{
                if(btAdapter?.isEnabled == true){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Toast.makeText(context, "Пошук для версії "+Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show()
                        checkScanPermission()
                    } else {
                        Toast.makeText(context, "Пробую для древніх устройств", Toast.LENGTH_SHORT).show()
                        btAdapter?.startDiscovery()
                    }

                    binding.kotlinProgressBar.visibility = View.VISIBLE
                }
            }catch (e: SecurityException){

            }
        }

        intentFilters()
        checkPermissions()
        initRcView()
        registerBtLauncher()
        initBtAdapter()
        bluetoothState()
        //bluetoothController = BluetoothController(btAdapter)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun checkScanPermission(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.BLUETOOTH_SCAN)) {
                // Explain why the permission is needed
                Snackbar.make(requireView(), "The app needs Bluetooth scan permission to find nearby devices", Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        // Request the permission again
                        requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN)
                        btAdapter?.startDiscovery()
                    }
                    .show()
            } else {
                // Request the permission
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN)
                btAdapter?.startDiscovery()
            }
            return
        } else {
            // Permission already granted, start Bluetooth scan
            btAdapter?.startDiscovery()
        }
    }

    public fun GetMyBtData()
    {
        val mac = preference?.getString(BtConstKotlin.MAC, "")
        bluetoothController.connect(mac ?: "", this)
    }

    private fun initRcView() = with(binding)
    {
        //створюємо вертикальний список з необіхдним конеткстом
        rcViewPaired.layoutManager = LinearLayoutManager(requireContext())
        rcViewSearched.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(this@DeviceListFragment, false)
        discoveryAdapter = ItemAdapter(this@DeviceListFragment,true)
        rcViewPaired.adapter = itemAdapter
        rcViewSearched.adapter = discoveryAdapter
    }

    private fun getPairedDevices()
    {
        try {
            val list = ArrayList<ListItem>()
            //берем список устройст с помощью сет листа в котором не может біть повторений
            val deviceList = btAdapter?.bondedDevices as Set<BluetoothDevice>
            deviceList.forEach {
                list.add(
                    ListItem(
                        it,
                        preference?.getString(BtConstKotlin.MAC, "") == it.address
                    )
                )
            }
            itemAdapter.submitList(list)

        } catch (e: SecurityException) { }
    }

    //init Bt
    private fun initBtAdapter()
    {
        val btManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager.adapter
    }

    private fun bluetoothState()
    {
        //если блютуз включен
        if(btAdapter?.isEnabled == true) {
            getPairedDevices()
        }
    }

    private fun registerBtLauncher()
    {
        btLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode == Activity.RESULT_OK)
            {
                getPairedDevices()
                Snackbar.make(binding.root, "Блютуз підключено", Snackbar.LENGTH_LONG).show()
            }
            else
            {
                Snackbar.make(binding.root, "Блютуз відключено", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private  fun checkPermissions()
    {
        if(!checkBtPermission())
        {
            registerPermissionListener()
            launchBtPermission()
        }
    }

    private fun launchBtPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            pLauncher.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        } else {
            pLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }

    private fun registerPermissionListener()
    {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){

        }
    }

    private fun saveMac(mac: String)
    {
        val editor = preference?.edit()
        editor?.putString(BtConstKotlin.MAC, mac)
        editor?.apply()
    }

    override fun onClick(item: ListItem)
    {
        saveMac(item.device.address)
        //!!!! Добавил для теста
        Toast.makeText(context, "Test ", Toast.LENGTH_SHORT)
        GetMyBtData()
    }

    private val bReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val list = mutableSetOf<ListItem>()
                list.addAll(discoveryAdapter.currentList)

                if(device!=null) list.add(ListItem(device, false))

                discoveryAdapter.submitList(list.toList())

                try {

                }catch (e: SecurityException){

                }
            } else if (intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                getPairedDevices()
            } else if (intent?.action == BluetoothAdapter.ACTION_DISCOVERY_STARTED) {

            } else if (intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED){
                binding.kotlinProgressBar.visibility = View.GONE
            }
        }
    }
    private fun intentFilters(){
        val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
        val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        val f4 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        activity?.registerReceiver(bReceiver, f1)
        activity?.registerReceiver(bReceiver, f2)
        activity?.registerReceiver(bReceiver, f3)
        activity?.registerReceiver(bReceiver, f4)
    }

    override fun onReceive(message: String) {
        activity?.runOnUiThread{
            when(message){

                BluetoothController.BLUETOOTH_CONNECTED->{
                    Toast.makeText(context, "Bluetooth connected", Toast.LENGTH_SHORT)
                }
                BluetoothController.BLUETOOTH_NO_CONNECTED->{
                    Toast.makeText(context, "Bluetooth no connected", Toast.LENGTH_SHORT)
                }
                else -> {
                    Log.d("my_log", "message"+ message)

                }
            }
        }
    }
}