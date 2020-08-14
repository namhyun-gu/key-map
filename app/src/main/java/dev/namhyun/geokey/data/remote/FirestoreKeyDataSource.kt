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

import com.google.firebase.firestore.FirebaseFirestore
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class FirestoreKeyDataSource(
  val firestore: FirebaseFirestore
) : KeyDataSource {
    override suspend fun addKey(key: Key): String {
        return suspendCoroutine { cont ->
            firestore
                .collection(KEY_COLLECTION)
                .add(key)
                .addOnSuccessListener {
                    cont.resume(it.id)
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun getKey(id: String): Document<Key> {
        return suspendCoroutine { cont ->
            firestore
                .collection(KEY_COLLECTION)
                .document(id).get()
                .addOnSuccessListener {
                    cont.resume(Document(it.id, it.toObject(Key::class.java)!!))
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override fun getKeys(): Flow<List<Document<Key>>> {
        return callbackFlow {
            firestore
                .collection(KEY_COLLECTION)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        channel.offer(emptyList())
                    } else {
                        if (snapshot != null) {
                            val documents = snapshot.documents.map {
                                Document(it.id, it.toObject(Key::class.java)!!)
                            }
                            channel.offer(documents)
                        }
                    }
                }
            awaitClose { /* No-op */ }
        }
    }

    override suspend fun updateKey(id: String, key: Key): String {
        return suspendCoroutine { cont ->
            firestore
                .collection(KEY_COLLECTION)
                .document(id).set(key)
                .addOnSuccessListener {
                    cont.resume(id)
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun deleteKey(id: String): Boolean {
        return suspendCoroutine { cont ->
            firestore
                .collection(KEY_COLLECTION)
                .document(id).delete()
                .addOnSuccessListener {
                    cont.resume(true)
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    companion object {
        private const val KEY_COLLECTION = "keys"
    }
}
