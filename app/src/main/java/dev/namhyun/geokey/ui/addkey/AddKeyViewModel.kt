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
package dev.namhyun.geokey.ui.addkey

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.repository.AddKeyRepository
import kotlinx.coroutines.launch

class AddKeyViewModel @ViewModelInject constructor(
  private val addKeyRepository: AddKeyRepository
) : ViewModel() {

    val addKeyFormData = MutableLiveData<AddKeyFormState>(AddKeyFormState.EmptyData)

    fun createKey(name: String, key: String, location: LocationData) {
        val invalidItems = mutableListOf<String>()
        if (name.trim().isEmpty()) {
            invalidItems.add("name")
        }
        if (key.trim().isEmpty()) {
            invalidItems.add("key")
        }
        if (invalidItems.isNotEmpty()) {
            addKeyFormData.value = AddKeyFormState.InvalidData(invalidItems)
            return
        }
        val keyData = Key(name = name, key = key, locationData = location)
        viewModelScope.launch {
            addKeyRepository.createKey(keyData)
            addKeyFormData.value = AddKeyFormState.ValidData
        }
    }

    fun updateKey(id: String, name: String, key: String, location: LocationData) {
        val invalidItems = mutableListOf<String>()
        if (name.trim().isEmpty()) {
            invalidItems.add("name")
        }
        if (key.trim().isEmpty()) {
            invalidItems.add("key")
        }
        if (invalidItems.isNotEmpty()) {
            addKeyFormData.value = AddKeyFormState.InvalidData(invalidItems)
            return
        }
        val keyData = Key(name = name, key = key, locationData = location)
        viewModelScope.launch {
            addKeyRepository.updateKey(id, keyData)
            addKeyFormData.value = AddKeyFormState.ValidData
        }
    }
}
