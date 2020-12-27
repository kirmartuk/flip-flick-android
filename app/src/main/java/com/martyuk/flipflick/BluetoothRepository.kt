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


    private fun List<BluetoothDeviceWithConnectionStatus>.sortDevicesByStatus() =
        this.sortedByDescending { device -> device.status }


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