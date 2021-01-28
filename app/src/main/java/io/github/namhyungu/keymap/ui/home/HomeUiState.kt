package io.github.namhyungu.keymap.ui.home

import android.location.Location
import io.github.namhyungu.keymap.data.Key
import javax.annotation.concurrent.Immutable

@Immutable
data class HomeUiState(
    val keyList: List<Key> = emptyList(),
    val location: Location? = null,
    val address: String = "",
    val isMapReady: Boolean = false,
    val firstStart: Boolean = true,
)
