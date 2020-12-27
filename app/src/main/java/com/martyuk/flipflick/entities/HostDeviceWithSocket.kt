package com.martyuk.flipflick.entities

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

data class HostDeviceWithSocket(
    val bluetoothDevice: BluetoothDevice,
    var bluetoothSocket: BluetoothSocket?
)