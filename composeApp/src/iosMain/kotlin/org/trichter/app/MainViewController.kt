package org.trichter.app

import androidx.compose.ui.window.ComposeUIViewController
import org.trichter.app.di.initKoin

fun MainViewController() = ComposeUIViewController (
    configure = {
    }
){
    App()
}