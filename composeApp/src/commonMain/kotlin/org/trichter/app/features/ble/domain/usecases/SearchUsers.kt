package org.trichter.app.features.ble.domain.usecases

import org.trichter.app.features.ble.data.network.ApiService
import org.trichter.app.features.ble.domain.models.UserDto

class SearchUsers(private val api: ApiService) {
    suspend operator fun invoke(query: String): Result<List<UserDto>> =
        if (query.isBlank()) Result.success(emptyList())
        else api.searchUsers(query.trim())
}
