@file:OptIn(ExperimentalMaterial3Api::class)

package org.trichter.app.features.ble.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.trichter.app.features.ble.domain.models.Connection
import org.trichter.app.features.ble.domain.models.ResultMeta
import org.trichter.app.features.ble.domain.models.SessionStatus
import org.trichter.app.features.ble.domain.models.TrichterState
import org.trichter.app.features.ble.domain.models.UserDto
import org.trichter.app.features.ble.presentation.SearchUserState
import kotlin.math.pow
import kotlin.math.round

@Composable
fun BleConnectedScreen(
    trichterState: TrichterState,
    searchUserState: SearchUserState,
    onQueryChange: (String) -> Unit,
    onUserClick: (UserDto) -> Unit,
    onReconnect: () -> Unit,
    onDisconnect: () -> Unit,
    onAck: () -> Unit,
    onReset: () -> Unit,
    onSaveRun: (ResultMeta) -> Unit,
    onSaveImage: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trichter") },
                actions = {
                    when (trichterState.connection) {
                        Connection.Connected -> {
                            IconButton(onClick = onDisconnect) {
                                Icon(Icons.Outlined.Close, contentDescription = "Disconnect")
                            }
                        }

                        Connection.Disconnected -> {
                            IconButton(onClick = onReconnect) {
                                Icon(Icons.Outlined.Refresh, contentDescription = "Reconnect")
                            }
                        }

                        Connection.Connecting -> {
                            // no action while connecting
                        }
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ConnectionChip(trichterState.connection)
                StatusChip(trichterState.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = onAck,
                    enabled = trichterState.connection == Connection.Connected
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Acknowledge")
                }
                FilledTonalButton(
                    onClick = onReset,
                    enabled = trichterState.connection == Connection.Connected
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reset")
                }
            }

            ResultCard(
                meta = trichterState.lastResultMeta, onSaveRun = onSaveRun,
                searchUserState = searchUserState,
                onQueryChange = onQueryChange,
                onUserClick = onUserClick,
            )

            ImageCard(
                imageBytes = trichterState.lastImage,
                onSaveImage = onSaveImage
            )
        }
    }
}

@Composable
private fun ConnectionChip(connection: Connection, modifier: Modifier = Modifier) {
    val (label, color) = when (connection) {
        Connection.Connected -> "Connected" to MaterialTheme.colorScheme.primary
        Connection.Connecting -> "Connecting…" to MaterialTheme.colorScheme.tertiary
        Connection.Disconnected -> "Disconnected" to MaterialTheme.colorScheme.error
    }
    AssistChip(
        onClick = {},
        label = { Text(label) },
        enabled = false,
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = color.copy(alpha = 0.16f),
            disabledLabelColor = color
        )
    )
}

@Composable
private fun StatusChip(status: SessionStatus, modifier: Modifier = Modifier) {
    val (label, color) = when (status) {
        SessionStatus.IDLE -> "Idle" to MaterialTheme.colorScheme.outline
        SessionStatus.WAITING -> "Waiting" to MaterialTheme.colorScheme.tertiary
        SessionStatus.RUNNING -> "Running" to MaterialTheme.colorScheme.primary
        SessionStatus.COMPLETE -> "Complete" to MaterialTheme.colorScheme.secondary
        SessionStatus.ERROR -> "Error" to MaterialTheme.colorScheme.error
        SessionStatus.UNKNOWN -> "Unknown" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label) },
        enabled = false,
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = color.copy(alpha = 0.16f),
            disabledLabelColor = color
        )
    )
}

@Composable
private fun ResultCard(
    meta: ResultMeta?,
    onSaveRun: (ResultMeta) -> Unit,
    searchUserState: SearchUserState,
    onQueryChange: (String) -> Unit,
    onUserClick: (UserDto) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Latest Result",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (meta == null) {
                Text("No result yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Metric(
                        title = "Duration",
                        value = formatDuration(meta.durationMs),
                        supporting = "ms"
                    )
                    Metric(
                        title = "Rate",
                        value = meta.rateLpm.clean(2),
                        supporting = "L/min"
                    )
                    Metric(
                        title = "Volume",
                        value = meta.volumeL.clean(2),
                        supporting = "L"
                    )
                }
                val hint = if (meta.hasImage) {
                    "Image available • ${meta.imageSize} bytes"
                } else {
                    "No image"
                }
                Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UsersSearchScreen(
                        searchUserState = searchUserState,
                        onQueryChange = onQueryChange,
                        onUserClick = onUserClick,
                    )
                    Spacer(Modifier.size(15.dp))
                    FilledTonalButton(
                        onClick = { onSaveRun(meta) }
                    ) {
                        Text("Save Run")

                    }

                }
            }
        }
    }
}

@Composable
private fun Metric(
    title: String,
    value: String,
    supporting: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                supporting,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ImageCard(
    imageBytes: ByteArray?,
    onSaveImage: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Image",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (imageBytes == null || imageBytes.isEmpty()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Text("No image available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val bitmap: ImageBitmap? = remember(imageBytes) { decodeImage(imageBytes) }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Session image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                    )
                } else {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Face, contentDescription = null)
                        Text("${imageBytes.size} bytes (preview unsupported on this platform)")
                    }
                }

                OutlinedButton(
                    onClick = { onSaveImage(imageBytes) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun UsersSearchScreen(
    searchUserState: SearchUserState,
    onQueryChange: (String) -> Unit,
    onUserClick: (UserDto) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchUserState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search users") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Search, null) }
        )

        if (searchUserState.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
        }

        searchUserState.error?.let { err ->
            Text(
                err,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            searchUserState.results.forEach { user ->
                ElevatedCard(
                    onClick = { onUserClick(user) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            user.displayUsername ?: user.name ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val handle = user.username?.let { "@$it" }
                        if (!handle.isNullOrBlank()) {
                            Text(
                                handle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun Long.msToSeconds(): String = (this / 1000.0).clean(2)

private fun formatDuration(ms: Long): String = "${ms.msToSeconds()} s"

private fun formatFloatCommon(value: Double, digits: Int): String {
    val multiplier = 10.0.pow(digits)
    val rounded = round(value * multiplier) / multiplier
    val parts = rounded.toString().split('.')
    return if (digits == 0) {
        parts[0]
    } else {
        val frac = parts.getOrNull(1)?.padEnd(digits, '0')?.take(digits) ?: "".padEnd(digits, '0')
        "${parts[0]}.$frac"
    }
}

fun Double.clean(digits: Int): String = formatFloatCommon(this, digits)
fun Float.clean(digits: Int): String = formatFloatCommon(this.toDouble(), digits)

expect fun decodeImage(bytes: ByteArray): ImageBitmap?

