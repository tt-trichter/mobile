package org.trichter.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

import trichter_app.composeapp.generated.resources.Res
import trichter_app.composeapp.generated.resources.compose_multiplatform
import org.trichter.app.di.dataModule
import org.trichter.app.di.networkModule
import org.trichter.app.di.presentationModule
import org.trichter.app.presentation.posts.RunsScreen
import org.trichter.app.presentation.posts.RunsViewModel

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            networkModule,
            dataModule,
            presentationModule
        )
    }) {
        MaterialTheme {
            AppContent()
        }
    }
}

@Composable
private fun AppContent() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

            val viewModel: RunsViewModel = koinViewModel()
            RunsScreen(viewModel = viewModel)
    }
}