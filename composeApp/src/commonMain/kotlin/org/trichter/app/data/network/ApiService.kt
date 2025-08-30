package org.trichter.app.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.trichter.app.data.model.Run

class ApiService(private val httpClient: HttpClient) {
    private val baseUrl = "https://trichter.hauptspeicher.com/api/v1"

    suspend fun getRuns(): List<Run> {
        return httpClient.get("$baseUrl/runs").body()
    }


//    suspend fun createPost(post: Post): Post {
//        return httpClient.post("$baseUrl/posts") {
//            setBody(post)
//        }.body()
//    }
}
