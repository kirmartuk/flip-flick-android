package com.martyuk.flipflick

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.martyuk.flipflick.BluetoothRepository.Companion.getBatteryLevel
import com.martyuk.flipflick.adapters.HostRecyclerViewAdapter
import com.martyuk.flipflick.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_bluetooth_device.*

class BluetoothDeviceActivity() : BottomSheetDialogFragment() {
    private val mainViewModel: MainViewModel by viewModels()


    companion object {
        var macAddress: String? = null
        fun newInstance(macAddress: String): BluetoothDeviceActivity {
            this.macAddress = macAddress
            return BluetoothDeviceActivity()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bluetooth_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothDevice =
            bluetoothAdapter.getRemoteDevice(macAddress)

        tv_bluetooth_device_name.text = bluetoothDevice.name
        pgb_bluetooth_device_battery.progress = bluetoothDevice.getBatteryLevel()
        tv_bluetooth_device_battery.text = "${bluetoothDevice.getBatteryLevel()}%"

        mainViewModel.hostDevices.observe(this, {
            rv_host_devices.apply {
                layoutManager = LinearLayoutManager(context)
                adapter =
                    HostRecyclerViewAdapter(it)
            }
        })


    }

}
