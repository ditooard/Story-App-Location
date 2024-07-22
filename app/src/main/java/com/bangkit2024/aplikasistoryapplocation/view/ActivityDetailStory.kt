package com.bangkit2024.aplikasistoryapplocation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityDetailStoryBinding
import com.bumptech.glide.Glide

class ActivityDetailStory : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
        story?.let { setStory(it) }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setStory(listStoryItem: ListStoryItem) {
        with(binding) {
            tvNameStory.text = listStoryItem.name
            tvDescStory.text = listStoryItem.description
            Glide.with(this@ActivityDetailStory)
                .load(listStoryItem.photoUrl)
                .into(ivImage)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
