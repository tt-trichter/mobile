package org.trichter.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.trichter.app.features.ble.di.bleModules
import org.trichter.app.features.runs.di.runsModule
import org.trichter.app.permissions.PermissionBridge
import org.trichter.app.service.createPreferencesDataStore

fun appModules() = listOf(permissionsModule, serviceModule) + bleModules() + runsModule()

val permissionsModule = module {
    singleOf(::PermissionBridge)
}

val serviceModule = module  {
    single<DataStore<Preferences>> { createPreferencesDataStore() }
}