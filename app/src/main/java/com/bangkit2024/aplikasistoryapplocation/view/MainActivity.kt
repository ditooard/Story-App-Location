package com.bangkit2024.aplikasistoryapplocation.view

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit2024.aplikasistoryapplocation.R
import com.bangkit2024.aplikasistoryapplocation.adapter.LoadingStateAdapter
import com.bangkit2024.aplikasistoryapplocation.adapter.StoryAdapter
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityMainBinding
import com.bangkit2024.aplikasistoryapplocation.viewmodel.CustomViewModelFactory
import com.bangkit2024.aplikasistoryapplocation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel by viewModels<MainViewModel> {
        CustomViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupRecyclerView()
        observeToken()
        setupListeners()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun observeToken() {
        lifecycleScope.launch {
            viewModel.fetchToken().collect { token ->
                if (token.isEmpty()) {
                    navigateToLanding()
                } else {
                    showProgressBar()
                    observeStories(token)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, ActivityTambahStory::class.java))
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }

                R.id.action_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.action_maps -> {
                    startActivity(Intent(this@MainActivity, ActivityMaps::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToLanding() {
        startActivity(Intent(this@MainActivity, ActivityLanding::class.java))
        finish()
    }

    private fun showProgressBar() {
        binding.progressBar.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate().alpha(1f).setDuration(500).start()
        }
    }

    private fun observeStories(token: String) {
        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        viewModel.fetchStories(token).observe(this@MainActivity) {
            binding.progressBar.visibility = View.GONE
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun logout() {
        val confirmationDialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Logout")
            .setMessage("Wanna Log Out?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.clearSession()
                val intent = Intent(this@MainActivity, ActivityLanding::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(ActivityLanding.EXTRA_LOGOUT, true)
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .create()

        confirmationDialog.show()
    }


    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.GONE
    }
}
