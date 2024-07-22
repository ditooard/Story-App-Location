package com.bangkit2024.aplikasistoryapplocation.di

import android.content.Context
import com.bangkit2024.aplikasistoryapplocation.api.ApiConfig
import com.bangkit2024.aplikasistoryapplocation.data.UserPreference
import com.bangkit2024.aplikasistoryapplocation.data.UserRepository
import com.bangkit2024.aplikasistoryapplocation.data.dataStore
import com.bangkit2024.aplikasistoryapplocation.database.StoryDatabase

object Injection {
    fun provideRepository(context: Context): UserRepository {

        val pref = UserPreference.getInstance(context.dataStore)
        val network = ApiConfig.getApiService()
        val db = StoryDatabase.getInstance(context)


        return UserRepository.fetchInstance(network, pref, db)
    }
}
