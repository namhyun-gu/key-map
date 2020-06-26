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
package dev.namhyun.geokey.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dev.namhyun.geokey.model.Location
import dev.namhyun.geokey.util.safeOffer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationDataSourceImpl(context: Context) : LocationDataSource {
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): Location {
        return suspendCoroutine { cont ->
            locationClient.lastLocation
                .addOnSuccessListener { cont.resume(Location(it.latitude, it.longitude)) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }

    @SuppressLint("MissingPermission")
    @ExperimentalCoroutinesApi
    override fun getLocationUpdates(): Flow<Location> {
        return callbackFlow {
            if (locationCallback == null) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult?) {
                        super.onLocationResult(result)
                        result ?: return
                        val lastLocation = result.lastLocation
                        channel.safeOffer(Location(lastLocation.latitude, lastLocation.longitude))
                    }
                }
            }
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                null
            )
            awaitClose { locationClient.removeLocationUpdates(locationCallback) }
        }
    }

    companion object {
        private val locationRequest = LocationRequest.create().apply {
            interval = 15 * 60 * 1000
            fastestInterval = 2 * 60 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}
