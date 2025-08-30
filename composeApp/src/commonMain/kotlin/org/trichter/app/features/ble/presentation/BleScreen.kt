package org.trichter.app.features.ble.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.intPreferencesKey
import com.juul.kable.ExperimentalApi
import com.juul.kable.PlatformAdvertisement

@OptIn(ExperimentalApi::class)
@Composable
fun BleScreen(
    viewModel: BleViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isGranted by viewModel.isBluetoothPermissionGranted.collectAsState()

    LaunchedEffect(isGranted) { if (isGranted) viewModel.tryReconnectToLast() }
    LaunchedEffect(uiState.isReconnecting) { if (!uiState.isReconnecting) viewModel.startScan() }
    DisposableEffect(Unit) { onDispose { viewModel.stopScan() } }

    when {
        uiState.lastHrPayload != null && uiState.heartRateBpm != null -> ConnectedView(
            uiState.heartRateBpm!!,
            uiState.lastHrPayload!!.decodeToString()

        )
        uiState.peripheral != null -> ErrorView(
            message = "Connecting to ${uiState.peripheral?.name}",
            onRetry = {}
        )
        !isGranted -> PermissionSection(
            error = uiState.error,
            onRequest = { viewModel.requestBluetoothPermissions() }
        )

        uiState.error != null -> ErrorView(
            message = uiState.error!!,
            onRetry = { viewModel.startScan() }
        )

        uiState.advertisements.isEmpty() -> ScanningView()

        else -> DevicesView(
            advertisements = uiState.advertisements.values.toList(),
            onAdClick = { viewModel.connect(it)}
        )
    }
}

@Composable
private fun ConnectedView(
    bpm: Int,
    lastHrPayload: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Last Heart Rate: $bpm", style = MaterialTheme.typography.titleMedium)
        Text(lastHrPayload, color = MaterialTheme.colorScheme.error)
    }
}
@Composable
private fun PermissionSection(
    error: String?,
    onRequest: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Bluetooth permission is required to scan for devices.")
        Button(onClick = onRequest) { Text("Grant permission") }
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Something went wrong", style = MaterialTheme.typography.titleMedium)
        Text(message, color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRetry) { Text("Retry scan") }
        }
    }
}

@Composable
private fun ScanningView() {
    Box(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Scanning…", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DevicesView(
    advertisements: List<PlatformAdvertisement>,
    onAdClick: (PlatformAdvertisement) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(advertisements, key = { it.rssi }) { ad ->
            val device = ad.toDeviceItem()
            ListItem(
                headlineContent = { Text(device.name ?: "Unnamed") },
                supportingContent = {
                    val rssi = device.rssi?.let { "$it dBm" } ?: "?"
                    val uuids = if (device.serviceUuids.isNotEmpty())
                        " • ${device.serviceUuids.take(2).joinToString()}" else ""
                    Text("RSSI: $rssi$uuids")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAdClick(ad) }
                    .padding(horizontal = 8.dp)
            )
            HorizontalDivider()
        }
    }
}

