package org.trichter.app.features.runs.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.trichter.app.features.runs.presentation.RunsViewModel

actual val runsPlatformModule = module {
    viewModelOf(::RunsViewModel)
}