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
package dev.namhyun.geokey.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.databinding.ActivityDetailBinding
import dev.namhyun.geokey.ui.addkey.AddKeyActivity
import dev.namhyun.geokey.util.latLng

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(R.layout.activity_detail), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailViewModel>()

    private lateinit var naverMap: NaverMap

    private var requireCameraUpdate: Boolean = false
    private var latLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        (supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment)
            .getMapAsync(this)

        viewModel.key.observe(this, {
            val value = it.value

            binding.tvName.text = value.name
            binding.tvKey.text = value.key
            binding.tvLocation.text = value.address

            updateMap(value.latLng)
        })
    }

    override fun onStart() {
        super.onStart()
        if (intent.hasExtra(EXTRA_KEY_ID)) {
            viewModel.readKey(intent.getStringExtra(EXTRA_KEY_ID)!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_delete -> {
                if (viewModel.key.value != null) {
                    val keyDocument = viewModel.key.value!!
                    buildDeleteDialog(keyDocument.value.name) {
                        viewModel.deleteKey(keyDocument.id)
                        onBackPressed()
                    }.show()
                }
                true
            }
            R.id.action_edit -> {
                if (viewModel.key.value != null) {
                    val keyDocument = viewModel.key.value!!
                    AddKeyActivity.openActivity(this, keyDocument.id, keyDocument.value)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map.apply {
            locationOverlay.isVisible = true
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        }
        if (requireCameraUpdate) {
            updateMap(latLng!!)
        }
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

    private fun updateMap(latLng: LatLng) {
        if (this::naverMap.isInitialized) {
            naverMap.locationOverlay.position = latLng
            naverMap.moveCamera(
                CameraUpdate.toCameraPosition(
                    CameraPosition(
                        latLng,
                        17.0
                    )
                )
            )
            requireCameraUpdate = false
        } else {
            this.latLng = latLng
            requireCameraUpdate = true
        }
    }

    companion object {
        const val EXTRA_KEY_ID = "key_id"

        fun openActivity(context: Context, keyId: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_ID, keyId)
            context.startActivity(intent)
        }
    }
}
