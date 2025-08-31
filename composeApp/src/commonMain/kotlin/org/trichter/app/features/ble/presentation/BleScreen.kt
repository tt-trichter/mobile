@file:OptIn(ExperimentalUuidApi::class)

package org.trichter.app.features.ble.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.permissions.PermissionState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.trichter.app.features.ble.domain.models.ConnectionState
import org.trichter.app.features.ble.domain.models.ResultMeta
import org.trichter.app.features.ble.presentation.views.BleConnectScreen
import org.trichter.app.features.ble.presentation.views.BleConnectedScreen
import org.trichter.app.features.ble.presentation.views.BlePermissionsScreen
import org.trichter.app.util.Log
import kotlin.uuid.ExperimentalUuidApi


@Composable
fun BleScreen(viewModel: BleViewModel) {
    val uiState by viewModel.state


    LaunchedEffect(uiState.permissionState) {
        if (uiState.permissionState == PermissionState.Granted) viewModel.startScan()
    }

    when {
        uiState.permissionState != PermissionState.Granted -> BlePermissionsScreen(
            permissionState = uiState.permissionState,
            onRequestPermissions = { viewModel.onRequestPermissions() },
            onOpenSettings = { viewModel.onOpenSettings() },
        )

        uiState.connectionState == ConnectionState.Disconnected -> BleConnectScreen(
            advertisements = uiState.advertisements.values.toList(),
            onConnectClick = { viewModel.connect(it) }
        )

        uiState.connectionState == ConnectionState.Connecting -> BleConnectingScreen()

        uiState.connectionState == ConnectionState.Connected  -> BleConnectedScreen(
            state = uiState.trichterState!!,
            onReconnect = { Log.d("ConnectScreen", "onReconnect") },
            onDisconnect = {  viewModel.disconnect()  },
            onAck = { viewModel.sendAck() },
            onReset = { Log.d("ConnectScreen", "onReset")},
            onSaveImage = { Log.d("ConnectScreen", "onSaveImage")},
            onSaveRun = { meta: ResultMeta -> viewModel.saveRun(meta)}
        )


        uiState.error != null -> ErrorView(uiState.error!!) { viewModel.startScan() }
    }
}


@Composable
private fun ErrorView(error: Throwable, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Something went wrong", style = MaterialTheme.typography.titleMedium)
        error.message?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRetry) { Text("Retry scan") }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BleConnectingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LoadingIndicator(
            modifier = Modifier
                .size(150.dp)
        )
        Text("Connecting", style = MaterialTheme.typography.bodyMediumEmphasized)
    }

}

@Preview
@Composable
fun ConnectingPreview() {
    BleConnectingScreen()
}
