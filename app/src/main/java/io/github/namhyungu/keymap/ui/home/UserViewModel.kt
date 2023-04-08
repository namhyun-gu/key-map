package io.github.namhyungu.keymap.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.namhyungu.keymap.data.User
import io.github.namhyungu.keymap.data.data
import io.github.namhyungu.keymap.domain.signin.GetUserUseCase
import io.github.namhyungu.keymap.domain.signin.SignInUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserUiState {

    object NotSignIn : UserUiState

    object Process : UserUiState

    data class SignIn(val user: User) : UserUiState

}

sealed interface UserEvent {
    object SignIn : UserEvent
}

fun User?.toUiState(): UserUiState {
    return if (this != null) {
        UserUiState.SignIn(this)
    } else {
        UserUiState.NotSignIn
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    val getUserUseCase: GetUserUseCase,
    val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _user: MutableStateFlow<UserUiState> = MutableStateFlow(UserUiState.NotSignIn)
    val user: StateFlow<UserUiState>
        get() = _user.asStateFlow()

    private val _event: MutableSharedFlow<UserEvent> = MutableSharedFlow()
    val event: SharedFlow<UserEvent>
        get() = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            getUser()
        }
    }

    fun startSignIn() = viewModelScope.launch {
        _user.emit(UserUiState.Process)
    }

    fun signIn(token: String?) = viewModelScope.launch {
        signInUseCase(token)
        getUser()
        _event.emit(UserEvent.SignIn)
    }

    private suspend fun getUser() {
        _user.emit(getUserUseCase(Unit).data.toUiState())
    }
}