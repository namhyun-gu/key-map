package io.github.namhyungu.keymap.domain.home

import io.github.namhyungu.keymap.data.BaseAddress
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.domain.UseCase
import javax.inject.Inject

class GetReverseGeocodingUseCase @Inject constructor(
    private val reverseGeocodingService: ReverseGeocodingService
) : UseCase<GetReverseGeocodingUseCase.Parameter, BaseAddress>() {

    override suspend fun execute(parameters: Parameter): BaseAddress {
        return reverseGeocodingService.reverseGeocoding(parameters.lat, parameters.lon)
    }

    data class Parameter(
        val lat: Double,
        val lon: Double,
    )
}