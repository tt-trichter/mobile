package org.trichter.app.features.ble.presentation.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.permissions.PermissionState


@Composable
fun BlePermissionsScreen(
    permissionState: PermissionState,
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (permissionState) {
        PermissionState.NotDetermined, PermissionState.Denied ->
            PermissionNotGrantedView(onRequestPermissions, modifier)
        PermissionState.DeniedAlways ->
            PermissionPermanentlyDeniedView(onOpenSettings, modifier)
        else -> { error("invalid permission state") }
    }
}


@Composable
private fun PermissionNotGrantedView(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Bluetooth permissions are required!",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.size(10.dp))
        Button(onClick = onRequestPermissions) { Text("Grant Permissions") }
    }
}


@Composable
private fun PermissionPermanentlyDeniedView(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Bluetooth permissions were permanently denied!",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Allow using the settings",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.size(10.dp))
        Button(onClick = onOpenSettings) { Text("Open Settings") }
    }
}
