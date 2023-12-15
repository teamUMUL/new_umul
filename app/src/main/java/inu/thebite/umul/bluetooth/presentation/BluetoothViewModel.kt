package inu.thebite.umul.bluetooth.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
): ViewModel() {

    private val _isServiceConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isServiceConnected = _isServiceConnected.asStateFlow()

    fun updateServiceConnection(connected: Boolean) {
        _isServiceConnected.value = connected
    }
}