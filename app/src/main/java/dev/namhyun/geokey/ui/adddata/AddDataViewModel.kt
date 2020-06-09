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
package dev.namhyun.geokey.ui.adddata

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.repository.AddDataRepository
import timber.log.Timber

class AddDataViewModel @ViewModelInject constructor(
  application: Application,
  private val addDataRepository: AddDataRepository
) : AndroidViewModel(application) {
    val toastData = MutableLiveData<Int>()
    val onSavedData = MutableLiveData<Boolean>()

    fun writeKey(location: LocationData, name: String, key: String) {
        val db = Firebase.firestore
        if (name.isBlank()) {
            toastData.value = R.string.msg_name_required
            return
        }
        if (key.isBlank()) {
            toastData.value = R.string.msg_key_required
            return
        }
        val data = Key(
            name = name,
            key = key,
            lat = location.lat,
            lon = location.lon,
            address = location.address
        )
        db.collection("keys")
            .add(data)
            .addOnSuccessListener {
                toastData.value = R.string.msg_key_saved
                onSavedData.value = true
            }
            .addOnFailureListener { e ->
                Timber.e(e)
                toastData.value = R.string.msg_key_not_saved
                onSavedData.value = false
            }
    }
}
