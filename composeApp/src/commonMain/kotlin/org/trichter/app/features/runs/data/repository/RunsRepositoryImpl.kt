package org.trichter.app.features.runs.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.trichter.app.features.runs.data.model.Run
import org.trichter.app.features.runs.data.network.ApiService


class RunsRepositoryImpl(private val apiService: ApiService) : RunsRepository {
    override fun getRuns(): Flow<Result<List<Run>>> = flow {
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
