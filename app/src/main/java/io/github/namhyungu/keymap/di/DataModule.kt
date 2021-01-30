package io.github.namhyungu.keymap.di

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.namhyungu.keymap.data.source.KeyDataSource
import io.github.namhyungu.keymap.data.source.ReverseGeocodingService
import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.data.source.remote.KeyRemoteDataSource
import io.github.namhyungu.keymap.data.source.remote.ReverseGeocodingServiceImpl
import io.github.namhyungu.keymap.data.source.remote.UserRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.chromium.net.CronetEngine
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DataModule {
    @Singleton
    @Provides
    fun provideReverseGeocodingService(@ApplicationContext context: Context): ReverseGeocodingService {
        val cronetEngine = CronetEngine.Builder(context).build()
        return ReverseGeocodingServiceImpl(cronetEngine)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Singleton
    @Provides
    fun provideKeyDataSource(@KeyCollection collection: CollectionReference): KeyDataSource {
        return KeyRemoteDataSource(collection)
    }

    @Singleton
    @Provides
    fun provideUserDataSource(): UserDataSource {
        return UserRemoteDataSource(Firebase.auth)
    }
}