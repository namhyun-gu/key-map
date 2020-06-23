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
package dev.namhyun.geokey.repository

import androidx.lifecycle.MutableLiveData
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import dev.namhyun.geokey.data.KeyDatabase
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.network.GeocodingClient
import dev.namhyun.geokey.util.getAddress
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository @Inject constructor(
  val geocodingClient: GeocodingClient,
  val keyDatabase: KeyDatabase
) {
    suspend fun reverseGeocoding(lat: Double, lon: Double, error: (String) -> Unit) =
        withContext(Dispatchers.IO) {
            val liveData = MutableLiveData<LocationData>()
            geocodingClient.reverseGeocode(lat, lon) {
                it.onSuccess {
                    data?.let { response ->
                        val address = response.getAddress() ?: ""
                        liveData.postValue(
                            LocationData(address, lat, lon)
                        )
                    }
                }
                    .onError { error(message()) }
                    .onException { error(message()) }
            }
            liveData
        }

    fun readAllKey() = keyDatabase.readAllKey()

    suspend fun createKey(key: Key) = keyDatabase.createKey(key)

    suspend fun deleteKey(id: String) = keyDatabase.deleteKey(id)
}
