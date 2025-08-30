package org.trichter.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.trichter.app.di.appModules
import org.trichter.app.features.ble.di.bleModules
import org.trichter.app.features.ble.presentation.BleScreen
import org.trichter.app.features.ble.presentation.BleViewModel
import org.trichter.app.features.runs.di.runsModule
import org.trichter.app.features.runs.presentation.RunsScreen
import org.trichter.app.features.runs.presentation.RunsViewModel
import org.trichter.app.navigation.Routes

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).crossfade(true).build()
    }

    KoinMultiplatformApplication(config = KoinConfiguration { modules(appModules()) }) {
        MaterialTheme {
            AppScreen()
        }
    }
}

@Composable
fun AppScreen(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.RUNS.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Routes.RUNS.route) {
                val viewModel: RunsViewModel = koinViewModel()
                RunsScreen(viewModel = viewModel)
            }
            composable(route = Routes.BLE.route) {
                val viewModel: BleViewModel = koinViewModel()
                BleScreen(viewModel = viewModel)

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currenRoute = currentBackStackEntry?.destination?.route

    val routes = listOf(Routes.RUNS, Routes.BLE)
    NavigationBar {
        routes.forEach { item ->
            val isSelected = currenRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(text = item.title)
                },
                        selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
