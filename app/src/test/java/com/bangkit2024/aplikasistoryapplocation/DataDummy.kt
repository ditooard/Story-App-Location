package com.bangkit2024.aplikasistoryapplocation

import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem

object DataDummy {
    fun generateDummyQuoteResponse(): List<ListStoryItem> {
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
        return items
    }
}
