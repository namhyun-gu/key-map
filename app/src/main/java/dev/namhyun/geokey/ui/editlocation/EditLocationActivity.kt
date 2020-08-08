package dev.namhyun.geokey.ui.editlocation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import dagger.hilt.android.AndroidEntryPoint
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.ui.addkey.AddKeyActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class EditLocationActivity : AppCompatActivity(R.layout.activity_edit_location),
    OnMapReadyCallback {
    private val viewModel by viewModels<EditLocationViewModel>()

    private lateinit var naverMap: NaverMap

    private var locationData: LocationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.locationData.observe(this, Observer {
            updateLocation(it)
        })
    }

    override fun onStart() {
        super.onStart()
        if (!intent.hasExtra(AddKeyActivity.EXTRA_LOCATION_DATA)) {
            throw IllegalAccessError("Require EXTRA_LOCATION_DATA extra")
        }
        locationData = intent.getParcelableExtra(AddKeyActivity.EXTRA_LOCATION_DATA)
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
                intent.putExtra(AddKeyActivity.EXTRA_LOCATION_DATA, locationData!!)
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

        tv_current_location.text = locationData?.address!!
        updateMap(LatLng(locationData?.lat!!, locationData?.lon!!))
    }

    private fun updateLocation(locationData: LocationData) {
        this.locationData = locationData
        tv_current_location.text = locationData.address
        naverMap.locationOverlay.position = LatLng(locationData.lat, locationData.lon)
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