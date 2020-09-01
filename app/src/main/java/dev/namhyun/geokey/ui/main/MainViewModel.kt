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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dev.namhyun.geokey.domain.key.GetKeysUseCase
import dev.namhyun.geokey.domain.location.GetAddressUseCase
import dev.namhyun.geokey.domain.location.GetLocationsUseCase
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.model.Result
import dev.namhyun.geokey.model.data
import dev.namhyun.geokey.model.succeeded
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@ExperimentalCoroutinesApi
class MainViewModel @ViewModelInject constructor(
    getKeysUseCase: GetKeysUseCase,
    getLocationsUseCase: GetLocationsUseCase,
    getAddressUseCase: GetAddressUseCase
) : ViewModel() {
    val keys = liveData {
        emit(null)
        getKeysUseCase(Unit).collect {
            if (it.succeeded) {
                emit(it.data)
            }
        }
    }

    val location = liveData {
        emit(null)
        getLocationsUseCase(Unit).collect { locationResult ->
            when (locationResult) {
                is Result.Success -> {
                    when (val addressResult = getAddressUseCase(locationResult.data)) {
                        is Result.Success -> {
                            val (_, lat, lon) = locationResult.data
                            emit(LocationModel(addressResult.data, lat, lon))
                        }
                        is Result.Error -> {
                            Timber.e(addressResult.exception)
                        }
                    }
                }
                is Result.Error -> {
                    Timber.e(locationResult.exception)
                }
            }
        }
    }
}
