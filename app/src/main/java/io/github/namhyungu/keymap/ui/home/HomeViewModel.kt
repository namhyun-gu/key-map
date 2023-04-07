package io.github.namhyungu.keymap.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.data.data
import io.github.namhyungu.keymap.domain.home.GetReverseGeocodingUseCase
import io.github.namhyungu.keymap.domain.key.ObserveKeyListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

data class Location(
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)

@Immutable
data class HomeUiState(
    val keyList: List<Key> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
)

// TODO: 로그인 상태가 변경될 떄 `key` 값을 다시 요청해야 함
// TODO: Paging 3 으로 처리하도록 개선
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeKeyListUseCase: ObserveKeyListUseCase,
    private val getReverseGeocodingUseCase: GetReverseGeocodingUseCase,
) : ViewModel() {

    private val location: MutableStateFlow<Location> = MutableStateFlow(Location())
    private val address: MutableStateFlow<String> = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        observeKeyListUseCase(Unit).map { it.data ?: emptyList() },
        location,
        address
    ) { keyList, location, address ->
        HomeUiState(
            keyList = keyList,
            latitude = location.lat,
            longitude = location.lon,
            address = address
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState()
    )

    fun updateLocation(newLocation: Location) = viewModelScope.launch {
        location.emit(newLocation)
        address.emit(getReverseGeocodingUseCase(GetReverseGeocodingUseCase.Parameter(newLocation.lat, newLocation.lon)).data?.toAddressString().orEmpty())
    }
}