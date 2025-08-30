package org.trichter.app.features.ble.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.trichter.app.features.ble.presentation.BleViewModel

fun bleModules() = listOf(bleSharedModules)

val bleSharedModules = module {
    viewModelOf(::BleViewModel)
}