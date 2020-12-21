package com.martyuk.flipflick.entities

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceWithConnectionStatus(
    val status: Boolean,
    val bluetoothDevice: BluetoothDevice
)
