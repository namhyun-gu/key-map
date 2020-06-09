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
package dev.namhyun.geokey.ui.adddata

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.LocationData
import kotlinx.android.synthetic.main.activity_add_data.*

@AndroidEntryPoint
class AddDataActivity : AppCompatActivity(R.layout.activity_add_data) {
    val viewModel by viewModels<AddDataViewModel>()
    var location: LocationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.onSavedData.observe(this, Observer {
            fab.isEnabled = true
            if (it) onBackPressed()
        })

        viewModel.toastData.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        fab.setOnClickListener {
            fab.isEnabled = false
            val name = edit_name.editText!!.text.toString()
            val key = edit_key.editText!!.text.toString()
            viewModel.writeKey(location!!, name, key)
        }
    }

    override fun onStart() {
        super.onStart()
        if (intent.hasExtra(EXTRA_LOCATION_DATA)) {
            location = intent.getParcelableExtra(EXTRA_LOCATION_DATA)
            tv_current_location.text = location!!.address
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_LOCATION_DATA = "location_data"
    }
}
