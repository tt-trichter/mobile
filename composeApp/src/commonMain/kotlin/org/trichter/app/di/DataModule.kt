package org.trichter.app.di

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.trichter.app.data.network.ApiService
import org.trichter.app.data.repository.RunRepository
import org.trichter.app.presentation.posts.RunsViewModel

val networkModule = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = false
                })
            }

            install(Logging) {
                level = LogLevel.INFO
                logger = Logger.DEFAULT
            }
        }
    }
}

val dataModule = module {
    single { ApiService(get()) }
    single { RunRepository(get()) }
}

val presentationModule = module {
    viewModelOf(::RunsViewModel)
}
