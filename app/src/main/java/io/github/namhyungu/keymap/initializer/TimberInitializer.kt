package io.github.namhyungu.keymap.initializer

import android.content.Context
import androidx.startup.Initializer
import io.github.namhyungu.keymap.BuildConfig
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Timber.d("TimberInitializer is created.")
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}