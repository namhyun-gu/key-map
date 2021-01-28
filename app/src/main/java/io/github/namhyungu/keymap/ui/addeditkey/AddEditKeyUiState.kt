package io.github.namhyungu.keymap.ui.addeditkey

import android.location.Location
import io.github.namhyungu.keymap.data.BaseAddress

data class AddEditKeyUiState(
    val id: String = "",
    val content: String = "",
    val description: String = "",
    val location: Location? = null,
    val address: BaseAddress? = null,
    val detail: String = "",
    val isMapReady: Boolean = false,
)
