package inu.thebite.umul.bluetooth.presentation

import inu.thebite.umul.bluetooth.domain.BluetoothDevice
import inu.thebite.umul.bluetooth.domain.BluetoothMessage


data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()
)
