package com.bangkit2024.aplikasistoryapplocation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository
import com.bangkit2024.aplikasistoryapplocation.di.Injection

class CustomViewModelFactory private constructor(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> SignupViewModel(repository) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(
                repository
            ) as T

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: CustomViewModelFactory? = null

        fun getInstance(context: Context): CustomViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: CustomViewModelFactory(Injection.provideRepository(context)).also {
                    instance = it
                }
            }
    }
}
