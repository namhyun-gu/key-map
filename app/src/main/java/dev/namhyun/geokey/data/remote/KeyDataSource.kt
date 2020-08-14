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
package dev.namhyun.geokey.data.remote

import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import kotlinx.coroutines.flow.Flow

interface KeyDataSource {

    suspend fun addKey(key: Key): String

    suspend fun getKey(id: String): Document<Key>

    fun getKeys(): Flow<List<Document<Key>>>

    suspend fun updateKey(id: String, key: Key): String

    suspend fun deleteKey(id: String): Boolean
}
