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

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.ui.adapter.KeyAdapter
import dev.namhyun.geokey.ui.adddata.AddDataActivity
import dev.namhyun.geokey.ui.adddata.AddDataActivity.Companion.EXTRA_LOCATION_DATA
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main), OnMapReadyCallback {
    val viewModel by viewModels<MainViewModel>()
    val mapMarkers: MutableList<Marker> = mutableListOf()

    lateinit var keyAdapter: KeyAdapter
    lateinit var naverMap: NaverMap

    val requestLocation = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (isGranted) {
            viewModel.locationData.observe(this, Observer {
                tv_current_location.text = it.address
                keyAdapter.setLocation(it)
                naverMap.locationOverlay.position = LatLng(it.lat, it.lon)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(it.lat, it.lon)))
            })
        } else {
            Toast.makeText(this, "Required location permission", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        viewModel.keyData.observe(this, Observer {
            keyAdapter.addKeys(it)
        })

        viewModel.toastData.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.networkData.observe(this, Observer {
            layout_not_connected.visibility = if (it) View.GONE else View.VISIBLE
        })

        viewModel.markerData.observe(this, Observer {
            updateMarker(it)
        })
    }

    override fun onStart() {
        super.onStart()
        requestLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        fab.setOnClickListener { view ->
            val location = viewModel.locationData.value
            if (location != null) {
                val intent = Intent(this, AddDataActivity::class.java)
                intent.putExtra(EXTRA_LOCATION_DATA, location)
                startActivity(intent)
            }
        }
        keyAdapter = KeyAdapter() { id, item ->
            buildDeleteDialog(item.name) {
                viewModel.deleteKey(id)
            }.show()
        }
        list_keys.layoutManager = LinearLayoutManager(this)
        list_keys.adapter = keyAdapter
        list_keys.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun buildDeleteDialog(name: String, onDelete: () -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(getString(R.string.dialog_delete_title, name))
            setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                onDelete()
            }
            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }
        return builder.create()
    }

    private fun updateMarker(keys: Map<LatLng, List<Key>>) {
        if (mapMarkers.isNotEmpty()) {
            mapMarkers.forEach { it.map = null }
            mapMarkers.clear()
        }

        keys.forEach { (latLng, keys) ->
            val marker = Marker().apply {
                position = latLng
                map = naverMap
                icon = OverlayImage.fromResource(R.drawable.ic_place)
            }
            marker.setOnClickListener {
                true
            }
            mapMarkers.add(marker)
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        map.locationOverlay.isVisible = true
        map.addOnCameraIdleListener {
            viewModel.updateMarker(naverMap.contentRegion)
        }
    }
}
