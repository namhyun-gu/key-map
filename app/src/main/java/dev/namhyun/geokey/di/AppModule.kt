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
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
import dev.namhyun.geokey.model.AdmCodeResult
import dev.namhyun.geokey.model.GeocodingResult
import dev.namhyun.geokey.model.LegalCodeResult
import dev.namhyun.geokey.model.OperationName
import dev.namhyun.geokey.model.RoadAddrResult
import dev.namhyun.geokey.repository.KeyRepository
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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

    @Singleton
    @Provides
    fun provideGeocodingService(): GeocodingService {
        val moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(GeocodingResult::class.java, "name")
                    .withSubtype(LegalCodeResult::class.java, OperationName.legalcode.name)
                    .withSubtype(AdmCodeResult::class.java, OperationName.admcode.name)
                    .withSubtype(RoadAddrResult::class.java, OperationName.roadaddr.name)
            )
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeocodingService::class.java)
    }
}
