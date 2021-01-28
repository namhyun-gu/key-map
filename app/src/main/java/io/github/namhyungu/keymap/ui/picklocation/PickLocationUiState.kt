package io.github.namhyungu.keymap.ui.picklocation

import android.location.Location
import io.github.namhyungu.keymap.data.BaseAddress

data class PickLocationUiState(
    val location: Location? = null,
    val address: BaseAddress? = null,
    val isMapReady: Boolean = false,
    val firstStart: Boolean = true,
)