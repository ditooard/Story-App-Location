package com.bangkit2024.aplikasistoryapplocation.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityLandingBinding

class ActivityLanding : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViews()
        setupActions()
        startAnimation()
        handleLogoutFlag()
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
        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, ActivityLogin::class.java))
        }

        binding.BtnSignUp.setOnClickListener {
            startActivity(Intent(this, ActivityRegister::class.java))
        }
    }

    private fun startAnimation() {

        val loginButtonAnim =
            ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(500)
        val signupButtonAnim =
            ObjectAnimator.ofFloat(binding.BtnSignUp, View.ALPHA, 1f).setDuration(500)
        val titleAnim = ObjectAnimator.ofFloat(binding.TVTitle, View.ALPHA, 1f).setDuration(500)
        val descAnim = ObjectAnimator.ofFloat(binding.TVdesc, View.ALPHA, 1f).setDuration(500)

        val buttonsAnimSet = AnimatorSet().apply {
            playTogether(loginButtonAnim, signupButtonAnim)
        }

        AnimatorSet().apply {
            playSequentially(titleAnim, descAnim, buttonsAnimSet)
            start()
        }
    }

    private fun handleLogoutFlag() {
        if (intent.getBooleanExtra(EXTRA_LOGOUT, false)) {
            finish()
        }
    }

    companion object {
        const val EXTRA_LOGOUT = "EXTRA_LOGOUT"
    }
}
