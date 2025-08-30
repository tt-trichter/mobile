package org.trichter.app.features.runs.di

import io.ktor.client.HttpClient
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
import org.trichter.app.features.runs.data.network.ApiService
import org.trichter.app.features.runs.data.network.ApiServiceImpl

// in theory could be shared, as viewmodel is same everywhere
// just wanted to try out
fun runsModule() = listOf(runsSharedModule, runsPlatformModule)

expect val runsPlatformModule: Module

val runsSharedModule = module {
    singleOf(::RunsRepositoryImpl).bind<RunsRepository>()
    singleOf(::ApiServiceImpl).bind<ApiService>()

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