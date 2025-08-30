package org.trichter.app.features.runs.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.trichter.app.features.runs.data.model.Run

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    private val baseUrl = "https://trichter.hauptspeicher.com/api/v1"

    override suspend fun getRuns(): List<Run> {
        return httpClient.get("$baseUrl/runs").body()
    }


//    suspend fun createPost(post: Post): Post {
//        return httpClient.post("$baseUrl/posts") {
//            setBody(post)
//        }.body()
//    }
}
