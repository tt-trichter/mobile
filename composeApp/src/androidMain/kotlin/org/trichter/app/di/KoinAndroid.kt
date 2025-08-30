package org.trichter.app.di

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import android.content.Context

fun initKoinAndroid(context: Context) {
    startKoin {
        androidLogger()
        androidContext(context)
        modules(
            networkModule,
            dataModule,
            presentationModule
        )
    }
}
