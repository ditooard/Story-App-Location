package com.bangkit2024.aplikasistoryapplocation.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityLoginBinding
import com.bangkit2024.aplikasistoryapplocation.viewmodel.CustomViewModelFactory
import com.bangkit2024.aplikasistoryapplocation.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        CustomViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        playAnimation()
        setupViews()
        setupActions()
    }

    private fun playAnimation() {
        val animViews = listOf(
            binding.TVtitle, binding.TVmessage, binding.TVemail,
            binding.ETemail, binding.TVpassword, binding.ETpassword, binding.Btnlogin
        )

        AnimatorSet().apply {
            playSequentially(*animViews.map {
                ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(400)
            }.toTypedArray())
            startDelay = 100
        }.start()
    }

    private fun setupViews() {
        hideStatusBar()
        supportActionBar?.hide()
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupActions() {
        binding.Btnlogin.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.ETpassword.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                viewModel.login(email, password).observe(this@ActivityLogin) { result ->
                    binding.progressBar.visibility = View.GONE
                    result.onSuccess { response ->
                        response.loginResult.token.let { token ->
                            lifecycleScope.launch {
                                viewModel.saveToken(token)
                                navigateToMainActivity()
                            }
                        }
                    }.onFailure { e ->
                        clearFields()
                        showToast(e.message)
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@ActivityLogin, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun clearFields() {
        binding.emailEditText.text = null
        binding.ETpassword.text = null
    }

    private fun showToast(message: String?) {
        Toast.makeText(this@ActivityLogin, message, Toast.LENGTH_SHORT).show()
    }
}
