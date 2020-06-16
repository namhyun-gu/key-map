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
import androidx.lifecycle.viewModelScope
import dev.namhyun.geokey.R
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.model.Resource
import dev.namhyun.geokey.repository.AddDataRepository
import kotlinx.coroutines.launch

class AddDataViewModel @ViewModelInject constructor(
  application: Application,
  private val addDataRepository: AddDataRepository
) : AndroidViewModel(application) {
    val keyData = MutableLiveData<Resource<Document<Key>>>()
    val toastData = MutableLiveData<Int>()
    val onSavedData = MutableLiveData<Boolean>()

    fun readKey(id: String) {
        viewModelScope.launch {
            keyData.postValue(addDataRepository.readKey(id))
        }
    }

    fun createKey(name: String, key: String, location: LocationData) {
        validData(name, key)
        val keyData = Key(name = name, key = key, locationData = location)
        viewModelScope.launch {
            addDataRepository.createKey(keyData)
            toastData.value = R.string.msg_key_saved
            onSavedData.value = true
        }
    }

    fun updateKey(id: String, name: String, key: String, location: LocationData) {
        validData(name, key)
        val keyData = Key(name = name, key = key, locationData = location)
        viewModelScope.launch {
            val updatedId = addDataRepository.updateKey(id, keyData)
            if (id == updatedId) {
                toastData.value = R.string.msg_key_saved
                onSavedData.value = true
            }
        }
    }

    private fun validData(name: String, key: String): Boolean {
        if (name.isBlank()) {
            toastData.value = R.string.msg_name_required
            return false
        }
        if (key.isBlank()) {
            toastData.value = R.string.msg_key_required
            return false
        }
        return true
    }
}
