package io.github.namhyungu.keymap.ui.home

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.skydoves.bundler.intentOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.databinding.ActivityHomeBinding
import io.github.namhyungu.keymap.ui.addeditkey.AddEditKeyActivity
import io.github.namhyungu.keymap.util.EventObserver
import io.github.namhyungu.keymap.util.await
import io.github.namhyungu.keymap.util.awaitMap
import io.github.namhyungu.keymap.util.checkSelfPermissions
import io.github.namhyungu.keymap.util.viewBindings


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private val binding: ActivityHomeBinding by viewBindings(ActivityHomeBinding::inflate)
    private val keyController: KeyController by lazy {
        KeyController {
            viewModel.startEditKey(it.id)
        }
    }
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val fusedLocationSource: FusedLocationSource by lazy {
        FusedLocationSource(this, RC_REQ_LOCATION_PERM)
    }

    private val markerList = mutableListOf<Marker>()
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.keyList.setController(keyController)
        binding.keyList.addItemDecoration(DividerItemDecoration(
            this, LinearLayoutManager.VERTICAL
        ))

        binding.fab.setOnClickListener {
            viewModel.startAddKey()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            naverMap = mapFragment.awaitMap().also { map ->
                map.uiSettings.apply {
                    isLocationButtonEnabled = true
                }
                map.locationOverlay.isVisible = true
                map.locationSource = fusedLocationSource
                map.locationTrackingMode = LocationTrackingMode.Follow
                map.addOnLocationChangeListener {
                    viewModel.updateLocation(it)
                }
            }
            viewModel.setIsMapReady()
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycle.coroutineScope.launchWhenStarted {
            /** [FusedLocationSource]가 최신 위치를 가져오는 데 오래 걸리기에 직접 요청 **/
            if (!checkSelfPermissions(listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            ) {
                return@launchWhenStarted
            }

            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                viewModel.updateLocation(location)
            }
        }

        viewModel.uiState.observe(this, {
            val (keyList, location, address, isMapReady, firstStart) = it
            keyController.setData(keyList, location)

            binding.address.text = address
            if (address.isNotEmpty()) {
                binding.addressShimmer.stopShimmer()
                binding.addressShimmer.visibility = View.GONE
            }

            if (isMapReady) {
                val map = checkNotNull(naverMap)
                val markerList = keyList.map { key ->
                    val keyLocation = key.place!!.location
                    val latLng = LatLng(keyLocation.latitude, keyLocation.longitude)
                    Marker(latLng)
                }

                if (location != null) {
                    val locationOverlay = map.locationOverlay
                    val latLng = LatLng(location.latitude, location.longitude)
                    locationOverlay.position = latLng

                    if (firstStart) {
                        val cameraUpdate = CameraUpdate.toCameraPosition(
                            CameraPosition(latLng, 17.0)
                        )
                        map.moveCamera(cameraUpdate)

                        viewModel.setFirstStart()
                    }
                }

                updateMarker(map, markerList)
            }
        })

        viewModel.startAddKeyEvent.observe(this, EventObserver {
            intentOf<AddEditKeyActivity> {
                putExtra("location" to it)
                startActivity(this@HomeActivity)
            }
        })

        viewModel.startEditKeyEvent.observe(this, EventObserver {
            intentOf<AddEditKeyActivity> {
                putExtra("keyId" to it)
                startActivity(this@HomeActivity)
            }
        })

        viewModel.fetchKeyList()
    }

    private fun updateMarker(map: NaverMap, newMarkerList: List<Marker>) {
        // Remove marker in map
        markerList.forEach { it.map = null }

        markerList.clear()
        markerList.addAll(newMarkerList)

        // Add marker in map
        markerList.forEach { it.map = map }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (fusedLocationSource.onRequestPermissionsResult(requestCode,
                                                           permissions,
                                                           grantResults)
        ) {
            if (!fusedLocationSource.isActivated) {
                Toast.makeText(this, "Require location permission", Toast.LENGTH_SHORT).show()
                finish()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val RC_REQ_LOCATION_PERM = 100
    }
}