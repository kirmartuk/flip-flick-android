package com.martyuk.flipflick.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.martyuk.flipflick.BluetoothRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    val slaveDevices: MutableLiveData<ArrayList<BluetoothDevice>> = MutableLiveData()
    val connectedDevices: MutableLiveData<ArrayList<BluetoothDevice>> = MutableLiveData()

    init {
        loadSlaveDevices()
        loadConnectedDevices()
    }

    fun loadConnectedDevices() {
        viewModelScope.launch(Dispatchers.Default) {
            BluetoothRepository(app).getConnectedBluetoothDevices(connectedDevices)
        }
    }

    fun loadSlaveDevices() {
        viewModelScope.launch(Dispatchers.Default) {
            val devices = BluetoothAdapter.getDefaultAdapter().bondedDevices
            Log.e(
                "devices",
                ArrayList(devices.map { bluetoothDevice -> bluetoothDevice.name }).toString()
            )
            slaveDevices.postValue(ArrayList(devices))
        }
    }
}