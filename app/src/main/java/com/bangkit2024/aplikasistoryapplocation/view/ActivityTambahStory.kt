package com.bangkit2024.aplikasistoryapplocation.view

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bangkit2024.aplikasistoryapplocation.R
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityTambahStoryBinding
import com.bangkit2024.aplikasistoryapplocation.util.getImageUri
import com.bangkit2024.aplikasistoryapplocation.util.reduceFileImage
import com.bangkit2024.aplikasistoryapplocation.util.uriToFile
import com.bangkit2024.aplikasistoryapplocation.viewmodel.AddStoryViewModel
import com.bangkit2024.aplikasistoryapplocation.viewmodel.CustomViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ActivityTambahStory : AppCompatActivity() {
    private lateinit var binding: ActivityTambahStoryBinding
    private val viewModel by viewModels<AddStoryViewModel> {
        CustomViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private var tempUri: Uri? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        binding.apply {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadImage() }
            cbAddLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLocation()
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        tempUri = getImageUri(this)
        launcherIntentCamera.launch(tempUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri = tempUri
            showImage()
        } else {
            showToast("Camera action canceled")
        }
    }

    private fun setupListeners() {
        binding.apply {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            uploadButton.setOnClickListener { uploadImage() }
            cbAddLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLocation()
                }
            }
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)?.reduceFileImage()
            if (imageFile != null) {
                Log.d("Image File", "uploadImage: ${imageFile.path}")

                binding.progressBar.visibility = View.VISIBLE

                val requestBody =
                    binding.descEditText.text.toString().toRequestBody("text/plain".toMediaType())
                val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())
                val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                lifecycleScope.launch {
                    viewModel.fetchToken().collect { token ->
                        if (token.isNotEmpty()) {
                            viewModel.submitStory(
                                token,
                                multipartBody,
                                requestBody,
                                lonRequestBody,
                                latRequestBody
                            ).observe(this@ActivityTambahStory) { result ->
                                binding.progressBar.visibility = View.GONE
                                result.onSuccess {
                                    Toast.makeText(
                                        this@ActivityTambahStory,
                                        getString(R.string.upload_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@ActivityTambahStory, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                result.onFailure { error ->
                                    Toast.makeText(
                                        this@ActivityTambahStory,
                                        "Error: ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            } else {
                showToast(getString(R.string.empty_image_warning))
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        lat = it.latitude
                        lon = it.longitude
                    }
                }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
