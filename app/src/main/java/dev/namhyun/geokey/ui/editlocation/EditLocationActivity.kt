/*
 * Copyright 2020 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.namhyun.geokey.ui.editlocation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.databinding.ActivityEditLocationBinding
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.ui.addkey.AddKeyActivity

@AndroidEntryPoint
class EditLocationActivity : AppCompatActivity(R.layout.activity_edit_location),
    OnMapReadyCallback {
    private lateinit var binding: ActivityEditLocationBinding

    private val viewModel by viewModels<EditLocationViewModel>()

    private lateinit var naverMap: NaverMap

    private var locationModel: LocationModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.location.observe(this, {
            updateLocation(it)
        })
    }

    override fun onStart() {
        super.onStart()
        if (!intent.hasExtra(AddKeyActivity.EXTRA_LOCATION_DATA)) {
            throw IllegalAccessError("Require EXTRA_LOCATION_DATA extra")
        }
        locationModel = intent.getParcelableExtra(AddKeyActivity.EXTRA_LOCATION_DATA)
        (supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment)
            .getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_done -> {
                val intent = Intent()
                intent.putExtra(AddKeyActivity.EXTRA_LOCATION_DATA, locationModel!!)
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        map.locationOverlay.isVisible = true
        map.setOnMapClickListener { _, latLng ->
            viewModel.updateLocation(latLng.latitude, latLng.longitude)
        }

        binding.tvCurrentLocation.text = locationModel?.address!!
        updateMap(LatLng(locationModel?.lat!!, locationModel?.lon!!))
    }

    private fun updateLocation(locationModel: LocationModel) {
        this.locationModel = locationModel
        binding.tvCurrentLocation.text = locationModel.address
        naverMap.locationOverlay.position = LatLng(locationModel.lat, locationModel.lon)
    }

    private fun updateMap(latLng: LatLng) {
        naverMap.locationOverlay.position = latLng
        naverMap.moveCamera(
            CameraUpdate.toCameraPosition(
                CameraPosition(
                    latLng,
                    17.0
                )
            )
        )
    }
}
