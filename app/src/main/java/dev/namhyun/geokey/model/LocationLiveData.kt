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
package dev.namhyun.geokey.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationLiveData(context: Context) : LiveData<Location>() {
    private val client = LocationServices.getFusedLocationProviderClient(context)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            postValue(result.lastLocation)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        client.lastLocation.addOnSuccessListener {
            postValue(it)
        }
        client.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onInactive() {
        super.onInactive()
        client.removeLocationUpdates(locationCallback)
    }

    companion object {
        private val locationRequest = LocationRequest.create().apply {
            interval = 15 * 60 * 1000
            fastestInterval = 2 * 60 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}
