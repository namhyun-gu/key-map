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
package dev.namhyun.geokey.domain.key

import dev.namhyun.geokey.di.IoDispatcher
import dev.namhyun.geokey.domain.UseCase
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.repository.KeyRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class UpdateKeyUseCase @Inject constructor(
    private val repository: KeyRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : UseCase<Document<Key>, Unit>(ioDispatcher) {

    override suspend fun execute(parameters: Document<Key>) {
        repository.updateKey(parameters.id, parameters.value)
    }
}
