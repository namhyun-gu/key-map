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

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.namhyun.geokey.model.AdmCodeResult
import dev.namhyun.geokey.model.LegalCodeResult
import dev.namhyun.geokey.model.OperationName
import dev.namhyun.geokey.model.Result
import dev.namhyun.geokey.model.RoadAddrResult
import dev.namhyun.geokey.network.GeocodingClient
import dev.namhyun.geokey.network.GeocodingService
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingRetrofit

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingMoshi(): Moshi {
        return Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(Result::class.java, "name")
                    .withSubtype(LegalCodeResult::class.java, OperationName.legalcode.name)
                    .withSubtype(AdmCodeResult::class.java, OperationName.admcode.name)
                    .withSubtype(RoadAddrResult::class.java, OperationName.roadaddr.name)
            )
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    @GeocodingRetrofit
    fun provideGeocodingRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingService(@GeocodingRetrofit retrofit: Retrofit): GeocodingService {
        return retrofit.create(GeocodingService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingClient(geocodingService: GeocodingService): GeocodingClient {
        return GeocodingClient(geocodingService)
    }
}
