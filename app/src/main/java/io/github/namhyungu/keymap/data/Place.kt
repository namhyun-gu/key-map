package io.github.namhyungu.keymap.data

import android.location.Location
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint

data class Place(
    val geohash: String,
    val lat: Double,
    val lon: Double,
    val coordinates: GeoPoint,
    val addressData: Map<String, Any>,
    val detail: String,
) {
    constructor() : this("", 0.0, 0.0, GeoPoint(0.0, 0.0), emptyMap(), "")

    @get:Exclude
    val location: Location
        get() = Location("").apply {
            latitude = lat
            longitude = lon
        }

    @get:Exclude
    val address: BaseAddress
        get() = BaseAddress.fromMap(addressData)
}