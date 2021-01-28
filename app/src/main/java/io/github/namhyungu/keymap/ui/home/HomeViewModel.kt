package io.github.namhyungu.keymap.ui.home

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.namhyungu.keymap.data.source.KeyDataSource
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel @ViewModelInject constructor(
    private val keyDataSource: KeyDataSource,
    private val reverseGeocodingService: ReverseGeocodingService,
) : ViewModel() {
    private val _uiState = MutableLiveData(HomeUiState())
    val uiState: LiveData<HomeUiState> = _uiState

    private val _startAddKeyEvent = MutableLiveData<Event<Location>>()
    val startAddKeyEvent: LiveData<Event<Location>> = _startAddKeyEvent

    private val _startEditKeyEvent = MutableLiveData<Event<String>>()
    val startEditKeyEvent: LiveData<Event<String>> = _startEditKeyEvent

    fun fetchKeyList() {
        viewModelScope.launch {
            keyDataSource.observeKeys().collect {
                _uiState.value = _uiState.value!!.copy(
                    keyList = it
                )
            }
        }
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            _uiState.value = _uiState.value!!.copy(
                location = location
            )

            val reverseGeocodingResult = withContext(Dispatchers.IO) {
                reverseGeocodingService.reverseGeocoding(location.latitude, location.longitude)
            }

            val address = reverseGeocodingResult.run {
                "$city $address1"
            }

            _uiState.value = _uiState.value!!.copy(
                address = address
            )
        }
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

    fun startAddKey() {
        val uiState = _uiState.value!!
        if (uiState.location != null) {
            _startAddKeyEvent.value = Event(uiState.location)
        }
    }

    fun startEditKey(keyId: String) {
        _startEditKeyEvent.value = Event(keyId)
    }
}