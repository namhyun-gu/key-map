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