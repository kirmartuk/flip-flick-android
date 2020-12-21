package com.martyuk.flipflick

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.martyuk.flipflick.BluetoothRepository.Companion.getBatteryLevel
import kotlinx.android.synthetic.main.activity_bluetooth_device.*

class BluetoothDeviceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_device)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothDevice =
            bluetoothAdapter.getRemoteDevice(intent.getStringExtra("macAddress"))

        tv_bluetooth_device_name.text = bluetoothDevice.name
        pgb_bluetooth_device_battery.progress = bluetoothDevice.getBatteryLevel()

    }
}