package io.github.namhyungu.keymap.util

import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.viewbinding.ViewBinding

inline fun <T : ViewBinding> ComponentActivity.viewBindings(
    crossinline bindingInflater: (LayoutInflater) -> T,
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}