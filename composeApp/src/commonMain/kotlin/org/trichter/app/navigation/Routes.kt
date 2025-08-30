package org.trichter.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class Routes(val route: String, val title: String = route, val icon: ImageVector) {
    RUNS(route = "runs", title = "Runs", Icons.Default.Home),
    BLE(route = "ble", title = "BLE", Icons.Default.DateRange)
}