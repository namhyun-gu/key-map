package io.github.namhyungu.keymap.ui.addeditkey

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditKeyActivity : AppCompatActivity() {
//    private val viewModel: AddEditKeyViewModel by viewModels()
//    private val binding: ActivityAddkeyBinding by viewBindings(ActivityAddkeyBinding::inflate)
//
//    private val keyId: String? by bundle("keyId")
//    private val location: Location? by bundle("location")
//    private var naverMap: NaverMap? = null
//
//    private val startPickLocation = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        when (result.resultCode) {
//            Activity.RESULT_OK -> {
//                val locationResult = result.data?.getParcelableExtra<Location>("location")
//                viewModel.updateLocation(checkNotNull(locationResult))
//            }
//        }
//    }
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
//        if (keyId != null) {
//            binding.toolbar.setTitle(R.string.title_edit_key)
//        } else {
//            binding.toolbar.setTitle(R.string.title_add_key)
//        }
//
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
//        lifecycle.coroutineScope.launchWhenCreated {
//            naverMap = mapFragment.awaitMap().also { map ->
//                map.uiSettings.apply {
//                    isLocationButtonEnabled = false
//                    isZoomControlEnabled = false
//                    logoGravity = Gravity.TOP or Gravity.START
//                    setLogoMargin(24, 24, 0, 0)
//                }
//                map.locationOverlay.isVisible = true
//            }
//            viewModel.setIsMapReady()
//        }
//
//        binding.editContent.doOnTextChanged { text, _, _, _ ->
//            viewModel.updateContent(text.toString())
//        }
//
//        binding.editDescription.doOnTextChanged { text, _, _, _ ->
//            viewModel.updateDescription(text.toString())
//        }
//
//        binding.layoutAddress.setEndIconOnClickListener {
//            viewModel.startPickLocation()
//        }
//
//        binding.editDetail.doOnTextChanged { text, _, _, _ ->
//            viewModel.updateDetail(text.toString())
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        viewModel.uiState.observe(this, {
//            Timber.d("Update uiState: $it")
//
//            val (
//                _,
//                content,
//                description,
//                location,
//                address,
//                detail,
//                isMapReady,
//            ) = it
//
//            binding.editContent.apply {
//                if (text.toString() != content) {
//                    setText(content)
//                }
//            }
//
//            binding.editDescription.apply {
//                if (text.toString() != description) {
//                    setText(description)
//                }
//            }
//
//            binding.editDetail.apply {
//                if (text.toString() != detail) {
//                    setText(detail)
//                }
//            }
//
//            if (address != null) {
//                var addressStr = Util.getAddressString(address)
//                val buildingName = Util.getBuildingName(address)
//                if (buildingName.isNotEmpty()) {
//                    addressStr += " ($buildingName)"
//                }
//                binding.editAddress.setText(addressStr)
//            }
//
//            if (isMapReady && location != null) {
//                val map = checkNotNull(naverMap)
//                val locationOverlay = map.locationOverlay
//                val latLng = LatLng(location.latitude, location.longitude)
//                val cameraUpdate = CameraUpdate.toCameraPosition(
//                    CameraPosition(latLng, 17.0)
//                )
//
//                map.moveCamera(cameraUpdate)
//                locationOverlay.position = latLng
//            }
//        })
//
//        viewModel.keySavedEvent.observe(this, EventObserver {
//            Toast.makeText(this, getString(R.string.msg_key_saved), Toast.LENGTH_SHORT).show()
//            finish()
//        })
//
//        viewModel.keyDeletedEvent.observe(this, EventObserver {
//            Toast.makeText(this, getString(R.string.msg_key_deleted), Toast.LENGTH_SHORT).show()
//            finish()
//        })
//
//        viewModel.contentIsEmptyEvent.observe(this, EventObserver {
//            Snackbar.make(
//                binding.root,
//                getString(R.string.msg_content_empty),
//                BaseTransientBottomBar.LENGTH_SHORT
//            ).show()
//        })
//
//        viewModel.startPickLocationEvent.observe(this, EventObserver {
//            val intent = intentOf<PickLocationActivity> {
//                putExtra("location" to it)
//            }
//            startPickLocation.launch(intent)
//        })
//
//        viewModel.start(keyId, location)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_addedit_key, menu)
//        if (keyId == null) {
//            menu?.findItem(R.id.action_delete)?.isVisible = false
//        }
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_save -> {
//                viewModel.saveKey()
//            }
//            R.id.action_delete -> {
//                viewModel.deleteKey(keyId!!)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}