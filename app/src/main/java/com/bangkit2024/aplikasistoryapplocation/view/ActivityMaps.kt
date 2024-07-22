package com.bangkit2024.aplikasistoryapplocation.view

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit2024.aplikasistoryapplocation.R
import com.bangkit2024.aplikasistoryapplocation.database.response.ListStoryItem
import com.bangkit2024.aplikasistoryapplocation.databinding.ActivityMapsBinding
import com.bangkit2024.aplikasistoryapplocation.viewmodel.CustomViewModelFactory
import com.bangkit2024.aplikasistoryapplocation.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class ActivityMaps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private var listStory: List<ListStoryItem> = listOf()
    private val viewModel by viewModels<MapsViewModel> {
        CustomViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMapUI()
        setMapStyle()
        fetchStoriesWithLocation()
    }

    private fun setupMapUI() {
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
    }

    private fun fetchStoriesWithLocation() {
        lifecycleScope.launch {
            viewModel.fetchToken().collect { token ->
                viewModel.getStoriesWithLocation(token).observe(this@ActivityMaps) { result ->
                    result.onSuccess { storyResponse ->
                        listStory = storyResponse.listStory
                        if (listStory.isNotEmpty()) {
                            addManyMarker()
                        } else {
                            Log.e(TAG, "stories with location not available.")
                        }
                    }.onFailure { exception ->
                        Log.e(TAG, "Error : ${exception.message}")
                    }
                }
            }
        }
    }

    private fun addManyMarker() {
        listStory.forEach { data ->
            data.lat?.let { lat ->
                data.lon?.let { lon ->
                    val latLng = LatLng(lat, lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(data.name)
                            .snippet(data.description)
                    )
                    boundsBuilder.include(latLng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Not Found: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
