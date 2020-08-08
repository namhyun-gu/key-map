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
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.ui.editlocation.EditLocationActivity
import kotlinx.android.synthetic.main.activity_add_key.*

@AndroidEntryPoint
class AddKeyActivity : AppCompatActivity(R.layout.activity_add_key) {
    private val viewModel by viewModels<AddKeyViewModel>()

    private var locationData: LocationData? = null

    private val openEditLocation = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                updateLocation(result.data!!)
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
            intent.putExtra(EXTRA_LOCATION_DATA, locationData!!)
            openEditLocation.launch(intent)
        }

        btn_add.setOnClickListener {
            val name = edit_name.editText!!.text.toString()
            val key = edit_key.editText!!.text.toString()
            viewModel.createKey(name, key, locationData!!)
        }

        btn_cancel.setOnClickListener { onBackPressed() }

        viewModel.addKeyFormData.observe(this, Observer {
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

        if (!intent.hasExtra(EXTRA_LOCATION_DATA)) {
            throw IllegalAccessError("Require EXTRA_LOCATION_DATA extra")
        }
        updateLocation(intent)
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

    private fun updateLocation(intent: Intent) {
        locationData = intent.getParcelableExtra(EXTRA_LOCATION_DATA)
        edit_location.editText?.setText(locationData?.address!!)
    }

    companion object {
        const val EXTRA_LOCATION_DATA = "extra_location_data"

        fun openActivity(context: Context, locationData: LocationData) {
            val intent = Intent(context, AddKeyActivity::class.java)
            intent.putExtra(EXTRA_LOCATION_DATA, locationData)
            context.startActivity(intent)
        }
    }
}