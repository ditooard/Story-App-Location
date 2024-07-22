package com.bangkit2024.aplikasistoryapplocation.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bangkit2024.aplikasistoryapplocation.api.ApiService
import com.bangkit2024.aplikasistoryapplocation.database.StoryDatabase
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem
import com.bangkit2024.aplikasistoryapplocation.database.response.LoginResponse
import com.bangkit2024.aplikasistoryapplocation.database.response.RegisterResponse
import com.bangkit2024.aplikasistoryapplocation.database.response.StoryResponse
import com.bangkit2024.aplikasistoryapplocation.database.response.UploadResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private val mockApi: ApiService = FakeApiService()
    private val token = "Bearer token"

    private val mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(mockDb, mockApi, token)
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }


    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(token: String, page: Int, size: Int): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt $i",
                "name $i",
                "description $i",
                i.toString(),
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        val start = (page - 1) * size
        val end = minOf(start + size, items.size)
        val paginatedItems = items.subList(start, end)
        return StoryResponse(
            listStory = paginatedItems,
            error = false,
            message = "Stories fetched successfully"
        )
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lon: RequestBody?,
        lat: RequestBody?
    ): UploadResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStoriesWithLocation(token: String, location: Int): StoryResponse {
        TODO("Not yet implemented")
    }
}
