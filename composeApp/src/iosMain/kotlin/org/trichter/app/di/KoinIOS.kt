package org.trichter.app.di

import org.koin.core.context.startKoin

fun initKoinIOS() {
    startKoin {
        modules(
            networkModule,
            dataModule,
            presentationModule
        )
    }
}
