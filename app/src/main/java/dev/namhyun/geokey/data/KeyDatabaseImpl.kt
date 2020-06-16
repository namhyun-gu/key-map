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
package dev.namhyun.geokey.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KeyDatabaseImpl : KeyDatabase {
    private val db = Firebase.firestore.collection("keys")

    override suspend fun createKey(key: Key): String {
        return suspendCoroutine { cont ->
            db.add(key)
                .addOnSuccessListener {
                    cont.resume(it.id)
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun readKey(id: String): Resource<Document<Key>> {
        return suspendCoroutine { cont ->
            db.document(id).get()
                .addOnSuccessListener {
                    cont.resume(Resource.Success(Document(it.id, it.toObject(Key::class.java)!!)))
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override fun readAllKey(): LiveData<Resource<List<Document<Key>>>> {
        val liveData = MutableLiveData<Resource<List<Document<Key>>>>()
        db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                liveData.value = Resource.Error(e)
            } else {
                if (snapshot != null) {
                    val documents = snapshot.documents.map {
                        Document(it.id, it.toObject(Key::class.java)!!)
                    }
                    liveData.value = Resource.Success(documents)
                }
            }
        }
        return liveData
    }

    override suspend fun updateKey(id: String, key: Key): String {
        return suspendCoroutine { cont ->
            db.document(id).set(key)
                .addOnSuccessListener {
                    cont.resume(id)
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }

    override suspend fun deleteKey(id: String): Boolean {
        return suspendCoroutine { cont ->
            db.document(id).delete()
                .addOnSuccessListener {
                    cont.resume(true)
                }.addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
    }
}
