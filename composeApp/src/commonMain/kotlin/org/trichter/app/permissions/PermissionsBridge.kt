package org.trichter.app.permissions

expect interface PermissionsBridgeListener {
    fun requestBluetoothPermission(callback: PermissionResultCallback)
    fun isBluetoothPermissionGranted(): Boolean
}

class PermissionBridge {
    private var listener: PermissionsBridgeListener? = null

    fun setListener(listener: PermissionsBridgeListener) {
        this.listener = listener
    }

    fun requestBluetoothPermission(callback: PermissionResultCallback) {
        listener?.requestBluetoothPermission(callback)
            ?: error("PermissionsBridgeListener not set")
    }

    // NOTE: was isContactPermissionGranted() — rename to what it actually does:
    fun isBluetoothPermissionGranted(): Boolean {
        return listener?.isBluetoothPermissionGranted() ?: false
    }
}

interface PermissionResultCallback {
    fun onPermissionGranted()
    fun onPermissionDenied(isPermanentDenied: Boolean)
}
