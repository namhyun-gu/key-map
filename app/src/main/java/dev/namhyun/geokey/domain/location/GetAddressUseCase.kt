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
package dev.namhyun.geokey.domain.location

import dev.namhyun.geokey.BuildConfig
import dev.namhyun.geokey.data.remote.GeocodingService
import dev.namhyun.geokey.di.IoDispatcher
import dev.namhyun.geokey.domain.UseCase
import dev.namhyun.geokey.model.LocationModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class GetAddressUseCase @Inject constructor(
  private val geocodingService: GeocodingService,
  @IoDispatcher ioDispatcher: CoroutineDispatcher
) : UseCase<LocationModel, String>(ioDispatcher) {
    override suspend fun execute(parameters: LocationModel): String {
        val coords = "${parameters.lon},${parameters.lat}"
        val address = geocodingService.reverseGeocode(
            BuildConfig.NCP_CLIENT_ID,
            BuildConfig.NCP_CLIENT_SECRET,
            coords
        ).getAddress()
        return address ?: ""
    }
}
