package io.github.namhyungu.keymap.data.source

import io.github.namhyungu.keymap.data.Key
import kotlinx.coroutines.flow.Flow

interface KeyDataSource {

    fun observeKeys(): Flow<List<Key>>

    suspend fun getKeys(): List<Key>

    fun observeKey(keyId: String): Flow<Key?>

    suspend fun getKey(keyId: String): Key?

    suspend fun saveKey(key: Key)

    suspend fun deleteKey(keyId: String)

}