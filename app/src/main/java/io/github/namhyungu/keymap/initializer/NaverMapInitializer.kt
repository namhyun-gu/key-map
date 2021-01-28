package io.github.namhyungu.keymap.initializer

import android.content.Context
import androidx.startup.Initializer
import com.naver.maps.map.NaverMapSdk
import io.github.namhyungu.keymap.BuildConfig
import timber.log.Timber

class NaverMapInitializer : Initializer<NaverMapSdk> {
    override fun create(context: Context): NaverMapSdk {
        Timber.d("NaverMapInitializer is created.")
        return NaverMapSdk.getInstance(context).apply {
            client = NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NCP_CLIENT_ID)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(TimberInitializer::class.java)
    }
}