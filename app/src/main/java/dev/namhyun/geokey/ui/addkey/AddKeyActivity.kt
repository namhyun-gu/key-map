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
package dev.namhyun.geokey.ui.addkey

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.ui.editlocation.EditLocationActivity
import kotlinx.android.synthetic.main.activity_add_key.*

@AndroidEntryPoint
class AddKeyActivity : AppCompatActivity(R.layout.activity_add_key) {
    private val viewModel by viewModels<AddKeyViewModel>()

    private var locationModel: LocationModel? = null
    private var keyId: String? = null

    private val openEditLocation = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                locationModel = result.data?.getParcelableExtra(EXTRA_LOCATION_DATA)
                updateLocation()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        edit_location.setEndIconOnClickListener {
            val intent = Intent(this, EditLocationActivity::class.java)
            intent.putExtra(EXTRA_LOCATION_DATA, locationModel!!)
            openEditLocation.launch(intent)
        }

        btn_add.setOnClickListener {
            val name = edit_name.editText!!.text.toString()
            val key = edit_key.editText!!.text.toString()

            viewModel.saveKey(keyId, name, key, locationModel!!)
        }

        btn_cancel.setOnClickListener { onBackPressed() }

        viewModel.formState.observe(this, Observer {
            edit_name.error = ""
            edit_key.error = ""

            when (it) {
                is AddKeyFormState.InvalidData -> {
                    if (it.invalidItem.contains("name")) {
                        edit_name.error = getString(R.string.msg_name_required)
                    }
                    if (it.invalidItem.contains("key")) {
                        edit_key.error = getString(R.string.msg_key_required)
                    }
                }
                AddKeyFormState.ValidData -> {
                    Toast.makeText(this, R.string.msg_key_saved, Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        })

        if (!intent.hasExtra(EXTRA_LOCATION_DATA)
            and !intent.hasExtra(EXTRA_KEY_ID)
            and !intent.hasExtra(EXTRA_KEY)
        ) {
            throw IllegalAccessError("Require EXTRA_LOCATION_DATA or EXTRA_KEY_ID and EXTRA_KEY extra")
        }
        if (intent.hasExtra(EXTRA_LOCATION_DATA)) {
            locationModel = intent.getParcelableExtra(EXTRA_LOCATION_DATA)
        } else if (intent.hasExtra(EXTRA_KEY_ID) && intent.hasExtra(EXTRA_KEY)) {
            val key: Key = intent.getParcelableExtra(EXTRA_KEY)!!

            keyId = intent.getStringExtra(EXTRA_KEY_ID)!!
            locationModel = LocationModel(key.address, key.lat, key.lon)

            edit_name.editText!!.setText(key.name)
            edit_key.editText!!.setText(key.key)

            supportActionBar?.title = getString(R.string.title_edit_key)
            btn_add.text = getString(R.string.action_edit)
        }
        updateLocation()
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

    private fun updateLocation() {
        edit_location.editText?.setText(locationModel?.address!!)
    }

    companion object {
        const val EXTRA_LOCATION_DATA = "extra_location_data"
        const val EXTRA_KEY_ID = "extra_key_id"
        const val EXTRA_KEY = "extra_key"

        fun openActivity(context: Context, locationModel: LocationModel) {
            val intent = Intent(context, AddKeyActivity::class.java)
            intent.putExtra(EXTRA_LOCATION_DATA, locationModel)
            context.startActivity(intent)
        }

        fun openActivity(context: Context, keyId: String, key: Key) {
            val intent = Intent(context, AddKeyActivity::class.java)
            intent.putExtra(EXTRA_KEY_ID, keyId)
            intent.putExtra(EXTRA_KEY, key)
            context.startActivity(intent)
        }
    }
}
