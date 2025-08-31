package org.trichter.app.features.ble.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import org.trichter.app.features.ble.domain.models.NewRunDto
import org.trichter.app.features.ble.domain.models.UserDto
import org.trichter.app.features.runs.data.model.Run
import org.trichter.app.util.Log

private const val DEV_AUTH_HEADER = "Basic dHJpY2h0ZXI6c3VwZXItc2FmZS1wYXNzd29yZA==";

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    private val baseUrl = "https://trichter.hauptspeicher.com/api/v1"

    override suspend fun createRun(newRun: NewRunDto): Result<Unit> = runCatching {
        val response = httpClient.post("$baseUrl/runs") {
            header(HttpHeaders.Authorization, DEV_AUTH_HEADER)
            contentType(ContentType.Application.Json)
            setBody(newRun)
        }

        if(!response.status.isSuccess()) {
            val errBody = response.bodyAsText()
            Log.e("HTTP", "HTTP ${response.status.value}: $errBody")
            throw IllegalStateException("HTTP ${response.status.value}: $errBody")
        }
    }

    override suspend fun searchUsers(name: String): Result<List<UserDto>> = runCatching {
        val response = httpClient.get("$baseUrl/users/search") {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            parameter("name", name)
        }
        if (!response.status.isSuccess()) {
            val err = response.bodyAsText()
            Log.e("HTTP", "HTTP ${response.status.value}: $err")
            error("HTTP ${response.status.value}: $err")
        }
        response.body<List<UserDto>>()

    }
}
