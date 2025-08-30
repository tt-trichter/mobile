package org.trichter.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.trichter.app.permissions.PermissionBridge

class TrichterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
