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
package dev.namhyun.geokey.repository

import dev.namhyun.geokey.data.remote.KeyDataSource
import dev.namhyun.geokey.model.Key
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyRepository @Inject constructor(
    private val keyDataSource: KeyDataSource
) {

    suspend fun addKey(key: Key) = keyDataSource.addKey(key)

    suspend fun getKey(id: String) = keyDataSource.getKey(id)

    fun getKeys() = keyDataSource.getKeys()

    suspend fun updateKey(id: String, key: Key) = keyDataSource.updateKey(id, key)

    suspend fun deleteKey(id: String) = keyDataSource.deleteKey(id)
}
