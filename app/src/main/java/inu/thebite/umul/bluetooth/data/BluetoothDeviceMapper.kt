package inu.thebite.umul.bluetooth.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import inu.thebite.umul.bluetooth.domain.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}