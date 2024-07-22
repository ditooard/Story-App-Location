package com.bangkit2024.aplikasistoryapplocation.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun fetchToken(): Flow<String> {
        return repository.fetchToken()
    }

    suspend fun submitStory(
        authToken: String,
        image: MultipartBody.Part,
        description: RequestBody,
        longitude: RequestBody? = null,
        latitude: RequestBody? = null
    ) = repository.addStories(authToken, image, description, longitude, latitude)
}
