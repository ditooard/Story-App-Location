package com.bangkit2024.aplikasistoryapplocation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository
import com.bangkit2024.aplikasistoryapplocation.database.response.StoryResponse
import kotlinx.coroutines.flow.Flow

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun getStoriesWithLocation(token: String): LiveData<Result<StoryResponse>> {
        return userRepository.fetchStoriesLocation(token)
    }

    fun fetchToken(): Flow<String> {
        return userRepository.fetchToken()
    }
}