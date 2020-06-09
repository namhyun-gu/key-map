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
package dev.namhyun.geokey.ui.main

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationLiveData
import dev.namhyun.geokey.model.NetworkLiveData
import dev.namhyun.geokey.repository.MainRepository
import dev.namhyun.geokey.util.launchOnViewModelScope
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(
  application: Application,
  private val mainRepository: MainRepository
) : AndroidViewModel(application) {
    private val db = Firebase.firestore

    private val _locationData = LocationLiveData(application)
    val locationData = _locationData.switchMap {
        launchOnViewModelScope {
            mainRepository.reverseGeocoding(it.latitude, it.longitude) {
                Timber.e(it)
            }
        }
    }

    val networkData = NetworkLiveData(application)
    val keyData = MutableLiveData<List<Pair<String, Key>>>()
    val toastData = MutableLiveData<Int>()

    init {
        fetchKeys()
    }

    private fun fetchKeys() {
        db.collection("keys").addSnapshotListener { snapshot, e ->
            if (e != null) {
                toastData.value = R.string.msg_fetch_keys_error
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val ids = snapshot.documents.map { it.id }
                val keys = snapshot.toObjects(Key::class.java)
                keyData.value = ids.mapIndexed { idx, id -> id to keys[idx] }
            } else {
                keyData.value = emptyList()
            }
        }
    }

    fun deleteKey(id: String) {
        db.collection("keys").document(id).delete()
    }
}
