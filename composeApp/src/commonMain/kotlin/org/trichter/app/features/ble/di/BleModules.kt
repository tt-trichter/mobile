package org.trichter.app.features.ble.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.trichter.app.features.ble.data.KableBleRepository
import org.trichter.app.features.ble.data.MokoPermissionsGateway
import org.trichter.app.features.ble.data.network.ApiService
import org.trichter.app.features.ble.data.network.ApiServiceImpl
import org.trichter.app.features.ble.domain.BleRepository
import org.trichter.app.features.ble.domain.PermissionsGateway
import org.trichter.app.features.ble.domain.usecases.ConnectToDevice
import org.trichter.app.features.ble.domain.usecases.DisconnectFromDevice
import org.trichter.app.features.ble.domain.usecases.ObservePermissionsState
import org.trichter.app.features.ble.domain.usecases.ObserveScanResults
import org.trichter.app.features.ble.domain.usecases.ObserveTrichterState
import org.trichter.app.features.ble.domain.usecases.OpenAppSettings
import org.trichter.app.features.ble.domain.usecases.RequestBluetoothPermissions
import org.trichter.app.features.ble.domain.usecases.SaveRun
import org.trichter.app.features.ble.domain.usecases.SendAck
import org.trichter.app.features.ble.domain.usecases.StartScan
import org.trichter.app.features.ble.domain.usecases.StopScan
import org.trichter.app.features.ble.presentation.BleViewModel

fun bleModules() = listOf(blePresentationModule, bleDataModule, bleDomainModule)

val blePresentationModule = module {
    viewModelOf(::BleViewModel)
}

val bleDataModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    singleOf(::MokoPermissionsGateway).bind<PermissionsGateway>()
    singleOf(::KableBleRepository).bind<BleRepository>()
    singleOf(::ApiServiceImpl).bind<ApiService>()
}

val bleDomainModule = module {
    factoryOf(::ObservePermissionsState)
    factoryOf(::ObserveScanResults)
    factoryOf(::RequestBluetoothPermissions)
    factoryOf(::OpenAppSettings)
    factoryOf(::StartScan)
    factoryOf(::StopScan)
    factoryOf(::ConnectToDevice)
    factoryOf(::DisconnectFromDevice)
    factoryOf(::ObserveTrichterState)
    factoryOf(::SendAck)
    factoryOf(::SaveRun)
}