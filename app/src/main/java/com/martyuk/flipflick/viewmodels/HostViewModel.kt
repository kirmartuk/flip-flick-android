package com.martyuk.flipflick.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.martyuk.flipflick.BluetoothRepository
import com.martyuk.flipflick.db.AppDatabase
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus
import com.martyuk.flipflick.entities.HostDeviceEntity
import com.martyuk.flipflick.entities.HostDeviceWithSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class HostViewModel(private val app: Application) : AndroidViewModel(app) {
    val hostDevices: MutableLiveData<ArrayList<HostDeviceWithSocket>> =
        MutableLiveData()

    init {
        getHostDeviceEntities()
        getHostDeviceEntitiesWithStatus()
    }

    fun loadConnectedDevices() {
        hostDevices.value?.forEach {
            it.bluetoothSocket?.outputStream?.write(
                """{"command": "get_connected_devices", "device": ""}""".toByteArray(
                    Charset.defaultCharset()
                )
            )
            val buffer = ByteArray(1024)
            val bytes: Int
            bytes = it.bluetoothSocket?.inputStream?.read(buffer)!!
            val readMessage = String(buffer, 0, bytes)
            Log.d("asd", "Message :: $readMessage")
        }
    }


    fun updateHostDevice(bluetoothDevice: BluetoothDevice) {
        hostDevices.value?.forEach { hostDeviceWithSocket ->
            if (hostDeviceWithSocket.bluetoothDevice == bluetoothDevice) {
                hostDeviceWithSocket.bluetoothSocket = null
            }
        }
        update()
    }

    private fun update() {
        hostDevices.postValue(hostDevices.value)
    }

    fun getHostDeviceEntities() {
        viewModelScope.launch(Dispatchers.Default) {
            val db = AppDatabase.getAppDataBase(context = app)
            val dao = db?.hostDeviceEntityDao()
            val hosts = dao?.getHostDeviceEntities()!!
            hostDevices.postValue(ArrayList(hosts.map {
                HostDeviceWithSocket(
                    BluetoothAdapter.getDefaultAdapter().getRemoteDevice(it.macAddress),
                    null
                )
            }))
        }
    }

    fun getHostDeviceEntitiesWithStatus() {
        viewModelScope.launch(Dispatchers.Default) {
            val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
            hostDevices.value?.forEach {
                Log.e("hoststst", it.toString())
                try {
                    if (it.bluetoothSocket != null) {
                        if (!it.bluetoothSocket!!.isConnected) {
                            it.bluetoothSocket =
                                it.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID)
                            it.bluetoothSocket?.connect()
                        }
                    } else {
                        it.bluetoothSocket =
                            it.bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID)
                        if (!it.bluetoothSocket!!.isConnected) {
                            it.bluetoothSocket?.connect()
                        }
                    }
                } catch (ioException: Exception) {
                    it.bluetoothSocket = null
                    Log.e("ioexception", ioException.toString())
                }
            }
            update()
        }
    }

    fun saveHostDeviceEntities(hostDeviceEntities: ArrayList<HostDeviceEntity>) {
        viewModelScope.launch(Dispatchers.Default) {
            val db = AppDatabase.getAppDataBase(context = app)
            val dao = db?.hostDeviceEntityDao()
            dao?.insertHostDeviceEntities(hostDeviceEntities)
        }
    }
}