package org.trichter.app.permissions

import org.koin.compose.koinInject
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "PermissionRequestProtocol")
actual interface PermissionsBridgeListener {
    actual fun requestBluetoothPermission(callback: PermissionResultCallback)
    actual fun isBluetoothPermissionGranted(): Boolean
}

//@Suppress("unused")
//fun registerPermissionHandler(listener: PermissionsBridgeListener){
//    koinInstance.koin.get<PermissionBridge>().setListener(listener)
//}