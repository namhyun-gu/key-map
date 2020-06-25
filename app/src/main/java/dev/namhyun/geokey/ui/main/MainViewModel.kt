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

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.model.Resource
import dev.namhyun.geokey.repository.MainRepository
import dev.namhyun.geokey.util.KeyUtil
import dev.namhyun.geokey.util.latLng
import dev.namhyun.geokey.util.launchOnViewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
  private val mainRepository: MainRepository
) : ViewModel() {
    val locationData = mainRepository.getLastLocation().switchMap {
        launchOnViewModelScope {
            mainRepository.reverseGeocoding(it.lat, it.lon) {
                Timber.e(it)
            }
        }
    }

    val networkStateData = mainRepository.getNetworkState()
    val keyData = mainRepository.readAllKey()
    val markerData = MutableLiveData<Map<LatLng, List<Document<Key>>>>()
    val addKeyFormData = MutableLiveData<AddKeyFormState>(EmptyData)

    fun updateMarker(region: Array<LatLng>) {
        val bounds = LatLngBounds.Builder().apply {
            for (idx in 1..4) {
                include(region[idx])
            }
        }.build()

        val keys = keyData.value
        if (keys != null && keys is Resource.Success) {
            val keysInBounds = keys.data
                .filter { bounds.contains(it.value.latLng) }
            val nearKeys = KeyUtil.collectNearKeys(keysInBounds)
            markerData.value = nearKeys
        }
    }

    fun createKey(name: String, key: String, location: LocationData) {
        val invalidItems = mutableListOf<String>()
        if (name.trim().isEmpty()) {
            invalidItems.add("name")
        }
        if (key.trim().isEmpty()) {
            invalidItems.add("key")
        }
        if (invalidItems.isNotEmpty()) {
            addKeyFormData.value = InvalidData(invalidItems)
            return
        }
        val keyData = Key(name = name, key = key, locationData = location)
        viewModelScope.launch {
            mainRepository.createKey(keyData)
            addKeyFormData.value = ValidData
        }
    }

    fun resetForm() {
        addKeyFormData.value = EmptyData
    }
}
