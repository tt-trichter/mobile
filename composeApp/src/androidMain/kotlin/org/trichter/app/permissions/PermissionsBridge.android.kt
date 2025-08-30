package org.trichter.app.permissions

actual interface PermissionsBridgeListener {
    actual fun requestBluetoothPermission(callback: PermissionResultCallback)
    actual fun isBluetoothPermissionGranted(): Boolean
}