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
import dev.namhyun.geokey.domain.key.DeleteKeyUseCase
import dev.namhyun.geokey.domain.key.GetKeyUseCase
import dev.namhyun.geokey.model.Document
import dev.namhyun.geokey.model.Key
import dev.namhyun.geokey.model.Result
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel @ViewModelInject constructor(
  private val getKeyUseCase: GetKeyUseCase,
  private val deleteKeyUseCase: DeleteKeyUseCase
) : ViewModel() {
    val key = MutableLiveData<Document<Key>>()

    fun readKey(id: String) = viewModelScope.launch {
        when (val result = getKeyUseCase(id)) {
            is Result.Success -> {
                key.postValue(result.data!!)
            }
            is Result.Error -> {
                Timber.e(result.exception)
            }
        }
    }

    fun deleteKey(id: String) = viewModelScope.launch {
        val result = deleteKeyUseCase(id)
        if (result is Result.Error) {
            Timber.e(result.exception)
        }
    }
}
