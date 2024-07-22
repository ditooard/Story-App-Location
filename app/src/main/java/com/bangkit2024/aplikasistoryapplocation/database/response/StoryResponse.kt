package com.bangkit2024.aplikasistoryapplocation.database.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "story")
@Parcelize
data class ListStoryItem(

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("directions")
    val directions: String,

    @field:SerializedName("ingredients")
    val ingredients: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String,

    @field:SerializedName("imageId")
    val imageId: String,

    @field:SerializedName("idUser")
    val idUser: String,

) : Parcelable
