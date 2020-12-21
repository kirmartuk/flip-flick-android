package com.martyuk.flipflick

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus

class BluetoothRepository(private val app: Application) {

    fun getConnectedBluetoothDevices(liveData: MutableLiveData<ArrayList<BluetoothDevice>>) {
        var list: ArrayList<BluetoothDevice>
        val serviceListener: BluetoothProfile.ServiceListener = object :
            BluetoothProfile.ServiceListener {
            override fun onServiceDisconnected(profile: Int) {
                TODO("Not yet implemented")
            }

            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                list = proxy?.connectedDevices as ArrayList<BluetoothDevice>
                liveData.postValue(list)
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)

            }

        }
        BluetoothAdapter.getDefaultAdapter()
            .getProfileProxy(
                app,
                serviceListener, BluetoothProfile.STATE_CONNECTED
            )
    }

    companion object {
        fun BluetoothDevice.getBatteryLevel() =
            this.let { bluetoothDevice ->
                (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                    .invoke(this) as Int
            }
    }

    private fun List<BluetoothDeviceWithConnectionStatus>.sortDevicesByStatus() =
        this.sortedByDescending { device -> device.status }


    fun mergeAllAndConnectedDevices(
        allDevices: ArrayList<BluetoothDevice>,
        connectedDevices: ArrayList<BluetoothDevice>
    ): ArrayList<BluetoothDeviceWithConnectionStatus> {

        return ArrayList(allDevices.map { bluetoothDevice ->
            BluetoothDeviceWithConnectionStatus(
                bluetoothDevice in connectedDevices,
                bluetoothDevice
            )
        }.sortDevicesByStatus())
    }
}