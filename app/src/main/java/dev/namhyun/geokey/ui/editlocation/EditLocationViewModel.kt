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
