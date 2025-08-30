package org.trichter.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.trichter.app.data.model.Run
import org.trichter.app.data.network.ApiService

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class RunRepository(private val apiService: ApiService) {

    fun getPosts(): Flow<Result<List<Run>>> = flow {
        emit(Result.Loading)
        try {
            val posts = apiService.getRuns()
            emit(Result.Success(posts))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

//    fun getPost(id: Int): Flow<Result<Post>> = flow {
//        emit(Result.Loading)
//        try {
//            val post = apiService.getPost(id)
//            emit(Result.Success(post))
//        } catch (e: Exception) {
//            emit(Result.Error(e))
//        }
//    }
//
//    suspend fun createPost(post: Post): Result<Post> {
//        return try {
//            val createdPost = apiService.createPost(post)
//            Result.Success(createdPost)
//        } catch (e: Exception) {
//            Result.Error(e)
//        }
//    }
}
