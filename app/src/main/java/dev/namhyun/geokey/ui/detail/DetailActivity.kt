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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.ui.addkey.AddKeyActivity
import dev.namhyun.geokey.util.latLng
import kotlinx.android.synthetic.main.activity_detail.*

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(R.layout.activity_detail), OnMapReadyCallback {
    private val viewModel by viewModels<DetailViewModel>()

    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

        (supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment)
            .getMapAsync(this)

        viewModel.keyData.observe(this, Observer {
            val value = it.value

            tv_name.text = value.name
            tv_key.text = value.key
            tv_location.text = value.address

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
                if (viewModel.keyData.value != null) {
                    val keyDocument = viewModel.keyData.value!!
                    buildDeleteDialog(keyDocument.value.name) {
                        viewModel.deleteKey(keyDocument.id)
                        onBackPressed()
                    }.show()
                }
                true
            }
            R.id.action_edit -> {
                if (viewModel.keyData.value != null) {
                    val keyDocument = viewModel.keyData.value!!
                    AddKeyActivity.openActivity(this, keyDocument.id, keyDocument.value)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        map.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        map.locationOverlay.isVisible = true
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

    companion object {
        const val EXTRA_KEY_ID = "key_id"
    }
}
