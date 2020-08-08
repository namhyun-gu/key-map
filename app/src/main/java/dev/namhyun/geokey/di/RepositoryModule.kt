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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.namhyun.geokey.data.KeyDatabase
import dev.namhyun.geokey.data.LocationDataSource
import dev.namhyun.geokey.data.NetworkStateDataSource
import dev.namhyun.geokey.network.GeocodingClient
import dev.namhyun.geokey.repository.AddKeyRepository
import dev.namhyun.geokey.repository.DetailRepository
import dev.namhyun.geokey.repository.EditLocationRepository
import dev.namhyun.geokey.repository.MainRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideMainRepository(
        locationDataSource: LocationDataSource,
        networkStateDataSource: NetworkStateDataSource,
        geocodingClient: GeocodingClient,
        keyDatabase: KeyDatabase
    ): MainRepository {
        return MainRepository(
            locationDataSource,
            networkStateDataSource,
            geocodingClient,
            keyDatabase
        )
    }

    @Provides
    @ActivityRetainedScoped
    fun provideDetailRepository(
        keyDatabase: KeyDatabase
    ): DetailRepository {
        return DetailRepository(keyDatabase)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideAddKeyRepository(
        keyDatabase: KeyDatabase
    ): AddKeyRepository {
        return AddKeyRepository(keyDatabase)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideEditLocationRepository(
        geocodingClient: GeocodingClient
    ): EditLocationRepository {
        return EditLocationRepository(geocodingClient)
    }
}
