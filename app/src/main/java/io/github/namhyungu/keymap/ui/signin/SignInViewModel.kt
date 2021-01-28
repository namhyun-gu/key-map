package io.github.namhyungu.keymap.ui.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.namhyungu.keymap.data.source.UserDataSource
import io.github.namhyungu.keymap.util.Event
import kotlinx.coroutines.launch

class SignInViewModel @ViewModelInject constructor(
    private val userDataSource: UserDataSource,
) : ViewModel() {
    private val _uiState = MutableLiveData(SignInUiState())
    val uiState: LiveData<SignInUiState> = _uiState

    private val _signInFailedEvent = MutableLiveData<Event<Unit>>()
    val signInFailedEvent: LiveData<Event<Unit>> = _signInFailedEvent

    private val _signInSuccess = MutableLiveData<Event<Unit>>()
    val signInSuccess: LiveData<Event<Unit>> = _signInSuccess

    fun startSignIn() {
        _uiState.value = _uiState.value!!.copy(
            isRequestSignIn = true
        )
    }

    fun stopSignIn() {
        _uiState.value = _uiState.value!!.copy(
            isRequestSignIn = false
        )
    }

    fun signIn(token: String) {
        viewModelScope.launch {
            val result = userDataSource.signIn(token)
            if (!result) {
                signInFailed()
            } else {
                signInSuccess()
            }
            stopSignIn()
        }
    }

    fun signInSuccess() {
        _signInSuccess.value = Event(Unit)
    }

    fun signInFailed() {
        _signInFailedEvent.value = Event(Unit)
    }
}