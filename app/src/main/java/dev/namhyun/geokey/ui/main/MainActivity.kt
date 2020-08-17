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
package dev.namhyun.geokey.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.ui.adapter.KeyAdapter
import dev.namhyun.geokey.ui.addkey.AddKeyActivity
import dev.namhyun.geokey.ui.detail.DetailActivity
import dev.namhyun.geokey.ui.detail.DetailActivity.Companion.EXTRA_KEY_ID
import dev.namhyun.geokey.util.KeyUtil
import dev.namhyun.geokey.util.latLng
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), OnMapReadyCallback {

    private val viewModel by viewModels<MainViewModel>()
    private val mapMarkers: MutableList<Marker> = mutableListOf()

    private lateinit var keyAdapter: KeyAdapter
    private lateinit var naverMap: NaverMap

    private var requireCameraUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()

        viewModel.keys.observe(this, Observer {
            if (it != null) {
                keyAdapter.addKeys(it)
            }
        })

        viewModel.location.observe(this, Observer {
            if (it != null) {
                updateAddress(it.address)
                updateAdapter(it)
                updateMapCamera(it)
            }
        })
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map.apply {
            locationOverlay.isVisible = true

            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            addOnCameraIdleListener {
                updateMarker(naverMap.contentRegion)
            }
        }
        if (requireCameraUpdate) {
            updateMapCamera(viewModel.location.value!!)
        }
    }

    private fun initViews() {
        fab.setOnClickListener {
            val location = viewModel.location.value
            if (location != null) {
                AddKeyActivity.openActivity(this, location)
            }
        }
        keyAdapter = KeyAdapter {
            openDetailKey(it.id)
        }

        list_keys.layoutManager = LinearLayoutManager(this)
        list_keys.adapter = keyAdapter
        list_keys.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun updateAddress(address: String) {
        tv_current_location.text = address
    }

    private fun updateAdapter(location: LocationModel) {
        keyAdapter.setLocation(location)
    }

    private fun updateMapCamera(location: LocationModel) {
        if (this::naverMap.isInitialized) {
            val latLng = LatLng(location.lat, location.lon)
            naverMap.locationOverlay.position = latLng
            naverMap.moveCamera(
                CameraUpdate.toCameraPosition(
                    CameraPosition(latLng, 17.0)
                )
            )
            requireCameraUpdate = false
        } else {
            requireCameraUpdate = true
        }
    }

    private fun updateMarker(region: Array<LatLng>) {
        val bounds = LatLngBounds.Builder().apply {
            for (idx in 1..4) {
                include(region[idx])
            }
        }.build()

        val keys = viewModel.keys.value
        if (keys != null) {
            val keysInBounds = keys.filter { bounds.contains(it.value.latLng) }
            val nearKeys = KeyUtil.collectNearKeys(keysInBounds)

            if (mapMarkers.isNotEmpty()) {
                mapMarkers.forEach { it.map == null }
                mapMarkers.clear()
            }

            nearKeys.forEach { (latLng, keys) ->
                val marker = createMarker(latLng, keys)
                mapMarkers.add(marker)
            }
        }
    }

    private fun createMarker(latLng: LatLng, keys: List<Document<Key>>): Marker {
        return Marker().apply {
            position = latLng
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_filled_key)
            setOnClickListener {
                showMarkerSheet(keys)
                true
            }
        }
    }

    private fun openDetailKey(keyId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_KEY_ID, keyId)
        startActivity(intent)
    }

    private fun showMarkerSheet(keys: List<Document<Key>>) {
        MarkerDialogFragment(keys) {
            openDetailKey(it.id)
        }.show(supportFragmentManager, "markerSheet")
    }
}
