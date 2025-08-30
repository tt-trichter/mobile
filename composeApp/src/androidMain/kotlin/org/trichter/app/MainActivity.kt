package org.trichter.app

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import org.koin.compose.koinInject
import org.trichter.app.permissions.PermissionBridge
import org.trichter.app.permissions.PermissionResultCallback
import org.trichter.app.permissions.PermissionsBridgeListener
import org.trichter.app.service.initPreferencesDataStore


class MainActivity : ComponentActivity(), PermissionsBridgeListener {
    private var bluetoothPermissionResultCallback: PermissionResultCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initPreferencesDataStore(applicationContext)


        setContent {
            App()
            val permissionBridge: PermissionBridge = koinInject()
            permissionBridge.setListener(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun requestBluetoothPermission(callback: PermissionResultCallback) {
        requestBluetoothPermissionsLauncher.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun isBluetoothPermissionGranted(): Boolean {
        Log.d("MainActivity", "isBluetoothPermissionGranted")
        return hasPermissions(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )
    }
    private val requestBluetoothPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

            val allGranted = results.all { it.value }
            if (allGranted) {
                bluetoothPermissionResultCallback?.onPermissionGranted()
            } else {
                val permanentlyDenied = results.any { (perm, granted) ->
                    !granted && !shouldShowRequestPermissionRationale(perm)
                }
                bluetoothPermissionResultCallback?.onPermissionDenied(permanentlyDenied)
            }
        }
}

fun Context.hasPermissions(vararg permissions: String): Boolean {
    return permissions.all { perm ->
        val isGranted = ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        Log.d("MainActivity", "hasPermissions: $perm isGranted: $isGranted")

        return isGranted
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}