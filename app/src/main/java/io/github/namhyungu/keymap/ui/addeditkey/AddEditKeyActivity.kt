package io.github.namhyungu.keymap.ui.addeditkey

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.coroutineScope
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.namhyungu.keymap.R
import io.github.namhyungu.keymap.databinding.ActivityAddkeyBinding
import io.github.namhyungu.keymap.ui.picklocation.PickLocationActivity
import io.github.namhyungu.keymap.util.EventObserver
import io.github.namhyungu.keymap.util.Util
import io.github.namhyungu.keymap.util.awaitMap
import io.github.namhyungu.keymap.util.viewBindings
import timber.log.Timber

@AndroidEntryPoint
class AddEditKeyActivity : AppCompatActivity() {
    private val viewModel: AddEditKeyViewModel by viewModels()
    private val binding: ActivityAddkeyBinding by viewBindings(ActivityAddkeyBinding::inflate)

    private val keyId: String? by bundle("keyId")
    private val location: Location? by bundle("location")
    private var naverMap: NaverMap? = null

    private val startPickLocation = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val locationResult = result.data?.getParcelableExtra<Location>("location")
                viewModel.updateLocation(checkNotNull(locationResult))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (keyId != null) {
            binding.toolbar.setTitle(R.string.title_edit_key)
        } else {
            binding.toolbar.setTitle(R.string.title_add_key)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
        lifecycle.coroutineScope.launchWhenCreated {
            naverMap = mapFragment.awaitMap().also { map ->
                map.uiSettings.apply {
                    isLocationButtonEnabled = false
                    isZoomControlEnabled = false
                    logoGravity = Gravity.TOP or Gravity.START
                    setLogoMargin(24, 24, 0, 0)
                }
                map.locationOverlay.isVisible = true
            }
            viewModel.setIsMapReady()
        }

        binding.editContent.doOnTextChanged { text, _, _, _ ->
            viewModel.updateContent(text.toString())
        }

        binding.editDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.updateDescription(text.toString())
        }

        binding.layoutAddress.setEndIconOnClickListener {
            viewModel.startPickLocation()
        }

        binding.editDetail.doOnTextChanged { text, _, _, _ ->
            viewModel.updateDetail(text.toString())
        }

        binding.fab.setOnClickListener {
            viewModel.saveKey()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.uiState.observe(this, {
            Timber.d("Update uiState: $it")

            val (
                _,
                content,
                description,
                location,
                address,
                detail,
                isMapReady,
            ) = it

            binding.editContent.apply {
                if (text.toString() != content) {
                    setText(content)
                }
            }

            binding.editDescription.apply {
                if (text.toString() != description) {
                    setText(description)
                }
            }

            binding.editDetail.apply {
                if (text.toString() != detail) {
                    setText(detail)
                }
            }

            if (address != null) {
                var addressStr = Util.getAddressString(address)
                val buildingName = Util.getBuildingName(address)
                if (buildingName.isNotEmpty()) {
                    addressStr += " ($buildingName)"
                }
                binding.editAddress.setText(addressStr)
            }

            if (isMapReady && location != null) {
                val map = checkNotNull(naverMap)
                val locationOverlay = map.locationOverlay
                val latLng = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdate.toCameraPosition(
                    CameraPosition(latLng, 17.0)
                )

                map.moveCamera(cameraUpdate)
                locationOverlay.position = latLng
            }
        })

        viewModel.keySavedEvent.observe(this, EventObserver {
            Toast.makeText(this, getString(R.string.msg_key_saved), Toast.LENGTH_SHORT).show()
            finish()
        })

        viewModel.contentIsEmptyEvent.observe(this, EventObserver {
            Snackbar.make(
                binding.root,
                getString(R.string.msg_content_empty),
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
        })

        viewModel.startPickLocationEvent.observe(this, EventObserver {
            val intent = intentOf<PickLocationActivity> {
                putExtra("location" to it)
            }
            startPickLocation.launch(intent)
        })

        viewModel.start(keyId, location)
    }
}