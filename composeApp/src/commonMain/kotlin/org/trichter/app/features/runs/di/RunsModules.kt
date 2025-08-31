package org.trichter.app.features.runs.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.trichter.app.features.runs.data.repository.RunsRepository
import org.trichter.app.features.runs.data.repository.RunsRepositoryImpl
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.trichter.app.features.runs.data.network.ApiService
import org.trichter.app.features.runs.data.network.ApiServiceImpl
import org.trichter.app.features.runs.presentation.RunsViewModel

fun runsModule() = listOf(runsSharedModule)

val runsSharedModule = module {
    singleOf(::RunsRepositoryImpl).bind<RunsRepository>()
    singleOf(::ApiServiceImpl).bind<ApiService>()
    viewModelOf(::RunsViewModel)

    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = false
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 10_000
            }

            install(Logging) {
                level = LogLevel.INFO
                logger = Logger.DEFAULT
            }
        }
    }
}