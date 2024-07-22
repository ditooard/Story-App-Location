package com.bangkit2024.aplikasistoryapplocation.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun regist(
        name: String,
        email: String,
        password: String
    ) = userRepository.register(name, email, password)
}