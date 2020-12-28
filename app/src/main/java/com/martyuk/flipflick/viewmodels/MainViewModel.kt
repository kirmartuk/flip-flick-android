package com.martyuk.flipflick.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.martyuk.flipflick.BluetoothRepository
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    val allDevices: MutableLiveData<ArrayList<BluetoothDeviceWithConnectionStatus>> =
        MutableLiveData()

    init {
        loadAllDevices()
    }

    fun loadConnectedDevices() {
        viewModelScope.launch(Dispatchers.Default) {
            BluetoothRepository(app).getConnectedBluetoothDevices(allDevices)
        }
    }

    fun loadAllDevices() {
        viewModelScope.launch(Dispatchers.Default) {
            val devices = BluetoothAdapter.getDefaultAdapter().bondedDevices
            Log.e(
                "devices",
                ArrayList(devices.map { bluetoothDevice -> bluetoothDevice.name }).toString()
            )
            allDevices.postValue(ArrayList(devices.map {
                BluetoothDeviceWithConnectionStatus(false, it)
            }))
            loadConnectedDevices()
        }
    }
}