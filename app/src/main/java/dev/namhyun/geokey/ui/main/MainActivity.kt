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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.model.Resource
import dev.namhyun.geokey.ui.adapter.KeyAdapter
import dev.namhyun.geokey.ui.addkey.AddKeyActivity
import dev.namhyun.geokey.ui.detail.DetailActivity
import dev.namhyun.geokey.ui.detail.DetailActivity.Companion.EXTRA_KEY_ID
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), OnMapReadyCallback {
    private val viewModel by viewModels<MainViewModel>()
    private val mapMarkers: MutableList<Marker> = mutableListOf()

    private lateinit var keyAdapter: KeyAdapter
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        viewModel.keyData.observe(this, Observer {
            if (it is Resource.Success) {
                keyAdapter.addKeys(it.data)
            } else {
                Toast.makeText(this, R.string.msg_fetch_keys_error, Toast.LENGTH_SHORT).show()
            }
        })

//        viewModel.networkStateData.observe(this, Observer {
//            layout_not_connected.visibility =
//                if (it is NetworkState.Available) View.GONE else View.VISIBLE
//        })

        viewModel.markerData.observe(this, Observer {
            updateMarker(it)
        })

        viewModel.locationData.observe(this, Observer {
            updateAddress(it.address)
            updateAdapter(it)
            updateMapCamera(it)
        })
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.apply {
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
            locationOverlay.isVisible = true
            addOnCameraIdleListener {
                viewModel.updateMarker(naverMap.contentRegion)
            }
        }
    }

    private fun initViews() {
        fab.setOnClickListener {
            val location = viewModel.locationData.value
            if (location != null) {
                AddKeyActivity.openActivity(this, location)
            }
        }
        keyAdapter = KeyAdapter() {
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

    private fun updateAdapter(location: LocationData) {
        keyAdapter.setLocation(location)
    }

    private fun updateMapCamera(location: LocationData) {
        val latLng = LatLng(location.lat, location.lon)
        naverMap.locationOverlay.position = latLng
        naverMap.moveCamera(
            CameraUpdate.toCameraPosition(
                CameraPosition(latLng, 17.0)
            )
        )
    }

    // TODO 화면 회전 시 naverMap 객체가 초기화 되어 이용할 수 없음.
    private fun updateMarker(keys: Map<LatLng, List<Document<Key>>>) {
        if (mapMarkers.isNotEmpty()) {
            mapMarkers.forEach { it.map = null }
            mapMarkers.clear()
        }

        keys.forEach { (latLng, keys) ->
            val marker = createMarker(latLng, keys)
            mapMarkers.add(marker)
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
