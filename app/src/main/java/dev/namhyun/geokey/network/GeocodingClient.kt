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
package dev.namhyun.geokey.network

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import dev.namhyun.geokey.BuildConfig
import dev.namhyun.geokey.model.GeocodingResponse
import javax.inject.Inject

class GeocodingClient @Inject constructor(
  private val geocodingService: GeocodingService
) {

    fun reverseGeocode(
      lat: Double,
      lon: Double,
      onResult: (response: ApiResponse<GeocodingResponse>) -> Unit
    ) {
        geocodingService.reverseGeocode(
            BuildConfig.NCP_CLIENT_ID,
            BuildConfig.NCP_CLIENT_SECRET,
            "$lon,$lat"
        ).request(onResult)
    }
}
