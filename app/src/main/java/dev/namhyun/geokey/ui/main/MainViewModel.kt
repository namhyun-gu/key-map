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
package dev.namhyun.geokey.ui.main

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationLiveData
import dev.namhyun.geokey.model.NetworkLiveData
import dev.namhyun.geokey.model.Resource
import dev.namhyun.geokey.repository.MainRepository
import dev.namhyun.geokey.util.KeyUtil
import dev.namhyun.geokey.util.latLng
import dev.namhyun.geokey.util.launchOnViewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
  application: Application,
  private val mainRepository: MainRepository
) : AndroidViewModel(application) {
    private val _locationData = LocationLiveData(application)
    val locationData = _locationData.switchMap {
        launchOnViewModelScope {
            mainRepository.reverseGeocoding(it.latitude, it.longitude) {
                Timber.e(it)
            }
        }
    }

    val networkData = NetworkLiveData(application)
    val keyData = mainRepository.readAllKey()
    val markerData = MutableLiveData<Map<LatLng, List<Key>>>()

    fun updateMarker(region: Array<LatLng>) {
        val bounds = LatLngBounds.Builder().apply {
            for (idx in 1..4) {
                include(region[idx])
            }
        }.build()

        val keys = keyData.value
        if (keys != null && keys is Resource.Success) {
            val keysInBounds = keys.data
                .map { it.value }
                .filter { bounds.contains(it.latLng) }
            val nearKeys = KeyUtil.collectNearKeys(keysInBounds)
            markerData.value = nearKeys
        }
    }

    fun deleteKey(id: String) {
        viewModelScope.launch {
            mainRepository.deleteKey(id)
        }
    }
}
