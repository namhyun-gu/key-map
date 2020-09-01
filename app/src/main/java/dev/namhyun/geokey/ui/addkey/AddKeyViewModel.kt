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
import dev.namhyun.geokey.domain.key.AddKeyUseCase
import dev.namhyun.geokey.domain.key.UpdateKeyUseCase
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.LocationModel
import dev.namhyun.geokey.model.Result
import kotlinx.coroutines.launch
import timber.log.Timber

class AddKeyViewModel @ViewModelInject constructor(
    private val addKeyUseCase: AddKeyUseCase,
    private val updateKeyUseCase: UpdateKeyUseCase
) : ViewModel() {

    val formState = MutableLiveData<AddKeyFormState>(AddKeyFormState.EmptyData)

    fun saveKey(id: String?, name: String, key: String, location: LocationModel) {
        if (!validForm(name, key)) {
            return
        }

        viewModelScope.launch {
            val keyData = Key(name, key, location)
            val result = if (id == null) {
                addKeyUseCase(keyData)
            } else {
                updateKeyUseCase(Document(id, keyData))
            }
            when (result) {
                is Result.Success -> {
                    formState.value = AddKeyFormState.ValidData
                }
                is Result.Error -> {
                    Timber.e(result.exception)
                }
            }
        }
    }

    private fun validForm(name: String, key: String): Boolean {
        val invalidItems = mutableListOf<String>()
        if (name.trim().isEmpty()) {
            invalidItems.add("name")
        }
        if (key.trim().isEmpty()) {
            invalidItems.add("key")
        }
        if (invalidItems.isNotEmpty()) {
            formState.value = AddKeyFormState.InvalidData(invalidItems)
            return false
        }
        return true
    }
}
