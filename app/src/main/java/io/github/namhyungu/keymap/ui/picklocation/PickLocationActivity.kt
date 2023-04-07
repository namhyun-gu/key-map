package io.github.namhyungu.keymap.ui.picklocation

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickLocationActivity : AppCompatActivity() {
//    private val viewModel: PickLocationViewModel by viewModels()
//    private val binding: ActivityPickLocationBinding by viewBindings(ActivityPickLocationBinding::inflate)
//
//    private val location: Location by bundleNonNull("location")
//    private var naverMap: NaverMap? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
//
//        binding.toolbar.setNavigationOnClickListener {
//            finish()
//        }
//
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
//        lifecycle.coroutineScope.launchWhenCreated {
//            naverMap = mapFragment.awaitMap().also { map ->
//                map.uiSettings.apply {
//                    isLocationButtonEnabled = false
//                    logoGravity = Gravity.TOP or Gravity.START
//                    setLogoMargin(24, 24, 0, 0)
//                }
//                map.locationOverlay.isVisible = true
//                map.setOnMapClickListener { _, latLng ->
//                    val location = Location("").apply {
//                        latitude = latLng.latitude
//                        longitude = latLng.longitude
//                    }
//                    viewModel.updateLocation(location)
//                }
//            }
//            viewModel.setIsMapReady()
//        }
//
//        binding.fab.setOnClickListener {
//            viewModel.finishPick()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        viewModel.uiState.observe(this, {
//            val (location, address, isMapReady, firstStart) = it
//
//            if (address != null) {
//                var addressStr = Util.getAddressString(address)
//                val buildingName = Util.getBuildingName(address)
//                if (buildingName.isNotEmpty()) {
//                    addressStr += " ($buildingName)"
//                }
//                binding.address.text = addressStr
//            }
//
//            if (isMapReady && location != null) {
//                val map = checkNotNull(naverMap)
//                val latLng = LatLng(location.latitude, location.longitude)
//                val locationOverlay = map.locationOverlay
//                locationOverlay.position = latLng
//
//                if (firstStart) {
//                    val cameraUpdate = CameraUpdate.toCameraPosition(
//                        CameraPosition(latLng, 17.0)
//                    )
//                    map.moveCamera(cameraUpdate)
//
//                    viewModel.setFirstStart()
//                }
//            }
//        })
//
//        viewModel.finishPickEvent.observe(this, EventObserver {
//            val intent = Intent()
//            intent.putExtra("location", it)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
//        })
//
//        viewModel.start(location)
//    }
}