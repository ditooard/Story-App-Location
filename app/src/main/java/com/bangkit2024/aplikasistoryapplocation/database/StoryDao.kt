package com.bangkit2024.aplikasistoryapplocation.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem

@Dao
interface StoryDao {
    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun fetchStories(): PagingSource<Int, ListStoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}