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
import com.bangkit2024.aplikasistoryapplocation.R
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityRegisterBinding
import com.bangkit2024.aplikasistoryapplocation.viewmodel.CustomViewModelFactory
import com.bangkit2024.aplikasistoryapplocation.viewmodel.SignupViewModel
import kotlinx.coroutines.launch

class ActivityRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<SignupViewModel> {
        CustomViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        startAnimation()
        setupViews()
        setupActions()
    }

    private fun startAnimation() {
        val animViews = listOf(
            binding.TVtitle, binding.TVmessage, binding.TVname,
            binding.ETname, binding.TVemail, binding.ETemail,
            binding.TVpassword, binding.ETpassword, binding.BtnSignup
        )

        AnimatorSet().apply {
            playSequentially(*animViews.map {
                ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(400)
            }.toTypedArray())
            startDelay = 100
        }.start()
    }

    private fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupActions() {
        binding.BtnSignup.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.ETpassword.text.toString()

            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                viewModel.regist(name, email, password).observe(this@ActivityRegister) { result ->
                    binding.progressBar.visibility = View.GONE
                    result.onSuccess {
                        Toast.makeText(
                            this@ActivityRegister,
                            getString(R.string.register_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@ActivityRegister, ActivityLogin::class.java)
                        startActivity(intent)
                        finish()
                    }.onFailure { e ->
                        binding.nameEditText.text = null
                        binding.emailEditText.text = null
                        binding.ETpassword.text = null
                        Toast.makeText(this@ActivityRegister, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
