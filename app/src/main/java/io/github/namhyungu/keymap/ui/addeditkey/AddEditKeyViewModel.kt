package io.github.namhyungu.keymap.ui.addeditkey

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.data.Place
import io.github.namhyungu.keymap.data.source.KeyDataSource
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.util.Event
import io.github.namhyungu.keymap.util.toGeohash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AddEditKeyViewModel @ViewModelInject constructor(
    private val keyDataSource: KeyDataSource,
    private val reverseGeocodingService: ReverseGeocodingService,
) : ViewModel() {
    private val _uiState = MutableLiveData(AddEditKeyUiState())
    val uiState: LiveData<AddEditKeyUiState> = _uiState

    private val _keySavedEvent = MutableLiveData<Event<Unit>>()
    val keySavedEvent: LiveData<Event<Unit>> = _keySavedEvent

    private val _contentIsEmptyEvent = MutableLiveData<Event<Unit>>()
    val contentIsEmptyEvent: LiveData<Event<Unit>> = _contentIsEmptyEvent

    private val _startPickLocationEvent = MutableLiveData<Event<Location>>()
    val startPickLocationEvent: LiveData<Event<Location>> = _startPickLocationEvent

    private var isDataLoaded = false

    fun start(keyId: String?, location: Location?) {
        if (isDataLoaded) {
            return
        }

        viewModelScope.launch {
            if (keyId != null) {
                val key = keyDataSource.getKey(keyId)
                if (key != null) {
                    isDataLoaded = true
                    _uiState.value = _uiState.value!!.copy(
                        id = keyId,
                        content = key.content,
                        description = key.description,
                        location = key.place!!.location,
                        address = key.place.address,
                        detail = key.place.detail
                    )
                }
            }

            if (location != null) {
                updateLocation(location)
            }
        }
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value!!.copy(
            content = content
        )
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value!!.copy(
            description = description
        )
    }

    fun updateDetail(detail: String) {
        _uiState.value = _uiState.value!!.copy(
            detail = detail
        )
    }

    fun updateLocation(location: Location) {
        Timber.d("Updated $location")
        viewModelScope.launch {
            val address = withContext(Dispatchers.IO) {
                reverseGeocodingService.reverseGeocoding(location.latitude, location.longitude)
            }

            _uiState.value = _uiState.value!!.copy(
                location = location,
                address = address
            )
        }
    }

    fun setIsMapReady() {
        _uiState.value = _uiState.value!!.copy(
            isMapReady = true
        )
    }

    fun saveKey() {
        viewModelScope.launch {
            val uiState = _uiState.value!!
            val address = checkNotNull(uiState.address)
            val location = checkNotNull(uiState.location)
            val geohash = location.toGeohash()
            val key = Key(
                id = if (uiState.id.isNotEmpty()) uiState.id else "",
                content = uiState.content,
                description = uiState.description,
                place = Place(
                    geohash = geohash,
                    lat = location.latitude,
                    lon = location.longitude,
                    coordinates = GeoPoint(location.latitude, location.longitude),
                    addressData = address.toMap(),
                    detail = uiState.detail,
                )
            )

            if (!key.isEmpty) {
                keyDataSource.saveKey(key)
                _keySavedEvent.value = Event(Unit)
            } else {
                _contentIsEmptyEvent.value = Event(Unit)
            }
        }
    }

    fun startPickLocation() {
        val uiState = _uiState.value!!
        if (uiState.location != null) {
            _startPickLocationEvent.value = Event(uiState.location)
        }
    }
}