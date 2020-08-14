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
import androidx.lifecycle.viewModelScope
import dev.namhyun.geokey.domain.location.GetAddressUseCase
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.model.Result
import kotlinx.coroutines.launch
import timber.log.Timber

class EditLocationViewModel @ViewModelInject constructor(
  private val getAddressUseCase: GetAddressUseCase
) : ViewModel() {
    private val _location = MutableLiveData<LocationModel>()
    val location: LiveData<LocationModel> = _location

    fun updateLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            when (val result = getAddressUseCase(LocationModel("", lat, lon))) {
                is Result.Success -> _location.postValue(LocationModel(result.data, lat, lon))
                is Result.Error -> {
                    Timber.e(result.exception)
                    _location.postValue(LocationModel("", lat, lon))
                }
            }
        }
    }
}
