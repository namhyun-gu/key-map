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
package dev.namhyun.geokey.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationData
import dev.namhyun.geokey.model.Resource
import dev.namhyun.geokey.repository.DetailRepository
import kotlinx.coroutines.launch

class DetailViewModel @ViewModelInject constructor(
  private val detailRepository: DetailRepository
) : ViewModel() {
    val keyData = MutableLiveData<Document<Key>>()
    val editKeyFormData = MutableLiveData<EditKeyFormState>(EmptyData)

    fun readKey(id: String) = viewModelScope.launch {
        when (val resource = detailRepository.readKey(id)) {
            is Resource.Success -> {
                keyData.postValue(resource.data)
            }
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
            editKeyFormData.value = InvalidData(invalidItems)
            return
        }
        val keyData = Key(name = name, key = key, locationData = location)
        viewModelScope.launch {
            detailRepository.updateKey(id, keyData)
            editKeyFormData.value = ValidData
        }
    }

    fun deleteKey(id: String) = viewModelScope.launch {
        detailRepository.deleteKey(id)
    }

    fun resetForm() {
        editKeyFormData.value = EmptyData
    }
}
