package com.bangkit2024.aplikasistoryapplocation.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem
import com.bangkit2024.aplikasistoryapplocation.databinding.ItemRowStoryBinding
import com.bangkit2024.aplikasistoryapplocation.view.ActivityDetailStory
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    inner class MyViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                tvNameStory.text = story.name
                tvDescStory.text = story.description
                Glide.with(ivImage.context)
                    .load(story.photoUrl)
                    .into(ivImage)

                setOnClickListener(story)
            }
        }

        private fun setOnClickListener(story: ListStoryItem) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ActivityDetailStory::class.java).apply {
                    putExtra(ActivityDetailStory.EXTRA_STORY, story)
                }

                val optionsCompat: ActivityOptionsCompat = createOptionsCompat()
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

        private fun createOptionsCompat(): ActivityOptionsCompat {
            val imagePair = Pair<View, String>(binding.ivImage, "profile")
            val namePair = Pair<View, String>(binding.tvNameStory, "name")
            val descPair = Pair<View, String>(binding.tvDescStory, "description")

            return ActivityOptionsCompat.makeSceneTransitionAnimation(
                itemView.context as Activity,
                imagePair, namePair, descPair
            )
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
