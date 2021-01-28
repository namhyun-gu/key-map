package io.github.namhyungu.keymap.data.source

import io.github.namhyungu.keymap.data.BaseAddress

interface ReverseGeocodingService {

    suspend fun reverseGeocoding(lat: Double, lon: Double): BaseAddress

}