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
package dev.namhyun.geokey.domain.location

import dev.namhyun.geokey.data.local.LocationDataSource
import dev.namhyun.geokey.di.MainDispatcher
import dev.namhyun.geokey.domain.FlowUseCase
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.model.Result
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
class GetLocationsUseCase @Inject constructor(
    private val locationDataSource: LocationDataSource,
    @MainDispatcher mainDispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, LocationModel>(mainDispatcher) {
    override fun execute(parameters: Unit): Flow<Result<LocationModel>> {
        return flow {
            emit(Result.Success(locationDataSource.getLastLocation()))
            locationDataSource.getLocationUpdates().collect {
                emit(Result.Success(it))
            }
        }
    }
}
