package io.github.namhyungu.keymap.data.source.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import io.github.namhyungu.keymap.data.Key
import io.github.namhyungu.keymap.data.source.KeyDataSource
import io.github.namhyungu.keymap.util.asFlow
import io.github.namhyungu.keymap.util.await
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class KeyRemoteDataSource internal constructor(
    private val ref: CollectionReference,
) : KeyDataSource {
    override fun observeKeys(): Flow<List<Key>> {
        return ref.asFlow().map { it?.toObjects() ?: emptyList() }
    }

    override suspend fun getKeys(): List<Key> {
        return ref.get().await()?.toObjects() ?: emptyList()
    }

    override fun observeKey(keyId: String): Flow<Key?> {
        return ref.document(keyId).asFlow().map { it?.toObject() }
    }

    override suspend fun getKey(keyId: String): Key? {
        return ref.document(keyId).get().await()?.toObject()
    }

    override suspend fun saveKey(key: Key) {
        if (key.id.isEmpty()) {
            val newId = ref.document().id
            ref.document(newId).set(key.copy(id = newId))
        } else {
            ref.document(key.id).set(key).await()
        }
    }

    override suspend fun deleteKey(keyId: String) {
        ref.document(keyId).delete().await()
    }
}