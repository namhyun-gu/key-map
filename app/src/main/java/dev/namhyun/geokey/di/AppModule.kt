/*
 * Copyright 2020 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.namhyun.geokey.di

import android.content.Context
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.namhyun.geokey.data.local.LocationDataSource
import dev.namhyun.geokey.data.local.LocationDataSourceImpl
import dev.namhyun.geokey.data.remote.FirestoreKeyDataSource
import dev.namhyun.geokey.data.remote.GeocodingService
import dev.namhyun.geokey.data.remote.KeyDataSource
import dev.namhyun.geokey.repository.KeyRepository
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

@InstallIn(ApplicationComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideKeyDataSource(): KeyDataSource {
        return FirestoreKeyDataSource(Firebase.firestore)
    }

    @Singleton
    @Provides
    fun provideKeyRepository(keyDataSource: KeyDataSource): KeyRepository {
        return KeyRepository(keyDataSource)
    }

    @Singleton
    @Provides
    fun provideLocationDataSource(@ApplicationContext context: Context): LocationDataSource {
        return LocationDataSourceImpl(context)
    }

    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideGeocodingService(): GeocodingService {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            classDiscriminator = "name"
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        return retrofit.create(GeocodingService::class.java)
    }
}
