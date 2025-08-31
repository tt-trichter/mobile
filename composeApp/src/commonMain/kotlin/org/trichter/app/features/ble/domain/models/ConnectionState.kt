package org.trichter.app.features.ble.domain.models


sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Connecting : ConnectionState()
    data object Connected : ConnectionState()
    data class Failed(val reason: String) : ConnectionState()
}
