package org.trichter.app.di

import org.koin.core.context.startKoin

fun initKoinWasm() {
    startKoin {
        modules(
            networkModule,
            dataModule,
            presentationModule
        )
    }
}
