package dev.namhyun.geokey.ui.editlocation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.repository.EditLocationRepository
import dev.namhyun.geokey.util.launchOnViewModelScope
import timber.log.Timber

class EditLocationViewModel @ViewModelInject constructor(
    private val editLocationRepository: EditLocationRepository
) : ViewModel() {
    private val _locationData = MutableLiveData<LocationData>()
    val locationData: LiveData<LocationData> = _locationData.switchMap {
        launchOnViewModelScope {
            editLocationRepository.reverseGeocoding(it.lat, it.lon) {
                Timber.e(it)
            }
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        _locationData.postValue(LocationData("", lat, lon))
    }
}