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
package dev.namhyun.geokey.util

import android.location.Location
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationModel
import java.util.concurrent.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel

@ExperimentalCoroutinesApi
fun <E> SendChannel<E>.safeOffer(value: E) = !isClosedForSend && try {
    offer(value)
} catch (e: CancellationException) {
    false
}

fun Key.distanceTo(location: LocationModel): Float {
    val it = Location("").apply {
        latitude = lat
        longitude = lon
    }
    val target = Location("").apply {
        latitude = location.lat
        longitude = location.lon
    }
    return target.distanceTo(it)
}

val Key.latLng: LatLng
    get() = LatLng(lat, lon)

val Key.locationModel: LocationModel
    get() = LocationModel(address, lat, lon)

fun Float.meter(): String {
    return if (this > 1000.0) {
        "${String.format("%.1f", this / 1000)} km"
    } else {
        "${String.format("%.1f", this)} m"
    }
}

fun LatLng.nearBounds(meter: Double): LatLngBounds {
    val northOffset = arrayOf(meter, meter, -meter, -meter)
    val eastOffset = arrayOf(-meter, meter, -meter, meter)

    return LatLngBounds.Builder().apply {
        for (idx in 0..3) {
            include(offset(northOffset[idx], eastOffset[idx]))
        }
    }.build()
}
