package io.github.namhyungu.keymap.ui.picklocation

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.skydoves.bundler.bundleNonNull
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.databinding.ActivityPickLocationBinding
import io.github.namhyungu.keymap.util.EventObserver
import io.github.namhyungu.keymap.util.Util
import io.github.namhyungu.keymap.util.awaitMap
import io.github.namhyungu.keymap.util.viewBindings

@AndroidEntryPoint
class PickLocationActivity : AppCompatActivity() {
    private val viewModel: PickLocationViewModel by viewModels()
    private val binding: ActivityPickLocationBinding by viewBindings(ActivityPickLocationBinding::inflate)

    private val location: Location by bundleNonNull("location")
    private var naverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            naverMap = mapFragment.awaitMap().also { map ->
                map.uiSettings.apply {
                    isLocationButtonEnabled = false
                    logoGravity = Gravity.TOP or Gravity.START
                    setLogoMargin(24, 24, 0, 0)
                }
                map.locationOverlay.isVisible = true
                map.setOnMapClickListener { _, latLng ->
                    val location = Location("").apply {
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                    }
                    viewModel.updateLocation(location)
                }
            }
            viewModel.setIsMapReady()
        }

        binding.fab.setOnClickListener {
            viewModel.finishPick()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.uiState.observe(this, {
            val (location, address, isMapReady, firstStart) = it

            if (address != null) {
                var addressStr = Util.getAddressString(address)
                val buildingName = Util.getBuildingName(address)
                if (buildingName.isNotEmpty()) {
                    addressStr += " ($buildingName)"
                }
                binding.address.text = addressStr
            }

            if (isMapReady && location != null) {
                val map = checkNotNull(naverMap)
                val latLng = LatLng(location.latitude, location.longitude)
                val locationOverlay = map.locationOverlay
                locationOverlay.position = latLng

                if (firstStart) {
                    val cameraUpdate = CameraUpdate.toCameraPosition(
                        CameraPosition(latLng, 17.0)
                    )
                    map.moveCamera(cameraUpdate)

                    viewModel.setFirstStart()
                }
            }
        })

        viewModel.finishPickEvent.observe(this, EventObserver {
            val intent = Intent()
            intent.putExtra("location", it)
            setResult(Activity.RESULT_OK, intent)
            finish()
        })

        viewModel.start(location)
    }
}