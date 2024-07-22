package com.bangkit2024.aplikasistoryapplocation.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    suspend fun login(email: String, password: String) =
        userRepository.login(email, password)

    suspend fun saveToken(token: String) {
        withContext(Dispatchers.IO) {
            userRepository.saveToken(token)
        }
    }
}
