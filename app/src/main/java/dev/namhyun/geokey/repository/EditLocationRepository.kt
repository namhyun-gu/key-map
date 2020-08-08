package dev.namhyun.geokey.repository

import androidx.lifecycle.MutableLiveData
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.network.GeocodingClient
import dev.namhyun.geokey.util.getAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditLocationRepository @Inject constructor(
    val geocodingClient: GeocodingClient
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
}