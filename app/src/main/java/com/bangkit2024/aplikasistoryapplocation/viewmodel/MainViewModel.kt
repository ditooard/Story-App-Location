package com.bangkit2024.aplikasistoryapplocation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun fetchStories(token: String): LiveData<PagingData<ListStoryItem>> =
        userRepository.fetchStories(token).cachedIn(viewModelScope)

    fun fetchToken(): Flow<String> {
        return userRepository.fetchToken()
    }

    fun clearSession() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
