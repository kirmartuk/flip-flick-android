package com.martyuk.flipflick

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.martyuk.flipflick.entities.BluetoothDeviceWithConnectionStatus
import java.nio.charset.Charset
import kotlin.collections.ArrayList

/**
 * Simple Bluetooth repository for android on kotlin
 *
 * @author  Kirill Martyuk
 * @version 1.0
 */
class BluetoothRepository(private val context: Context) {

    companion object {

        const val UPDATE_HOSTS = "com.martyuk.flipflick.BluetoothRepository.UPDATE_HOSTS"

        /**
         * Get battery lebel of Bluetooth device
         * @return Int - battery level from 0 to 100 if can't get battery level then return -1
         */
        fun BluetoothDevice.getBatteryLevel() =
            this.let { bluetoothDevice ->
                (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                    .invoke(this) as Int
            }
    }

    /**
     * Get current connected bluetooth devices
     * @param liveData with ArrayList of BluetoothDevice
     * @return update lifeData after receiving data from ServiceListener
     */
    fun getConnectedBluetoothDevices(liveData: MutableLiveData<ArrayList<BluetoothDeviceWithConnectionStatus>>) {
        var list: ArrayList<BluetoothDevice>
        val serviceListener: BluetoothProfile.ServiceListener = object :
            BluetoothProfile.ServiceListener {
            override fun onServiceDisconnected(profile: Int) {
                TODO("Not yet implemented")
            }

            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                list = proxy?.connectedDevices as ArrayList<BluetoothDevice>
                liveData.postValue(
                    ArrayList(
                        liveData.value!!.map {
                            BluetoothDeviceWithConnectionStatus(
                                it.bluetoothDevice in list,
                                it.bluetoothDevice
                            )
                        }
                    ).sortDevicesByStatus()
                )
                Log.e("connected", list.toString())
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
            }
        }

        BluetoothAdapter.getDefaultAdapter()
            .getProfileProxy(
                context,
                serviceListener, BluetoothProfile.STATE_CONNECTED
            )
    }

    /**
     * Connect to bluetooth device where BluetoothProfile == HEADSET && A2DP
     * @param device - BluetoothDevice
     * @return void
     */
    fun connect(device: BluetoothDevice) {
        val serviceListener: BluetoothProfile.ServiceListener = object :
            BluetoothProfile.ServiceListener {
            override fun onServiceDisconnected(profile: Int) {}

            @SuppressLint("DiscouragedPrivateApi")
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                val connect = proxy.javaClass.getDeclaredMethod(
                    "connect",
                    BluetoothDevice::class.java
                )
                connect.isAccessible = true
                connect.invoke(proxy, device)
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
            }
        }
        BluetoothAdapter.getDefaultAdapter().apply {
            getProfileProxy(context, serviceListener, BluetoothProfile.HEADSET)
            getProfileProxy(context, serviceListener, BluetoothProfile.A2DP)
        }
    }

    /**
     * Disconnect connected bluetooth device where BluetoothProfile == HEADSET && A2DP
     * @param device - BluetoothDevice
     * @return void
     */
    fun disconnect(device: BluetoothDevice) {
        val serviceListener: BluetoothProfile.ServiceListener = object :
            BluetoothProfile.ServiceListener {
            override fun onServiceDisconnected(profile: Int) {}

            @SuppressLint("DiscouragedPrivateApi")
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                val disconnectMethod = proxy.javaClass.getDeclaredMethod(
                    "disconnect",
                    BluetoothDevice::class.java
                )
                disconnectMethod.isAccessible = true
                disconnectMethod.invoke(proxy, device)
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)
            }
        }
        BluetoothAdapter.getDefaultAdapter().apply {
            getProfileProxy(context, serviceListener, BluetoothProfile.HEADSET)
            getProfileProxy(context, serviceListener, BluetoothProfile.A2DP)
        }

    }

    /**
     * Reconnect bluetooth device to the host
     * @param bluetoothDevice - BluetoothDevice
     * @param socket - Bluetooth socket of connected host
     * @return void
     */
    fun reconnectBluetoothDevice(bluetoothDevice: BluetoothDevice, socket: BluetoothSocket) {
        disconnect(bluetoothDevice)
        socket.outputStream?.write(
            """{"command": "connect", "device": "${bluetoothDevice.address}"}""".toByteArray(
                Charset.defaultCharset()
            )
        )
    }

    private fun List<BluetoothDeviceWithConnectionStatus>.sortDevicesByStatus() =
        ArrayList<BluetoothDeviceWithConnectionStatus>(this.sortedByDescending { device -> device.status })

}