package io.github.namhyungu.keymap.domain.home

import io.github.namhyungu.keymap.data.BaseAddress
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.di.IoDispatcher
import io.github.namhyungu.keymap.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetReverseGeocodingUseCase @Inject constructor(
    private val reverseGeocodingService: ReverseGeocodingService,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : UseCase<GetReverseGeocodingUseCase.Parameter, BaseAddress>() {

    override suspend fun execute(parameters: Parameter): BaseAddress {
        return withContext(coroutineDispatcher) {
            reverseGeocodingService.reverseGeocoding(parameters.lat, parameters.lon)
        }
    }

    data class Parameter(
        val lat: Double,
        val lon: Double,
    )
}