package com.bangkit2024.aplikasistoryapplocation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit2024.aplikasistoryapplocation.api.ApiService
import com.bangkit2024.aplikasistoryapplocation.database.StoryDatabase
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem
import com.bangkit2024.aplikasistoryapplocation.database.response.LoginResponse
import com.bangkit2024.aplikasistoryapplocation.database.response.RegisterResponse
import com.bangkit2024.aplikasistoryapplocation.database.response.StoryResponse
import com.bangkit2024.aplikasistoryapplocation.database.response.UploadResponse
import com.bangkit2024.aplikasistoryapplocation.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val network: ApiService,
    private val pref: UserPreference,
    private val db: StoryDatabase
) {
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> =
        liveData {
            emit(handleHttpException { network.register(name, email, password) })
        }

    suspend fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(handleHttpException { network.login(email, password) })
        }

    fun fetchStories(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(db, network, "Bearer $token"),
            pagingSourceFactory = { db.storyDao().fetchStories() }
        ).liveData
    }

    suspend fun addStories(
        token: String,
        file: MultipartBody.Part,
        desc: RequestBody,
        lon: RequestBody?,
        lat: RequestBody?
    ): LiveData<Result<UploadResponse>> =
        liveData {
            emit(handleHttpException {
                network.uploadImage(
                    "Bearer $token",
                    file,
                    desc,
                    lon,
                    lat
                )
            })
        }

    suspend fun fetchStoriesLocation(token: String): LiveData<Result<StoryResponse>> =
        liveData {
            emit(handleHttpException { network.getStoriesWithLocation("Bearer $token") })
        }

    suspend fun saveToken(token: String) {
        pref.saveToken(token)
    }

    fun fetchToken(): Flow<String> = pref.getToken()

    suspend fun logout() {
        pref.logout()
    }

    private suspend fun <T> handleHttpException(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Result.failure(Throwable(errorBody.message))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun fetchInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }
}
