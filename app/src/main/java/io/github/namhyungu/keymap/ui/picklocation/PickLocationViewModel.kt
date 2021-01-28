package io.github.namhyungu.keymap.ui.picklocation

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PickLocationViewModel @ViewModelInject constructor(
    private val reverseGeocodingService: ReverseGeocodingService,
) : ViewModel() {
    private val _uiState = MutableLiveData(PickLocationUiState())
    val uiState: LiveData<PickLocationUiState> = _uiState

    private val _finishPickEvent = MutableLiveData<Event<Location>>()
    val finishPickEvent: LiveData<Event<Location>> = _finishPickEvent

    private var isDataLoaded = false

    fun start(location: Location) {
        if (isDataLoaded) {
            return
        }

        isDataLoaded = true
        updateLocation(location)
    }

    fun setIsMapReady() {
        _uiState.value = _uiState.value!!.copy(
            isMapReady = true
        )
    }

    fun setFirstStart() {
        _uiState.value = _uiState.value!!.copy(
            firstStart = false
        )
    }

    fun finishPick() {
        val uiState = _uiState.value!!
        if (uiState.location != null) {
            _finishPickEvent.value = Event(uiState.location)
        }
    }

    fun updateLocation(location: Location) {
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
}